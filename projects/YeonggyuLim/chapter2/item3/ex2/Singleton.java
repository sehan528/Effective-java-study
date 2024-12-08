package chapter2.item3.ex2;

public class Singleton {
    //정적 팩토리 메서드를 이용한 싱글톤
    //이 방법 역시 리플렉션에 취약
    //readResolve 메서드를 제공하지 않으면 역직렬화 때마다 새로운 인스턴스가 생성됨
    private static final Singleton INSTANCE = new Singleton();
    private static boolean instanceCreated = false;

    //리플렉션 방지
    private Singleton() {
        if (instanceCreated) {
            throw new IllegalStateException("이미 싱글톤 객체가 존재");
        }
        instanceCreated = true;
    }

    public static Singleton getInstance() {
        return INSTANCE;
    }

    public void doSomething() {
        System.out.println("아무거나");
    }

    //역직렬화시 싱글톤 객체를 반환하고 새로 만들어진 객체는 가비지 컬렉터로
    private Object readResolve() {
        return INSTANCE;
    }
}
