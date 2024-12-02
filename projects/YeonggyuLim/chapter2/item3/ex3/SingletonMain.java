package chapter2.item3.ex3;

public class SingletonMain {
    public static void main(String[] args) {
        Singleton instance = Singleton.INSTANCE;
        Singleton instance1 = Singleton.INSTANCE;

        System.out.println(instance1 == instance);
        instance1.doSomething();
    }
}
