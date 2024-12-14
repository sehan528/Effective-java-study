package chapter3.item14.ex4;

import java.util.Comparator;

public class ComparatorEx {
    //이 방식은 사용금지 --> 정수 오버플로우 또는 부동소수점 계산 방식에 따른 오류 발생 할 수 있음
    //오버플로우 발생이유
    //int x = Integer.MAX_VALUE; // 2,147,483,647
    //int y = Integer.MIN_VALUE; // -2,147,483,648
    //int diff = x - y; // 4,294,967,295 (이 값은 int 범위를 초과)
    static Comparator<Object> hashCodeOrder = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            return o1.hashCode() - o2.hashCode();
        }
    };


    //대안
    //1. 정적 compare 메서드를 활용
    //Integer.compare()는 정수의 차이를 비교하는것이 아닌 직접 크기만 비교 따라서 오버플로우 발생 x

    static Comparator<Object> hashCodeOrder2 = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    };

    //2. 비교자 생성 메서드를 활용
    //Integer.compare()과 같은 방식으로 비교 + 람다라 가독성 좋음
    static Comparator<Object> hashCodeOrder3 =
            Comparator.comparing(o -> o.hashCode());
}
