package org.week3.item21;

/**
 * 디폴트 메서드의 문제점을 테스트하는 실행 클래스
 */
public class DefaultMethodTest {
    public static void main(String[] args) {
        // 기본 컬렉션 테스트
        System.out.println("=== 기본 컬렉션 테스트 ===");
        CustomCollection<Integer> custom = new CustomCollection<>();
        for (int i = 1; i <= 5; i++) {
            custom.add(i);
        }
        System.out.println("요소 추가: " + custom);
        
        custom.removeIf(num -> num % 2 == 0);
        System.out.println("짝수 제거 후: " + custom);
        
        // 동기화된 컬렉션 테스트
        System.out.println("\n=== 동기화된 컬렉션 테스트 ===");
        SynchronizedCollection<Integer> sync = new SynchronizedCollection<>();
        for (int i = 1; i <= 5; i++) {
            sync.add(i);
        }
        System.out.println("요소 추가: " + sync);
        
        System.out.println("동기화된 상태에서 짝수 제거");
        sync.removeIf(num -> num % 2 == 0);
        System.out.println("결과: " + sync);
        
        // 멀티스레드 환경 시뮬레이션
        System.out.println("\n=== 문제 상황 테스트 ===");
        testConcurrentAccess(sync);
    }
    
    private static void testConcurrentAccess(SynchronizedCollection<Integer> collection) {
        Thread t1 = new Thread(() -> {
            collection.add(100);
            collection.removeIf(num -> num > 50);
        });
        
        Thread t2 = new Thread(() -> {
            collection.add(200);
            collection.removeIf(num -> num > 150);
        });
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
            System.out.println("최종 결과: " + collection);
        } catch (InterruptedException e) {
            System.out.println("테스트 중단됨");
        }
    }
}