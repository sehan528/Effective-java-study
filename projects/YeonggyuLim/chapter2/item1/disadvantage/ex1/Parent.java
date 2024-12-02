package chapter2.item1.disadvantage.ex1;

public class Parent {
    private String name;

    private Parent(String name) {
        this.name = name;
    }
    //하위 타입에서 호출 불가


    public static Parent of(String name) {
        return new Parent(name);
    }

    public String getName() {
        return name;
    }
}
