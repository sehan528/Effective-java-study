package chapter5.item31.ex3;

import java.util.HashSet;
import java.util.Set;

public class UnionEx {

//    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
//        Set<E> result = new HashSet(s1);
//        result.addAll(s2);
//        return result;
//    }

    //E 의 하위타입 추가 가능 더 유연해짐
    public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2) {
        Set<E> result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }

    public static void main(String[] args) {
        Set<Integer> integers = Set.of(1, 3, 5);
        Set<Double> doubles = Set.of(1.1, 3.3, 5.5);
        Set<Number> num = union(integers, doubles);

        for (Number number : num) {
            System.out.println(number);
        }
    }
}
