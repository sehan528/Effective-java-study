package chapter5.item26.ex4;

import java.util.Set;

public class RawTypeExceptionEx {

    // 1. 클래스 리터럴에는 로타입을 사용해야 한다.
    // 런타임시 제네릭 타입 정보가 지워 지므로 그냥 로타입 사용
    Class<?> rawClass = Set.class;
//    Class<?> rawClass2 = Set<String>.class;
    // 컴파일 에러


    // 2. 런타임시 제네릭 타입의 정보가 지워짐 또한 로 타입이든 비한정적 와일드 카드 타입이든 instanceof는 완전히 똑같이 동작
    // 따라서 그냥 로타입을 쓰는게 남

    // 컴파일 후 에는 Set<String> Set<Integer> 둘다 그냥 똑같은 Set 취급
    // 따라서 instanceOf Set<String> 같은 검사가 불가능함 그래서 로타입을 사용해야함
    public static void main(String[] args) {
        Object o = new RawTypeExceptionEx();
        if (o instanceof Set) {
            Set<?> s = (Set<?>) o;
        }
    }
}
