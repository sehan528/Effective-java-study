package org.week3.item17;

/**
 * 가변 클래스 예제 - 불변 클래스와 비교용
 * - setter를 통한 상태 변경 가능
 * - 스레드 안전하지 않음
 */
public class MutablePerson {
    private String name;
    private int age;

    public MutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return "이름=" + name + ", 나이=" + age;
    }
}
