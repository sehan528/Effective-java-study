package chapter3.item11.ex1;

import java.util.HashMap;
import java.util.Map;

public class HashCodeEx {
    public static void main(String[] args) {
        Map<PhoneNumber, String> m = new HashMap<>();
        m.put(new PhoneNumber((short) 707, (short) 867, (short) 5309), "제니");
        //hashCode 를 재정의 하지 않아 다른 객체로 판단
        System.out.println(m.get(new PhoneNumber((short) 707, (short) 867, (short) 5309)));

    }
}
