package chapter2.item1.advantage.ex2;

import java.util.HashMap;
import java.util.Map;

public class Person {
    //캐싱을 활용한 동일 인스턴스 반환
    private static final Map<String, Person> CACHE = new HashMap<>();
    private String name;
    private int age;

    private Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Person of(String name, int age) {
        String key = name + age;
        if (!CACHE.containsKey(key)) {
            CACHE.put(key, new Person(name, age));
        }
        return CACHE.get(key);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
