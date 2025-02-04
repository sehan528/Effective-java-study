package chapter5.item31.ex4;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MaxEx {

    //생산자는 데이터를 읽기만함 외부로 데이터를 제공하는 역할
    //소비자는 데이터를 추가하거나 사용 ex) 리스트에 값 추가
    //리스트를 읽어서 Comparable 에 추가해서 최대값을 비교해서 반환
    public static <E extends Comparable<? super E>> E max(List<? extends E> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
        }

        E result = null;
        for (E e : list) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
}
