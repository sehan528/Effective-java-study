package org.week3.item19;

/**
 * 상속용으로 설계된 도형 클래스
 * 하위 클래스에서 draw와 drawBorder 메서드를 재정의할 수 있다.
 */
public class Shape {
    private int x, y;

    /**
     * @implSpec
     *           이 메서드는 도형을 주어진 색상으로 그린다.
     *           하위 클래스는 반드시 super.draw(color)를 호출해야 한다.
     */
    public void draw(String color) {
        // 기본 도형 그리기 구현
        System.out.println("도형 그리기: " + getClass().getSimpleName() + " (" + color + ")");
    }

    /**
     * @implSpec
     *           테두리를 그리기 전에 validateBorder를 호출하여 유효성을 검증한다.
     */
    protected void drawBorder(int thickness) {
        validateBorder(thickness);
        System.out.println("테두리 그리기: 두께 " + thickness + "로 " +
                getClass().getSimpleName() + " 테두리 그림");
    }

    // 하위 클래스에서 사용할 수 있는 유틸리티 메서드
    protected final void validateBorder(int thickness) {
        if (thickness <= 0) {
            throw new IllegalArgumentException("테두리 두께는 양수여야 합니다.");
        }
    }
}