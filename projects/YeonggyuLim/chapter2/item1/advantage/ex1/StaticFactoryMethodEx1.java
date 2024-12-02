package chapter2.item1.advantage.ex1;

public class StaticFactoryMethodEx1 {
    //이름을 가질 수 있다
    public static void main(String[] args) {
        Person person = Person.of("임영규", 26);
        System.out.println(person.getAge());
        System.out.println(person.getName());
    }
}
