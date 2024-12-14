package chapter3.item11.ex3;

import chapter3.item11.ex2.PhoneNumber;

import java.util.HashMap;
import java.util.Map;

public class HashCodeEx3 {
    public static void main(String[] args) {
        //해쉬코드 오버라이딩 한 결과

        Map<chapter3.item11.ex2.PhoneNumber, String> m = new HashMap<>();

        m.put(new chapter3.item11.ex2.PhoneNumber((short) 707,  (short) 867, (short) 5309), "제니");
        System.out.println(m.get(new PhoneNumber((short) 707, (short) 867, (short) 5309)));
    }
}
