package chapter4.item15.ex1;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ex {
    //package-private : 접근제한자 선언 안한것 --> 해당 패키지에서만 사용 가능
    //단 인터페이스는 선언 안할시 기본이 public

    //필드로 public static final 제공시 해당 필드를 반환하는 접근자 메서드를 제공하면 안된다.
    //배열 참조가 외부로 공개되므로 외부에서 내부 데이터를 수정 가능
    //불변성을 위반하게됨
    public static final int[] Values = {3, 5};

    //해결법
    //불변 리스트를 제공
    private static final Integer[] PRIVATE_VALUES = {3, 5};
    public static final List<Integer> VALUES =
            Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));


    //해결법 2
    //방어적 복사
    //기본 자료형일시 참조값이 복사 되지 않지만 참조형이면 참조값 복사되니 주의
    public static final Integer[] values() {
        return PRIVATE_VALUES.clone();
    }

}
