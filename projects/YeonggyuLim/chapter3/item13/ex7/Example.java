package chapter3.item13.ex7;

public class Example {
    private final int value;

    private Example(int value) {
        this.value = value;
    }

    //변환 생성자 메서드
    public Example(Example other) {
        this.value = other.value;
    }

    //변환 팩토리 메서드
    public static Example copyOf(Example other) {
        return new Example(other.value);
    }

    //변환 생성자, 변환 팩토리 메서드의 장점
    //대체로 변환 생성자, 팩토리 메서드가 유리
    //1. 깊은 복사 구현 용이
    //2. 예외 처리 불필요
    //3. 상속 문제 해결
}
