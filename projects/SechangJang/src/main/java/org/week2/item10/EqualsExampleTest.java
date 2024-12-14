package org.week2.item10;

/**
 * equals 메서드 테스트를 위한 실행 클래스
 */
public class EqualsExampleTest {
    public static void main(String[] args) {
        // 1. Point equals 테스트
        System.out.println("=== Point equals 테스트 ===");
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        Point p3 = new Point(2, 1);
        System.out.println("p1.equals(p2): " + p1.equals(p2));  // true
        System.out.println("p1.equals(p3): " + p1.equals(p3));  // false

        // 2. 잘못된 상속 관계 테스트
        System.out.println("\n=== ColorPoint equals 테스트 (잘못된 예시) ===");
        Point p = new Point(1, 2);
        ColorPoint cp = new ColorPoint(1, 2, Color.RED);
        System.out.println("p.equals(cp): " + p.equals(cp));    // true
        System.out.println("cp.equals(p): " + cp.equals(p));    // false
        // Learning Point: 대칭성 위배!

        // 3. 컴포지션을 사용한 올바른 구현 테스트
        System.out.println("\n=== ColorPointComposition equals 테스트 (올바른 예시) ===");
        ColorPointComposition cp1 = new ColorPointComposition(1, 2, Color.RED);
        ColorPointComposition cp2 = new ColorPointComposition(1, 2, Color.RED);
        System.out.println("cp1.equals(cp2): " + cp1.equals(cp2));  // true
        System.out.println("cp1.equals(p): " + cp1.equals(p));      // false

        // 4. CaseInsensitiveString 테스트
        System.out.println("\n=== CaseInsensitiveString equals 테스트 ===");
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        String s = "polish";
        System.out.println("cis.equals(s): " + cis.equals(s));      // true
        System.out.println("s.equals(cis): " + s.equals(cis));      // false
        // Learning Point: 대칭성 위배!
    }
}