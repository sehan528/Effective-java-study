package org.week3.item22;

/**
 * 안티패턴: 상수만 가진 인터페이스
 * 이렇게 하면 안됩니다!
 */
public interface GameConstants {
    int MAX_SCORE = 1000;
    int INITIAL_LIVES = 3;
    double SPEED_FACTOR = 1.5;
}