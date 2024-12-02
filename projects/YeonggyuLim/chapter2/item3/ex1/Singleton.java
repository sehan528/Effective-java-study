package chapter2.item3.ex1;

public class Singleton {
    //private 으로 생성자를 막아두고 public static final 로 인스턴스화 해서 싱글톤 객체임을 증명
    //단 권한이 있는 사용자가 리플렉션을 사용해 접근하면 private 생성자를 호출 할 수 있다.
    //두번째 객체 생성시 예외를 던지게 해서 공격을 방어 할 수 있음
    public static final Singleton INSTANCE = new Singleton();

    private Singleton() {
    }
    public void doSomeThing() {
        System.out.println("아무거나");
    }
}
