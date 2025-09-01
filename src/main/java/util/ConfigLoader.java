package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (input != null) {
                properties.load(input);
                System.out.println("Config file loaded successfully");
            } else {
                System.out.println("Config file not found! Using defaults.");
                setDefaults();
            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
            setDefaults();
        }
    }
    
    private static void setDefaults() {
        properties.setProperty("server.port", "1234");
        properties.setProperty("server.max_clients", "50");
        properties.setProperty("server.max_message_length", "500");
        properties.setProperty("client.timeout", "30000");
    }
    
    public static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port", "1234"));
    }
    
    public static int getMaxClients() {
        return Integer.parseInt(properties.getProperty("server.max_clients", "50"));
    }
    
    public static int getMaxMessageLength() {
        return Integer.parseInt(properties.getProperty("server.max_message_length", "500"));
    }
    
    public static String getWelcomeMessage() {
        return properties.getProperty("server.welcome_message", "Welcome to Textly Chat!");
    }
}