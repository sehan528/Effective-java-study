package org.week3.item17;

/**
 * 기본적인 불변 클래스 예제
 * - final 클래스
 * - final 필드
 * - 생성자를 통한 초기화
 */
public final class ImmutablePerson {
    private final String name;
    private final int age;

    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "이름=" + name + ", 나이=" + age;
    }
}