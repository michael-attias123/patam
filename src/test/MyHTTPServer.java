package test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the HTTPServer interface to handle HTTP requests sequentially.
 */
public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final int nThreads;
    private final Map<String, Map<String, Servlet>> servlets;
    private boolean running;

    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
        this.servlets = new HashMap<>();
        this.running = false;
    }

    @Override
    public void addServlet(String httpCommand, String uri, Servlet servlet) {
        servlets.computeIfAbsent(httpCommand, k -> new HashMap<>()).put(uri, servlet);
    }

    @Override
    public void removeServlet(String httpCommand, String uri) {
        Map<String, Servlet> commandMap = servlets.get(httpCommand);
        if (commandMap != null) {
            commandMap.remove(uri);
        }
    }

    @Override
    public void start() {
        running = true;
        super.start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // set short timeout so accept() can periodically wake and check `running`
            serverSocket.setSoTimeout(1000);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                } catch (java.net.SocketTimeoutException ste) {
                    // timeout used to re-check running flag
                    continue;
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream writer = clientSocket.getOutputStream()) {

            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(reader);
            Servlet servlet = findServlet(requestInfo.method, requestInfo.path);

            if (servlet != null) {
                servlet.handle(requestInfo, writer);
            } else {
                writer.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Servlet findServlet(String method, String path) {
        Map<String, Servlet> commandMap = servlets.get(method);
        if (commandMap == null) {
            return null;
        }

        return commandMap.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .max((e1, e2) -> Integer.compare(e1.getKey().length(), e2.getKey().length()))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    @Override
    public void close() {
        running = false;
    }
}