package org.week3.item20;

// 믹스인 인터페이스
interface Logging {
    default void log(String message) {
        System.out.println("로그: " + message);
    }
}