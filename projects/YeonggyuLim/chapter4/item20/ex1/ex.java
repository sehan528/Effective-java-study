package chapter4.item20.ex1;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class ex {
    //골격 구현을 사용해 완성한 구체 클래스
    //필요한 메서드만 구현하기 위해
    //equals hashcode 같은 Object 메서드는 디폴트 메서드로 제공하면 안됨
    //객체의 고유한 동작을 정의해야하기 때문
    //디폴트 메서드를 재정의 하지않을경우 하위 클래스에서 문제 발생할 수 있으니 차라리 하위클래스에서 재정의하게
    //Objects 메서드들을 디폴트 메서드로 제공하지 않는게 좋음
    static List<Integer> intArrayAsList(int[] a) {
        Objects.requireNonNull(a);

        return new AbstractList<Integer>() {
            @Override
            public Integer get(int i) {
                return a[i];
            }

            @Override
            public Integer set(int i, Integer val) {
                int oldVal = a[i];
                a[i] = val;
                return oldVal;
            }

            @Override
            public int size() {
                return a.length;
            }
        };
    }
}
