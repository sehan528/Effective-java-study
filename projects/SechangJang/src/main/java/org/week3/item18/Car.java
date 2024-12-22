package org.week3.item18;

/**
 * Vehicle의 하위 클래스
 * - "자동차는 운송수단이다" 라는 is-a 관계 성립
 */
public class Car extends Vehicle {
    private final int maxSpeed;

    public Car(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public void move() {
        System.out.printf("자동차로 이동 (최고속도: %dkm/h)%n", maxSpeed);
    }
}