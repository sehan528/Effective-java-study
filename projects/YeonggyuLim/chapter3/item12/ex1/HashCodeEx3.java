package chapter3.item12.ex1;

import java.util.HashMap;
import java.util.Map;

public class HashCodeEx3 {
    public static void main(String[] args) {
        //해쉬코드 오버라이딩 한 결과

        Map<PhoneNumber, String> m = new HashMap<>();

        m.put(new PhoneNumber((short) 707,  (short) 867, (short) 5309), "제니");

        //toString 재정의 안하고 호출시 해시코드가 나옴
        //구체 클래스에서 toString 을 재정의 하자 디버깅이 쉬워지기 때문
        System.out.println(m);


    }
}
