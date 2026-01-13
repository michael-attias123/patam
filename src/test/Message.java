package test;

import java.util.Date;

public class Message {
    // Immutable fields
    public final byte[] data; // Content of the message
    public final String asText; // Conversion to String
    public final double asDouble; // Conversion to double
    public final Date date; // Timestamp of message creation

    // Constructor for byte array input
    public Message(byte[] data) {
        this.data = data;
        this.asText = new String(data); // Convert bytes to String
        this.asDouble = parseDouble(this.asText); // Convert String to double
        this.date = new Date(); // Record creation time
    }

    // Constructor for String input
    public Message(String text) {
        this(text.getBytes()); // Reuse the byte array constructor
    }

    // Constructor for double input
    public Message(double value) {
        this(Double.toString(value)); // Reuse the String constructor
    }

    // Helper method to safely parse a String to double
    private double parseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return Double.NaN; // Return NaN if conversion fails
        }
    }
}