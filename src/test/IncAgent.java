package test;


/**
 * IncAgent subscribes to one topic, increments the value, and publishes the result.
 */
public class IncAgent implements Agent {
    private final String name;
    private final String[] subs;
    private final String[] pubs;

    public IncAgent(String[] subs, String[] pubs) {
        this.name = "IncAgent";
        this.subs = subs;
        this.pubs = pubs;

        // Subscribe to the first topic in subs
        TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(subs[0])) {
            double value = msg.asDouble;
            if (!Double.isNaN(value)) {
                double result = value + 1;
                Message resultMessage = new Message(result);
                TopicManagerSingleton.get().getTopic(pubs[0]).publish(resultMessage);
            }
        }
    }

    @Override
    public void close() {
    }
}
