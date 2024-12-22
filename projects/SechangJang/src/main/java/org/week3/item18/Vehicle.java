package org.week3.item18;

/**
 * 올바른 상속 관계를 보여주는 예제의 상위 클래스
 * - 명확한 is-a 관계
 */
public class Vehicle {
    protected int speed;

    public void move() {
        System.out.println("기본 이동");
    }

    protected void setSpeed(int speed) {
        this.speed = speed;
    }
}
