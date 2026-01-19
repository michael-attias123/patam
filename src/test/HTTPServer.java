package test;

/**
 * Defines the structure of an HTTP server.
 */
public interface HTTPServer extends Runnable {

    /**
     * Adds a servlet to handle requests for a specific HTTP command and URI.
     *
     * @param httpCommand the HTTP command (e.g., GET, POST)
     * @param uri the URI to handle
     * @param servlet the servlet to handle the requests
     */
    void addServlet(String httpCommand, String uri, test.Servlet servlet);

    /**
     * Removes a servlet for a specific HTTP command and URI.
     *
     * @param httpCommand the HTTP command (e.g., GET, POST)
     * @param uri the URI to remove the servlet for
     */
    void removeServlet(String httpCommand, String uri);

    /**
     * Starts the HTTP server.
     */
    void start();

    /**
     * Closes the HTTP server and releases resources.
     */
    void close();
}