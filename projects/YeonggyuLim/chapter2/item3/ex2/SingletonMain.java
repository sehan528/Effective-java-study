package chapter2.item3.ex2;

public class SingletonMain {
    public static void main(String[] args) {
        Singleton instance = Singleton.getInstance();
        Singleton instance1 = Singleton.getInstance();

        System.out.println(instance1 == instance);
        instance1.doSomething();
    }
}
