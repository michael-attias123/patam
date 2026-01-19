package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * GenericConfig dynamically creates agents based on a configuration file.
 */
public class GenericConfig implements Config {
    private String confFile; // Path to the configuration file
    private final List<Agent> agents; // List of created agents

    public GenericConfig() {
        this.agents = new ArrayList<>();
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    @Override
    public void create() {
        try (BufferedReader reader = new BufferedReader(new FileReader(confFile))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            if (lines.size() % 3 != 0) {
                throw new IllegalArgumentException("Invalid configuration file format");
            }

            for (int i = 0; i < lines.size(); i += 3) {
                String className = lines.get(i);
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");

                // Use reflection to create an agent instance
                Class<?> agentClass = Class.forName(className);
                Constructor<?> constructor = agentClass.getConstructor(String[].class, String[].class);
                Agent agent = (Agent) constructor.newInstance((Object) subs, (Object) pubs);

                // Wrap the agent in a ParallelAgent (decorator)
                Agent parallelAgent = new ParallelAgent(agent);
                agents.add(parallelAgent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        for (Agent agent : agents) {
            agent.close();
        }
    }
}
