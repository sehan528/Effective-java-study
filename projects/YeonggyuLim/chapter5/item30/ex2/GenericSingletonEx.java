package chapter5.item30.ex2;

import java.util.function.UnaryOperator;

public class GenericSingletonEx {


    //입력값을 받아 같은 타입의 값을 반환하는 단항 연산자 UnaryOperator
    private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    //UnaryOperator<Object>로 선언됐지만 어쩌피 T 타입으로 캐스팅 돼서 반환 되니까 타입 안전
    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction() {
        return (UnaryOperator<T>) IDENTITY_FN;
    }

    public static void main(String[] args) {
        String[] strings = {"삼베", "대마", "나일론"};
        UnaryOperator<String> sameString = identityFunction();
        for (String s : strings) {
            System.out.println(sameString.apply(s));
        }

        Number[] numbers = {1, 2.0, 3L};
        UnaryOperator<Number> sameNumber = identityFunction();
        for (Number n : numbers) {
            System.out.println(sameNumber.apply(n));
        }
    }


}
