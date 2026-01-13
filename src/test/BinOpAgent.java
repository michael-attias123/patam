package test;

import java.util.function.BinaryOperator;

/**
 * Agent that performs a binary operation on two input topics and publishes to an output topic.
 * Registers as subscriber to input topics and as publisher to the output topic.
 */
public class BinOpAgent implements Agent {
    private final String name;
    private final String input1;
    private final String input2;
    private final String output;
    private final BinaryOperator<Double> op;

    // track latest inputs (Double to allow NaN detection)
    private double val1 = Double.NaN;
    private double val2 = Double.NaN;

    public BinOpAgent(String name, String input1, String input2, String output, BinaryOperator<Double> op) {
        this.name = name;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.op = op;

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        // subscribe to inputs
        tm.getTopic(input1).subscribe(this);
        tm.getTopic(input2).subscribe(this);
        // register as publisher to output so Graph can see agent -> topic edges
        tm.getTopic(output).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        val1 = Double.NaN;
        val2 = Double.NaN;
    }

    @Override
    public void callback(String topic, Message msg) {
        // update the appropriate input
        if (topic.equals(input1)) {
            val1 = msg.asDouble;
        } else if (topic.equals(input2)) {
            val2 = msg.asDouble;
        }

        // if both inputs are numeric, compute and publish
        if (!Double.isNaN(val1) && !Double.isNaN(val2)) {
            double result = op.apply(val1, val2);
            Message out = new Message(result);
            TopicManagerSingleton.get().getTopic(output).publish(out);
        }
    }

    @Override
    public void close() {
        // no resources to free in this simple implementation
    }
}
