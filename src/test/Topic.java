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
        if (!subs.contains(a)) subs.add(a);
    }

    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    public void publish(Message m) {
        for (Agent a : subs) {
            a.callback(name, m);
        }
    }

    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) pubs.add(a);
    }

    public void removePublisher(Agent a) {
        pubs.remove(a);
    }

    // Expose subscribers/publishers for Graph construction (defensive unmodifiable copies)
    public List<Agent> getSubscribers() {
        return Collections.unmodifiableList(new ArrayList<>(subs));
    }

    public List<Agent> getPublishers() {
        return Collections.unmodifiableList(new ArrayList<>(pubs));
    }
}
