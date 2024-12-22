package org.week3.item16;

import org.week3.item16.bad.*;     
import org.week3.item16.good.*;      
import org.week3.item16.practice.ItemInfo;

public class AccessorMethodTest {
    public static void main(String[] args) {
        System.out.println("=== 잘못된 구현 테스트 ===");
        // 잘못된 구현 테스트
        UnsafePoint badPoint = new UnsafePoint();
        badPoint.x = 5;
        badPoint.y = 10;
        System.out.println("Point 직접 접근: (" + badPoint.x + ", " + badPoint.y + ")");

        Time time = new Time(12, 30);
        System.out.println("불변 Time 직접 접근: " + time.hour + ":" + time.minute);

        System.out.println("\n=== 올바른 구현 테스트 ===");
        // 올바른 구현 테스트
        SafePoint goodPoint = new SafePoint(5, 10);
        System.out.println("캡슐화된 Point: " + goodPoint);

        ColorPoint colorPoint = new ColorPoint(5, 10, "RED");
        System.out.println("중첩 클래스 활용: " + colorPoint.getColor() + " 점 (" 
            + colorPoint.getX() + ", " + colorPoint.getY() + ")");

        System.out.println("\n=== 실전 예제 테스트 ===");
        // 실전 예제 테스트
        ItemInfo item = new ItemInfo("노트북", 1000000);
        System.out.println("상품 정보 초기값: " + item);

        // 가격 정책 변경 시뮬레이션
        item.setPrice(1200000);
        System.out.println("가격 정책 변경 후: " + item + " (세금 포함)");
    }
}