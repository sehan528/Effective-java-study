package chapter5.item32.ex2;

import java.util.ArrayList;
import java.util.List;

public class VarargsSafeEx {
    // 1. @SafeVarargs 어노테이션 된 또 다른 varargs 메서드에 넘기는것은 안전
    // 2. 이 배열 내용의 일부 함수(varargs 를 받지 않는)를 호출만 하는 일반 메서드에 넘기는 것도 안전

    //@SafeVarargs 사용 할때의 규칙
    // 1. varargs 매개변수 배열에 아무것도 저장하지 않는다.
    // 2. 그 배열(복사본 포함)을 신뢰할 수 없는 코드에 노출하지 않는다.

    @SafeVarargs
    //List<? extends T>... --> List<? extends T>[] 배열 형태로 전달됨
    static <T> List<T> flatten(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list: lists) {
            result.addAll(list);
        }
        return result;
    }

    public static void main(String[] args) {
        //List.of 활용하면 여러 인수를 넘길 수 있는 이유 --> 이미 @SafeVarargs 어노테이션이 있음
        List<String> audience = flatten(List.of("friend", "romans", "countrymen"));
    }
}
