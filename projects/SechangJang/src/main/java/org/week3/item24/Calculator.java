package org.week3.item24;

/**
 * 정적/비정적 멤버 클래스의 차이를 보여주는 예제
 */
public class Calculator {
    private int value;

    // 정적 멤버 클래스 - 권장
    public static class StaticOperation {
        // 외부 클래스 참조 없음
        public static int add(int a, int b) {
            return a + b;
        }
    }

    // 비정적 멤버 클래스 - 필요한 경우만 사용
    public class NonStaticOperation {
        // 암묵적으로 외부 클래스 참조를 가짐
        public void add(int num) {
            value += num;  // 외부 클래스의 필드 접근
        }

        public int getValue() {
            return value;  // 외부 클래스의 필드 접근
        }
    }
}