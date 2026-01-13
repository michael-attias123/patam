package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a topic that allows agents to subscribe, unsubscribe,
 * publish messages, and manage publishers.
 */
public class Topic {
    public final String name; // The name of the topic
    private final List<Agent> subs; // List of subscribers (agents listening to this topic)
    private final List<Agent> pubs; // List of publishers (agents publishing to this topic)

    // Constructor with package-private access
    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>(); // Initialize the subscribers list
        this.pubs = new ArrayList<>(); // Initialize the publishers list
    }

    /**
     * Subscribes an agent to this topic.
     * 
     * @param a the agent to subscribe.
     */
    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a); // Add the agent to the subscribers list
        }
    }

    /**
     * Unsubscribes an agent from this topic.
     * 
     * @param a the agent to unsubscribe.
     */
    public void unsubscribe(Agent a) {
        subs.remove(a); // Remove the agent from the subscribers list
    }

    /**
     * Publishes a message to all subscribed agents.
     * 
     * @param m the message to publish.
     */
    public void publish(Message m) {
        for (Agent a : subs) {
            a.callback(name, m); // Notify each subscriber with the message
        }
    }

    /**
     * Adds an agent as a publisher to this topic.
     * 
     * @param a the agent to add as a publisher.
     */
    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a); // Add the agent to the publishers list
        }
    }

    /**
     * Removes an agent as a publisher from this topic.
     * 
     * @param a the agent to remove as a publisher.
     */
    public void removePublisher(Agent a) {
        pubs.remove(a); // Remove the agent from the publishers list
    }
}
