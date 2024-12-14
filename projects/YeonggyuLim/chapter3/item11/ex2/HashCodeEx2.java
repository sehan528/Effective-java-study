package chapter3.item11.ex2;

import java.util.HashMap;
import java.util.Map;

public class HashCodeEx2 {
    public static void main(String[] args) {
        //해쉬코드 오버라이딩 한 결과

        Map<PhoneNumber, String> m = new HashMap<>();

        m.put(new PhoneNumber((short) 707,  (short) 867, (short) 5309), "제니");
        System.out.println(m.get(new PhoneNumber((short) 707, (short) 867, (short) 5309)));
    }
}
