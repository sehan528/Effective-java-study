package chapter5.item32.ex1;

import java.util.List;

public class VarargsEx {
    static void dangerous(List<String>... stringLists) {
        List<Integer> intList = List.of(42);
        //Object[] 로 형변환 해서 위험 제네릭은 String 타입의 리스트이기 때문
        Object[] objects = stringLists;
        objects[0] = stringLists[0].get(0);
    }
}
