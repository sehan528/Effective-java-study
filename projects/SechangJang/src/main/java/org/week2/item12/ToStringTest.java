package org.week2.item12;

import java.util.HashMap;
import java.util.Map;

/**
 * toString의 다양한 사용 예시를 보여주는 테스트 클래스
 */
public class ToStringTest {
    public static void main(String[] args) {
        // 1. PhoneNumber toString 테스트
        PhoneNumber jenny = new PhoneNumber(707, 867, 5309);
        System.out.println("전화번호: " + jenny);
        
        // 2. Map에서의 toString 활용
        Map<PhoneNumber, String> m = new HashMap<>();
        m.put(jenny, "제니");
        System.out.println("전화번호부: " + m);
        
        // 3. Medicine toString 테스트
        Medicine medicine = new Medicine("9", "사랑", "테러빈유");
        System.out.println("약물 정보: " + medicine);
        
        // 4. 디버깅 메시지에서의 활용
        try {
            PhoneNumber invalid = new PhoneNumber(1000, 867, 5309);
        } catch (IllegalArgumentException e) {
            System.out.println("에러 발생: " + e);
        }
    }
}