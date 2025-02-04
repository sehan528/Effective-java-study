package chapter5.item32.ex1;

import java.util.concurrent.ThreadLocalRandom;

public class ex3 {

    static <T> T[]toArray(T... args) {
        return args;
        //제네릭 매개변수 배열의 참조를 노출

    }

    static <T> T[] pickTwo(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0: return toArray(a, b);
            case 1: return toArray(a, c);
            case 2: return toArray(b, c);
        }
        throw new AssertionError();
    }

    public static void main(String[] args) {
        String[] attributes = pickTwo("좋은", "빠른", "저렴한");
        //컴파일러 상에서 String 으로 추론하지만 런타임시 제네릭 타입이 소거 돼서 어떤 타입인지 모름
        //따라서 내부적으로 Object 타입을 생성 따라서 ClassCastException 발생
    }
}
