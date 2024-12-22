package org.week3.item25;

/**
 * 톱레벨 클래스 중복 정의 문제와 해결법 테스트
 */
public class Main {
    public static void main(String[] args) {
        // 중복 정의 문제 예제 실행
        System.out.println("=== 톱레벨 클래스 중복 정의 문제 ===");
        System.out.println(IncorrectExample.Utensil.NAME + IncorrectExample.Dessert.NAME);

        // 문제 해결 사례 실행
        System.out.println("\n=== 문제 해결 사례 ===");
        System.out.println(CorrectExample.Utensil.NAME + CorrectExample.Dessert.NAME);
        System.out.println("톱레벨 클래스 분리로 일관성 유지");
    }
}