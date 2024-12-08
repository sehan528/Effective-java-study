package chapter2.item1.advantage.ex2;

public class StaticFactoryMethodEx2 {
    //호출될때 마다 새로운 인스턴스를 생성 하지 않아도 됨
    public static void main(String[] args) {
        //캐싱을 활용한 예시
        Person person1 = Person.of("임영규", 26);
        Person person2 = Person.of("임영규", 26);

        //참조값 비교
        System.out.println(person1 == person2);

        //싱글턴 패턴을 활용한 예시
        Configuration config1 = Configuration.getInstance();
        Configuration config2 = Configuration.getInstance();

        //참조값 비교
        System.out.println(config1 == config2);
    }
}
