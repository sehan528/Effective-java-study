package org.week3.item15;

import java.util.*;

/**
 * public static final 필드의 올바른 사용법을 보여주는 예제
 */
public class StaticFieldExample {
    // 안티패턴: 배열을 직접 노출
    public static final Integer[] WRONG_VALUES = {1, 2, 3};

    // 올바른 방법 1: 불변 리스트로 제공
    private static final Integer[] VALUES = {1, 2, 3};
    public static final List<Integer> VALUES_LIST = 
        Collections.unmodifiableList(Arrays.asList(VALUES));

    // 올바른 방법 2: 방어적 복사를 통한 제공
    public static Integer[] getValues() {
        return VALUES.clone();
    }
}