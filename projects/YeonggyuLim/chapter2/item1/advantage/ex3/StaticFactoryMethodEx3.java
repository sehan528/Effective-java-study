package chapter2.item1.advantage.ex3;

import chapter2.item1.advantage.ex3.InterfaceEx.Circle;
import chapter2.item1.advantage.ex3.InterfaceEx.Rectangle;
import chapter2.item1.advantage.ex3.InterfaceEx.Shape;
import chapter2.item1.advantage.ex3.extendEx.Child;
import chapter2.item1.advantage.ex3.extendEx.Parent;

public class StaticFactoryMethodEx3 {

    /*정적 팩토리 메서드를 이용한 하위타입반환
    실제로 메서드는 Parent 반환이지만 Child 를 반환 가능 함
    생성자를 사용하면 반환타입이 고정돼서 Child 반환 불가능
     */
    public static Parent getInstance(boolean condition) {
        if (condition) {
            return new Child();
        } else {
            return new Parent();
        }
    }

    public static Shape createShape(String type) {
        if (type.equals("circle")) {
            return new Circle();
        } else if (type.equals("rectangle")) {
            return new Rectangle();
        }
        throw new IllegalArgumentException("정의 되지 않은 도형");
    }

    public static void main(String[] args) {
        //부모 클래스 반환, 하위클래스 반환 예시
        Parent instance1 = getInstance(true);
        Parent instance2 = getInstance(false);

        instance1.show();
        instance2.show();

        //인터페이스를 활용한 하위타입 반환, 매개변수에 따라 달라짐
        Shape circle = createShape("circle");
        Shape rectangle = createShape("rectangle");
        circle.draw();
        rectangle.draw();

    }

}
