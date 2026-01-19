package test;

/**
 * Config interface for creating and managing configurations.
 */
public interface Config {
    void create(); 
    String getName(); 
    int getVersion(); 
    void close(); 
}