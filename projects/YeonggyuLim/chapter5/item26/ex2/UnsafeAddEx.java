package chapter5.item26.ex2;

import java.util.ArrayList;
import java.util.List;

public class UnsafeAddEx {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();

        unsafeAdd(strings, Integer.valueOf(42));
        String s = strings.get(0);
    }
    private static void unsafeAdd(List list, Object o) {
        //raw 타입을 썼기 때문에 의도된 타입이 아니어도 일단 들어가지만 Class cast Exception 터짐
        list.add(o);
    }
}
