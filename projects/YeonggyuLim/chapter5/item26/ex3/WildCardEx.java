package chapter5.item26.ex3;

import java.util.Set;

public class WildCardEx {

    //raw 타입을 쓴 모습
    static int numElementsInCommon(Set s1, Set s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1))
                result++;
        }
        return result;
    }


    //raw 타입 대신 와일드카드 사용한 예시
    static int numElementsInCommonV2(Set<?> s1, Set<?> s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1))
                result++;
        }
        return result;
    }

    // raw, 와일드카드, Object 3가지의 차이점

// 1. 로 타입만 타입 안정성이 없음 (컴파일러가 타입 체크를 하지 않음 → 런타임 오류 위험)
// 2. Object 는 모든 타입의 요소 추가 가능, 반면 와일드카드는 읽기 전용으로 요소 추가 불가능
// 3. 와일드카드는 읽기만 가능(add 등 불가), 따라서 Collection 의 불변성을 보장
//    → 와일드카드는 의도적으로 데이터를 수정하지 않는 경우에 적합



}
