package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Graph is a list of Nodes. createFromTopics builds the graph from TopicManager:
 * - Topic node "T<name>" with edges to Agent nodes "A<agentName>" for subscribers
 * - Agent node "A<name>" with edges to Topic nodes "T<topicName>" for publishers
 */
public class Graph extends ArrayList<Node> {

    public Graph() { super(); }

    // check entire graph for cycles
    public boolean hasCycles() {
        for (Node n : this) if (n.hasCycles()) return true;
        return false;
    }

    // build the graph from the singleton TopicManager (no arguments)
    public void createFromTopics() {
        this.clear();

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        if (tm == null) return;

        Map<String, Node> nodes = new HashMap<>(); // nodeName -> Node

        // helper to get-or-create a node by name (declared first)
        java.util.function.Function<String, Node> getNode = (name) -> {
            Node existing = nodes.get(name);
            if (existing == null) {
                existing = new Node(name);
                nodes.put(name, existing);
            }
            return existing;
        };

        Collection<Topic> topics = tm.getTopics();

        // topic -> subscriber agent edges
        for (Topic t : topics) {
            String tName = "T" + t.name;
            Node tNode = getNode.apply(tName);
            for (Agent sub : t.getSubscribers()) {
                String aName = "A" + sub.getName();
                Node aNode = getNode.apply(aName);
                tNode.addEdge(aNode);
            }
        }

        // agent -> topic (for publishers)
        for (Topic t : topics) {
            String tName = "T" + t.name;
            Node tNode = getNode.apply(tName);
            for (Agent pub : t.getPublishers()) {
                String aName = "A" + pub.getName();
                Node aNode = getNode.apply(aName);
                aNode.addEdge(tNode);
            }
        }

        this.addAll(nodes.values());
    }
}
