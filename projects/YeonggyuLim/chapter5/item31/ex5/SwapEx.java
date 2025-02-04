package chapter5.item31.ex5;

import java.util.List;

public class SwapEx {
    //간단한 public api 라면 2번째 메서드가 낫다
    //기본 규칙 메서드 선언 타입 매개변수가 한 번만 나오면 와일드카드로 대체 하기
    //제네릭 타입 매개변수<E>는 메서드 내부에서 타입 안정성을 추가로 제공해야 할 경우에 사용하는게 좋음
    public static <E> void swap(List<E> list, int i, int j) {

    }

    public static void swap2(List<?> list, int i , int j) {
        //요소의 타입을 알 수 없음, 와일드 카드는 읽기는 허용되지만 쓰기는 허용 되지 않음
        //정확히는 와일드 카드는 어떤 타입인지는 모르지만 특정 타입임은 보장함

//        list.set(i, list.set(j, list.get(i)));
    }

    public static void swap3(List<?> list, int i, int j) {
        swapHelper(list, i, j);
    }

    //? 타입을 매개변수로 캡쳐해서 <E> 타입 매겨변수로 넘김 E 타입을 구체화 할 수 있으므로 타입안정성 있음
    // 타입 안정성을 보장하면서 리스트의 요소를 교환 가능
    private static <E> void swapHelper(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }
}
