package org.week3.item20;

// 믹스인 인터페이스
interface Secured {
    default void secure() {
        System.out.println("보안 검사 수행");
    }
}