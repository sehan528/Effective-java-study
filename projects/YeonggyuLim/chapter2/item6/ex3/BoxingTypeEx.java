package chapter2.item6.ex3;

public class BoxingTypeEx {
    public static void main(String[] args) {
        //Long 은 박싱 타입이라 불변객체 즉 반복문을 돌때마다 새로운 sum 이 생성됨
        // 2^31 개 만큼의 Long 타입이 생성되므로 그냥 기본타입 long 을 쓰자
        Long sum = 0L;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }

        //sum += i; 의 동작 과정
        //sum = Long.valueOf(sum.longValue() + i);
        //1. sum.longValue() --> 언박싱
        //2. sum.longValue() + i; 계산
        //3. Long.valueOf() 새로운 Long 객체 생성

    }
}
