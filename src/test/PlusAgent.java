package test;


/**
 * PlusAgent subscribes to two topics, adds their values, and publishes the result.
 */
public class PlusAgent implements Agent {
    private final String name;
    private final String[] subs;
    private final String[] pubs;
    private double x = 0.0; // Default value for x
    private double y = 0.0; // Default value for y

    public PlusAgent(String[] subs, String[] pubs) {
        this.name = "PlusAgent";
        this.subs = subs;
        this.pubs = pubs;

        // Subscribe to the first two topics in subs
        TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);
        TopicManagerSingleton.get().getTopic(subs[1]).subscribe(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        x = 0.0;
        y = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(subs[0])) {
            x = msg.asDouble;
        } else if (topic.equals(subs[1])) {
            y = msg.asDouble;
        }

        if (!Double.isNaN(x) && !Double.isNaN(y)) {
            double result = x + y;
            Message resultMessage = new Message(result);
            TopicManagerSingleton.get().getTopic(pubs[0]).publish(resultMessage);
        }
    }

    @Override
    public void close() {
    }
}
