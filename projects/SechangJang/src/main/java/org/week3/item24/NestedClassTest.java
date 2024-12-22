package org.week3.item24;

/**
 * 중첩 클래스 테스트를 위한 실행 클래스
 */
public class NestedClassTest {
    public static void main(String[] args) {
        // 정적 멤버 클래스 테스트
        System.out.println("=== 정적 멤버 클래스 테스트 ===");
        int result = Calculator.StaticOperation.add(10, 5);
        System.out.println("정적 연산 결과: " + result);
        System.out.println("메모리 사용 최적화됨");

        // 비정적 멤버 클래스 테스트
        System.out.println("\n=== 비정적 멤버 클래스 테스트 ===");
        Calculator calculator = new Calculator();
        Calculator.NonStaticOperation operation = calculator.new NonStaticOperation();
        operation.add(15);
        System.out.println("비정적 연산 결과: " + operation.getValue());
        System.out.println("외부 클래스 참조 존재");

        // Map.Entry 스타일 테스트
        System.out.println("\n=== Map.Entry 스타일 테스트 ===");
        MapExample<String, String> map = new MapExample<>();
        MapExample.Entry<String, String> entry = map.createEntry("testKey", "testValue");
        System.out.println(entry.toString());
        System.out.println("정적 멤버 독립성 확인");

        // UI 컴포넌트 테스트
        System.out.println("\n=== UI 컴포넌트 테스트 ===");
        UIComponent component = new UIComponent();
        UIComponent.ClickListener listener = component.createClickListener();
        listener.onClick();
        System.out.println("외부 상태 변경 확인: " + component.isClicked());
    }
}