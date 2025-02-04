package chapter5.item30.ex1;

import java.util.HashSet;
import java.util.Set;

public class ex2 {
    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();

        set.add("ss");
        set2.add(3);

        //다른 타입의 Set 이기 때문에 컴파일 에러
//        Set union = union(set, set2);
//        for (Object o : union) {
//            System.out.println(o);
        }
    }

