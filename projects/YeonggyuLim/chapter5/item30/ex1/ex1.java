package chapter5.item30.ex1;

import java.util.HashSet;
import java.util.Set;

public class ex1 {

    //컴파일은 가능하지만 타입이 안전하지 않다고 경고가 생김
    //아래 예시처럼 어떤 타입이 올지 모르기 떄문
    public static Set union(Set s1, Set s2) {
        Set result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();

        set.add("ss");
        set2.add(3);

        Set union = union(set, set2);
        for (Object o : union) {
            System.out.println(o);
        }
    }
}
