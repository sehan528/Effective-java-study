package chapter5.item32.ex3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PickTwoRefactor {

    //toArray 처럼 varargs 메서드를 안전하게 작성하는게 불가능한 상황에서 List.of 사용 가능
    //toArray 는 런타임시 제네릭타입 소거로 Object 배열을 생성해서 ClassCastException 발생함
    //List.of 는 불변 리스트 + 리스트기 때문에 내부 타입이 고정 즉 타입 안정성 보장
    static <T> List<T> pickTwo(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0: return List.of(a, b);
            case 1: return List.of(a, c);
            case 2: return List.of(b, c);
        }
        throw new AssertionError();
    }

    public static void main(String[] args) {
        List<String> attributes = pickTwo("좋은", "빠른", "저렴한");
    }
}
