package chapter2.item4.ex1;

public class Item4Ex {
    //private 이라 클래스 바깥에서 접근 불가지만 실수로 클래스 내에서 생성 할 수 있으니 예외 던지기
    private Item4Ex() {
        throw new AssertionError();
    }
}
