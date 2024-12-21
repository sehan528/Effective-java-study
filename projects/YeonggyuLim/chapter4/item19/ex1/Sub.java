package chapter4.item19.ex1;

import java.time.Instant;

public class Sub extends Super{
    //상속용 클래스, readObject, clone 메서드에는 재정의 가능 메서드 호출 금지
    //아래 예시처럼 생성자에서 초기화 전에 호출을 해버림
    //클래스 확장을 해야하는 명확한 이유가 없으면 차라리 final 로 선언해서 상속 금지
    // 또는 생성자를 외부에서 접근 불가능하게 해야함
    private final Instant instant;

    Sub() {
        instant = Instant.now();
    }


    @Override
    public void overrideMe() {
        System.out.println(instant);
    }

    public static void main(String[] args) {
        //하위 클래스 sub이 먼저 상위 클래스 Super 호출하고 Super의 생성자에 있는 overrideMe 실행
        Sub sub = new Sub();
        sub.overrideMe();
    }
}
