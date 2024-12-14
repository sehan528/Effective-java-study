# Item 14 예제 실행 가이드

## 프로젝트 구조
```
item14/
├── PhoneNumber.java          - Comparable 구현의 기본 예제
├── BigDecimalExample.java    - equals와 compareTo 차이 예제
├── HashCodeComparator.java   - 다양한 비교자 구현 예제
└── ComparableTest.java       - 테스트 실행 클래스
```

## 실행 방법
1. 모든 Java 파일을 item14 패키지 아래에 위치시킵니다.
2. ComparableTest 클래스를 실행합니다.

## 학습 포인트

### 1. PhoneNumber 클래스
* Comparable 인터페이스 구현
* 다중 필드 비교 구현
* Comparator 활용
* 메서드 체이닝을 통한 비교자 구성

### 2. BigDecimalExample 클래스
* equals와 compareTo의 차이점 이해
* HashSet과 TreeSet의 동작 차이
* 일관성 있는 동치성 비교의 중요성

### 3. HashCodeComparator 클래스
* 다양한 비교자 구현 방식
* 정적 compare 메서드 활용
* Comparator.comparingInt 활용
* 잘못된 구현의 예시와 해결방안

### 4. 실행 결과 분석
* compareTo 규약 준수 확인
* 정렬 결과 검증
* 컬렉션에서의 동작 확인

## 기대 실행 결과
```
=== PhoneNumber 비교 테스트 ===
정렬 전: [123-456-7890, 123-456-7891, 123-455-7890]
정렬 후: [123-455-7890, 123-456-7890, 123-456-7891]

=== BigDecimal 비교 테스트 ===
HashSet 크기: 2
TreeSet 크기: 1

=== Comparator 구현 테스트 ===
기본 비교: -1
메서드 체이닝 비교: -1
hashCode 비교: 양수
```

## 주요 학습 내용
1. Comparable 인터페이스의 특징과 활용
2. compareTo 메서드 규약
3. equals와 compareTo의 관계
4. 다양한 비교자 구현 방식
5. Java 8의 Comparator 인터페이스 활용

## 추가 실습 제안
1. 다른 필드를 기준으로 정렬하는 비교자 만들기
2. 역순 정렬 구현해보기
3. 복합 객체의 비교자 구현해보기
4. Comparator 제네릭 타입 활용해보기