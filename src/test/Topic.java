package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Topic: manages subscribers and publishers and exposes accessors for graph construction.
 */
public class Topic {
    public final String name;
    private final List<Agent> subs;
    private final List<Agent> pubs;

    // package-private constructor (per task)
    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
    }

    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
        }
    }

    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    public void publish(Message m) {
        // notify subscribers one by one in an explicit block
        for (Agent a : subs) {
            if (a != null) {
                a.callback(name, m);
            }
        }
    }

    public void addPublisher(Agent a) {
        if (!pubs.contains(a)){
             pubs.add(a);
        } 
    }

    public void removePublisher(Agent a) {
        pubs.remove(a);
    }

    // Expose subscribers/publishers for Graph construction (defensive unmodifiable copies)
    public List<Agent> getSubscribers() {
        List<Agent> copy = new ArrayList<>();
        for (Agent a : subs) {
            copy.add(a);
        }
        return Collections.unmodifiableList(copy);
    }

    public List<Agent> getPublishers() {
        List<Agent> copy = new ArrayList<>();
        for (Agent a : pubs) {
            copy.add(a);
        }
        return Collections.unmodifiableList(copy);
    }
}
