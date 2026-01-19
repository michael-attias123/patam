package test;

/**
 * ParallelAgent is a decorator that wraps an Agent and delegates calls to it.
 */
public class ParallelAgent implements Agent {
    private final Agent wrappedAgent;

    public ParallelAgent(Agent agent) {
        this.wrappedAgent = agent;
    }

    @Override
    public void reset() {
        wrappedAgent.reset();
    }

    @Override
    public void callback(String topic, Message message) {
        wrappedAgent.callback(topic, message);
    }

    @Override
    public String getName() {
        return wrappedAgent.getName();
    }

    @Override
    public void close() {
        wrappedAgent.close();
    }
}
