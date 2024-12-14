package org.week2.item13;

/**
 * clone 메서드의 다양한 구현을 테스트하는 클래스
 */
public class CloneTest {
    public static void main(String[] args) {
        // PhoneNumber 테스트
        System.out.println("=== PhoneNumber Clone 테스트 ===");
        PhoneNumber original = new PhoneNumber(123, 456, 7890);
        PhoneNumber cloned = original.clone();
        System.out.println("원본: " + original);
        System.out.println("복제본: " + cloned);
        System.out.println("원본 == 복제본: " + (original == cloned));
        System.out.println("원본.equals(복제본): " + original.equals(cloned));

        // Stack 테스트
        System.out.println("\n=== Stack Clone 테스트 ===");
        Stack originalStack = new Stack();
        originalStack.push("첫 번째");
        originalStack.push("두 번째");
        
        Stack clonedStack = originalStack.clone();
        System.out.println("원본 스택에서 pop: " + originalStack.pop());
        System.out.println("복제된 스택에서 pop: " + clonedStack.pop());
        System.out.println("스택이 독립적으로 동작함 확인: " + 
            (originalStack.isEmpty() == clonedStack.isEmpty()));

        // HashTable 테스트
        System.out.println("\n=== HashTable Clone 테스트 ===");
        HashTable originalTable = new HashTable();
        originalTable.put("키1", "값1");
        originalTable.put("키2", "값2");
        
        HashTable clonedTable = originalTable.clone();
        System.out.println("원본과 복제본이 다른 객체임: " + (originalTable != clonedTable));
        System.out.println("원본의 값 확인: " + originalTable.get("키1"));
        System.out.println("복제본의 값 확인: " + clonedTable.get("키1"));
        
        // 깊은 복사 확인을 위한 테스트
        System.out.println("\n=== 깊은 복사 확인 테스트 ===");
        originalTable.put("키1", "수정된 값");
        System.out.println("원본의 수정된 값: " + originalTable.get("키1"));
        System.out.println("복제본의 값(변경되지 않아야 함): " + clonedTable.get("키1"));
    }
}