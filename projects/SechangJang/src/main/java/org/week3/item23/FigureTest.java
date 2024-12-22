package org.week3.item23;

import java.util.List;
import java.util.ArrayList;

/**
 * 도형 클래스들을 테스트하는 실행 클래스
 */
public class FigureTest {
    public static void main(String[] args) {
        // 태그 달린 클래스 사용 예시
        System.out.println("=== 태그 달린 클래스 사용 (안티패턴) ===");
        TaggedFigure taggedCircle = new TaggedFigure(5.0);
        TaggedFigure taggedRectangle = new TaggedFigure(5.0, 10.0);
        
        System.out.printf("원의 넓이: %.2f%n", taggedCircle.area());
        System.out.printf("사각형의 넓이: %.2f%n", taggedRectangle.area());
        System.out.println("잘못된 도형 타입으로 인한 오류 발생 가능!");

        // 클래스 계층구조 사용 예시
        System.out.println("\n=== 클래스 계층구조 사용 (권장) ===");
        List<Figure> figures = new ArrayList<>();
        figures.add(new Circle(5.0));
        figures.add(new Rectangle(5.0, 10.0));

        // 다형성을 활용한 도형 처리
        System.out.println("다형성을 활용한 도형 처리:");
        for (Figure figure : figures) {
            System.out.printf("- %s: %.2f%n", 
                figure.getDescription(), 
                figure.area());
        }

        // 새로운 도형 추가가 용이함을 보여주는 예시
        System.out.println("\n=== 확장성 테스트 ===");
        processAnyFigure(new Circle(3.0));
        processAnyFigure(new Rectangle(4.0, 5.0));
    }

    // 어떤 도형이든 처리할 수 있는 메서드
    private static void processAnyFigure(Figure figure) {
        System.out.printf("%s의 넓이: %.2f%n", 
            figure.getDescription(), 
            figure.area());
    }
}