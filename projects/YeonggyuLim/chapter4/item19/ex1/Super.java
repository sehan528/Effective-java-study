package chapter4.item19.ex1;

public class Super {
    //생성자는 직간접적으로 재정의 가능 메서드를 호출해서는 안 된다.
    //상위 클래스의 생성자가 하위클래스의 생성자보다 먼저 실행되므로
    //하위클래스에서 재정의 한 메서드가
    //하위 클래스의 생성자보다 먼저 호출된다.
    public Super() {
        overrideMe();
    }
    public void overrideMe() {
    }
}
