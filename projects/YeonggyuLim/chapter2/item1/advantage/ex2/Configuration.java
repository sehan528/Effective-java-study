package chapter2.item1.advantage.ex2;

public class Configuration {
    private static final Configuration INSTANCE = new Configuration();

    private Configuration() {
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

}
