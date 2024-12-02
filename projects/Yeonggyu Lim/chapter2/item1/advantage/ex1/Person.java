package chapter2.item1.advantage.ex1;

public class Person {
    private String name;
    private int age;

    private Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    //생성자 대신 정적 팩토리메서드로 무슨 역할을 하는지 더 정확히 나타낼 수 있음
    public static Person of(String name, int age) {
        return new Person(name, age);
    }
}
