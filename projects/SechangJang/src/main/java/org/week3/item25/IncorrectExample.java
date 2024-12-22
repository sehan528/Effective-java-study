package org.week3.item25;

public class IncorrectExample {
    /**
     * 톱레벨 클래스 중복 정의 문제를 보여주는 예제
     * 동일 이름의 클래스들이 다른 파일에 정의되어 컴파일 순서에 따라 결과가 달라짐
     */
    class Utensil {
        static final String NAME = "pan";
    }

    class Dessert {
        static final String NAME = "cake";
    }
}
