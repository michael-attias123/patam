package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import test.RequestParser.RequestInfo;


public class MainTrain { // RequestParser
    

    private static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                            "Host: example.com\n" +
                            "Content-Length: 5\n"+
                            "\n" +
                            "filename=\"hello_world.txt\"\n"+
                            "\n" +
                            "hello world!\n"+
                            "\n" ;

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            } 
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            } 
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }        
    }


    public static void testServer() throws Exception{
        int port = 12345;

        // baseline thread count
        int beforeThreads = Thread.activeCount();

        MyHTTPServer server = new MyHTTPServer(port, 3);

        // simple servlet that adds parameters a and b and returns the sum
        Servlet addServlet = new Servlet() {
            @Override
            public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
                Map<String,String> params = ri.getParameters();
                double a = 0, b = 0;
                try { if (params.containsKey("a")) a = Double.parseDouble(params.get("a")); } catch(Exception e) {}
                try { if (params.containsKey("b")) b = Double.parseDouble(params.get("b")); } catch(Exception e) {}
                double sum = a + b;
                String body = Double.toString(sum) + "\n";
                byte[] bodyBytes = body.getBytes();
                String headers = "HTTP/1.1 200 OK\r\nContent-Length: " + bodyBytes.length + "\r\n\r\n";
                toClient.write(headers.getBytes());
                toClient.write(bodyBytes);
            }

            @Override
            public void close() throws IOException { }
        };

        server.addServlet("GET", "/add", addServlet);
        server.start();

        // give server time to start
        Thread.sleep(200);

        int afterStartThreads = Thread.activeCount();
        if (afterStartThreads - beforeThreads != 1) {
            System.out.println("Server thread count test failed (-10)");
            throw new Exception("Server thread count test failed");
        } else {
            System.out.println("Server thread count test passed");
        }

        // create client socket and send request
        try (Socket sock = new Socket("localhost", port)) {
            OutputStream out = sock.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            String req = "GET /add?a=2&b=3 HTTP/1.1\r\nHost: localhost\r\n\r\n";
            out.write(req.getBytes());
            out.flush();

            // read response: status line
            String statusLine = in.readLine();
            if (statusLine == null) {
                System.out.println("Server functional test failed (-20) - no status line");
            } else {
                // read headers
                Map<String,String> respHeaders = new HashMap<>();
                String headerLine;
                while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                    String[] hp = headerLine.split(": ", 2);
                    if (hp.length == 2) respHeaders.put(hp[0], hp[1]);
                }

                int contentLen = 0;
                if (respHeaders.containsKey("Content-Length")) {
                    try { contentLen = Integer.parseInt(respHeaders.get("Content-Length")); } catch(Exception e) { contentLen = 0; }
                }

                // read body characters exactly contentLen using the same BufferedReader
                char[] cbuf = new char[contentLen];
                int read = 0;
                while (read < contentLen) {
                    int r = in.read(cbuf, read, contentLen - read);
                    if (r == -1) break;
                    read += r;
                }
                String bodyStr = new String(cbuf, 0, Math.max(0, read));
                if (!bodyStr.contains("5.0") && !bodyStr.contains("5")) {
                    System.out.println("Server functional test failed (-20)");
                    throw new Exception("Server functional test failed");
                } else {
                    System.out.println("Server functional test passed");
                }
            }
        }

        // shutdown
        server.close();
        // wait for server thread to stop
        Thread.sleep(2000);
        int afterCloseThreads = Thread.activeCount();
        if (afterCloseThreads > beforeThreads) {
            System.out.println("Server threads did not shut down (-10)");
            throw new Exception("Server threads did not shut down");
        } else {
            System.out.println("Server shutdown test passed");
        }
    }
    
    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try{
            testServer(); // 60
        }catch(Exception e){
            System.out.println("your server throwed an exception (-60)");
        }
        System.out.println("done");
    }

}
