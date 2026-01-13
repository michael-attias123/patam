package test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A singleton class that provides access to the single instance of TopicManager.
 */
public class TopicManagerSingleton {

    // Private constructor to prevent instantiation
    private TopicManagerSingleton() {}

    /**
     * Provides access to the single instance of TopicManager.
     * 
     * @return the single instance of TopicManager.
     */
    public static TopicManager get() {
        return TopicManagerHolder.instance;
    }

    // Inner static class responsible for holding the single instance of TopicManager
    private static class TopicManagerHolder {
        private static final TopicManager instance = new TopicManager();
    }

    /**
     * Manages the creation and retrieval of Topic instances.
     */
    public static class TopicManager {
        // Map to store topics by their name
        private final Map<String, Topic> topics;

        // Private constructor to prevent external instantiation
        private TopicManager() {
            this.topics = new HashMap<>();
        }

        /**
         * Retrieves a Topic by its name. If the Topic does not exist, it creates a new one.
         * 
         * @param name the name of the Topic.
         * @return the Topic instance.
         */
        public Topic getTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new); // Create and store a new Topic if it doesn't exist
        }

        /**
         * Retrieves a collection of all Topics.
         * 
         * @return a collection of all Topics.
         */
        public Collection<Topic> getTopics() {
            return topics.values();
        }

        /**
         * Clears all Topics from the manager.
         */
        public void clear() {
            topics.clear();
        }
    }
}
