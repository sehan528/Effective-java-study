package org.week3.item15;

import java.util.*;

/**
 * 접근 제어 예제들을 테스트하는 클래스
 */
public class AccessControlTest {
    public static void main(String[] args) {
        // Point 테스트
        System.out.println("=== 접근 제어 테스트 ===");
        
        BadPoint badPoint = new BadPoint();
        badPoint.x = 5;  // 직접 접근 가능 - 위험!
        
        GoodPoint goodPoint = new GoodPoint(5, 10);
        System.out.println("올바른 접근: " + goodPoint);
        
        // StaticFieldExample 테스트
        System.out.println("\n=== Public Static Final 필드 테스트 ===");
        
        // 잘못된 방식의 배열 수정 시도
        System.out.println("배열 변경 시도 전: " + Arrays.toString(StaticFieldExample.WRONG_VALUES));
        try {
            StaticFieldExample.WRONG_VALUES[0] = 100;
            System.out.println("경고: 배열이 수정되었습니다!");
        } catch (Exception e) {
            System.out.println("배열 직접 변경 시도 실패");
        }

        // 올바른 방식의 접근
        System.out.println("불변 리스트: " + StaticFieldExample.VALUES_LIST);
        System.out.println("방어적 복사: " + Arrays.toString(StaticFieldExample.getValues()));

        // SecurityExample 테스트
        System.out.println("\n=== 보안 테스트 ===");
        SecurityExample securityExample = new SecurityExample();
        
        List<String> unsafeData = securityExample.getSecretDataUnsafe();
        List<String> safeData = securityExample.getSecretDataSafe();
        List<String> defensiveData = securityExample.getSecretDataDefensive();

        try {
            unsafeData.add("새로운 데이터");
            System.out.println("경고: 안전하지 않은 데이터가 수정되었습니다!");
        } catch (UnsupportedOperationException e) {
            System.out.println("안전: 데이터 수정이 차단되었습니다");
        }

        try {
            safeData.add("새로운 데이터");
        } catch (UnsupportedOperationException e) {
            System.out.println("안전: 불변 리스트는 수정할 수 없습니다");
        }

        defensiveData.add("새로운 데이터");
        System.out.println("안전: 방어적 복사본은 수정 가능하나 원본에 영향 없음");
    }
}