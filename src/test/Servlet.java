package test;

import java.io.IOException;
import java.io.OutputStream;

import test.RequestParser.RequestInfo;

/**
 * Defines the functionality for handling HTTP requests.
 */
public interface Servlet {

    /**
     * Handles an HTTP request.
     *
     * @param ri the parsed request information
     * @param toClient the output stream to write the response to
     * @throws IOException if an I/O error occurs
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Closes the servlet and releases any resources.
     *
     * @throws IOException if an I/O error occurs
     */
    void close() throws IOException;
}