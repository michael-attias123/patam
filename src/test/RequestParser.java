package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses HTTP requests and returns a RequestInfo object.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from a BufferedReader.
     *
     * @param reader the BufferedReader to read the request from
     * @return a RequestInfo object containing the parsed request data
     * @throws IOException if an I/O error occurs
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null || line.isEmpty()) {
            throw new IOException("Empty request");
        }

        // Parse the request line
        String[] requestLineParts = line.split(" ", 3);
        if (requestLineParts.length < 3) {
            throw new IOException("Invalid request line");
        }
        String method = requestLineParts[0];
        String uri = requestLineParts[1];
        String[] uriParts = uri.split("\\?");
        String path = uriParts[0];
        String[] pathParts = path.startsWith("/") ? path.substring(1).split("/") : path.split("/");
        Map<String, String> queryParams = new HashMap<>();

        // Parse query parameters from URI
        if (uriParts.length > 1) {
            String[] params = uriParts[1].split("&");
            for (String param : params) {
                if (param.isEmpty()) continue;
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                } else {
                    queryParams.put(keyValue[0], ""); // Handle keys without values
                }
            }
        }

        // Parse headers
        Map<String, String> headers = new HashMap<>();
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // After headers: there may be additional parameter lines (e.g. filename=...) until a blank line
        if (reader.ready()) {
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                // expect lines like key=value
                String[] kv = line.split("=", 2);
                if (kv.length == 2) {
                    queryParams.put(kv[0], kv[1]);
                }
            }
        }

        // Read content if available (do not block): read until blank line or EOF
        byte[] content;
        if (reader.ready()) {
            StringBuilder contentBuilder = new StringBuilder();
            String contentLine;
            while ((contentLine = reader.readLine()) != null && !contentLine.isEmpty()) {
                contentBuilder.append(contentLine).append("\n");
            }
            content = contentBuilder.length() > 0 ? contentBuilder.toString().getBytes() : new byte[0];
        } else {
            content = new byte[0];
        }

        return new RequestInfo(method, uri, path, pathParts, queryParams, headers, content);
    }

    /**
     * Represents parsed HTTP request information.
     */
    public static class RequestInfo {
        public final String method;
        public final String uri;
        public final String path;
        public final String[] pathParts;
        public final Map<String, String> queryParams;
        public final Map<String, String> headers;
        public final byte[] content;

        public RequestInfo(String method, String uri, String path, String[] pathParts, Map<String, String> queryParams, Map<String, String> headers, byte[] content) {
            this.method = method;
            this.uri = uri;
            this.path = path;
            this.pathParts = pathParts;
            this.queryParams = queryParams;
            this.headers = headers;
            this.content = content;
        }

        /**
         * Returns the HTTP command (method).
         *
         * @return the HTTP command
         */
        public String getHttpCommand() {
            return method;
        }

        /**
         * Returns the URI of the request.
         *
         * @return the URI
         */
        public String getUri() {
            return uri;
        }

        /**
         * Returns the URI segments as an array of strings.
         *
         * @return the URI segments
         */
        public String[] getUriSegments() {
            return pathParts;
        }

        /**
         * Returns the query parameters as a map.
         *
         * @return the query parameters
         */
        public Map<String, String> getParameters() {
            return queryParams;
        }

        /**
         * Returns the content of the request as a byte array.
         *
         * @return the content
         */
        public byte[] getContent() {
            return content;
        }
    }
}
