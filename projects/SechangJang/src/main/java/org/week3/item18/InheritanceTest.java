package org.week3.item18;

import java.util.Arrays;
import java.util.List;

/**
 * 상속과 컴포지션의 차이를 테스트하는 실행 클래스
 */
public class InheritanceTest {
    public static void main(String[] args) {
        // 상속의 문제점 테스트
        System.out.println("=== 상속의 문제점 테스트 ===");
        CustomHashSet<Integer> hashSet = new CustomHashSet<>();
        System.out.println("요소 추가 전 크기: " + hashSet.size());
        
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        System.out.println(numbers + " 추가");
        hashSet.addAll(numbers);
        
        System.out.println("요소 추가 후 크기: " + hashSet.size());
        System.out.println("실제 호출된 add 횟수: " + hashSet.getAddCount());

        // 컴포지션 활용 테스트
        System.out.println("\n=== 컴포지션 활용 테스트 ===");
        CompositionSet<Integer> compositionSet = new CompositionSet<>();
        System.out.println("요소 추가 전 크기: " + compositionSet.size());
        
        System.out.println(numbers + " 추가");
        compositionSet.addAll(numbers);
        
        System.out.println("요소 추가 후 크기: " + compositionSet.size());
        System.out.println("실제 추가된 요소 수: " + compositionSet.getAddCount());

        // IS-A 관계 테스트
        System.out.println("\n=== IS-A 관계 테스트 ===");
        Vehicle vehicle = new Vehicle();
        Car car = new Car(200);
        
        System.out.print("Vehicle 이동: ");
        vehicle.move();
        System.out.print("Car 이동: ");
        car.move();
    }
}