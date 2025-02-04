package chapter5.item28.ex2;

import java.util.List;

public class GenericArrayBadEx {
    public static void main(String[] args) {
//        List<String>[] stringList = new List<String>[1];
//
//        List<Integer>[] intList = List.of(42);
//
//        Object[] objects = stringList;
//
//        objects[0] = intList;
//
//        String s = stringList[0].get(0);

        //제네릭은 타입 안정성을 보장하려고 설계 됐는데
        //배열은 공변 즉 String[] -> Object[]로 캐스팅이 가능함 Object 는 String 을 포함하니까 가능
        //그러면 제네릭타입은 String 만쓰게 설계했는데 Object 로 바뀌어 버렸으니까 제네릭의 설계 의도와 맞지 않아서 금지
    }
}
