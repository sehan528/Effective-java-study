package org.week3.item19;


public class Rectangle extends Shape {
    private final int width;
    private final int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(String color) {
        super.draw(color);  // 상위 클래스의 동작 수행
        // 추가적인 사각형 특화 그리기 동작
    }
}