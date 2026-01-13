package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Node representing a vertex in the directed computational graph.
 * Fields: name, outgoing edges, optional message.
 */
public class Node {
    private String name;
    private List<Node> edges;
    private Message message;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
        this.message = null;
    }

    // getters / setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Node> getEdges() { return edges; }
    public void setEdges(List<Node> edges) { this.edges = edges; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }

    // add an outgoing edge (no duplicates)
    public void addEdge(Node node) {
        if (node == null) return;
        if (!edges.contains(node)) edges.add(node);
    }

    // public entry: check whether this node belongs to a component that contains a cycle
    public boolean hasCycles() {
        return hasCycles(new HashSet<>(), new HashSet<>());
    }

    // DFS with recursion stack; visited marks fully-processed nodes only after children processed
    private boolean hasCycles(Set<Node> visited, Set<Node> stack) {
        if (stack.contains(this)) {
            return true; // back edge -> cycle
        }
        if (visited.contains(this)) {
            return false; // already fully processed -> no cycle via this node
        }

        stack.add(this);
        for (Node child : edges) {
            if (child.hasCycles(visited, stack)) {
                return true;
            }
        }
        stack.remove(this);
        visited.add(this); // mark fully processed
        return false;
    }

    // equality/hash based on name so nodes representing same entity are identified
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Node{" + name + "}";
    }
}