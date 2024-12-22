# Item 19 예제 실행 가이드

## 프로젝트 구조
```
item19/
├── Shape.java                  - 상속용으로 설계된 기본 도형 클래스
├── Rectangle.java              - Shape의 올바른 상속 예제
├── ProhibitedInheritance.java - 상속을 금지하는 두 가지 방법 예제
├── BadSuperClass.java         - 잘못된 상속 설계 예제
└── InheritanceTest.java       - 테스트 실행 클래스
```

## 실행 방법
1. 모든 Java 파일을 item19 패키지 아래에 위치시킵니다.
2. InheritanceTest 클래스를 실행합니다.

## 학습 포인트

### 1. Shape 클래스
* 상속용 클래스의 올바른 문서화
* @implSpec 사용 방법
* protected 메서드 제공 방식

### 2. Rectangle 클래스
* 상위 클래스의 문서화된 규약 준수
* 올바른 메서드 재정의 방법
* super 호출의 중요성

### 3. ProhibitedInheritance 클래스
* final 클래스 구현
* 정적 팩터리와 private 생성자 활용

### 4. BadSuperClass 클래스
* 생성자의 재정의 가능 메서드 호출 문제
* 하위 클래스에서 발생할 수 있는 오류

## 기대 실행 결과
```
=== 올바른 상속 설계 테스트 ===
도형 그리기: Rectangle (빨간색)
테두리 그리기: 두께 2로 Rectangle 테두리 그림

=== 상속 금지 테스트 ===
final 클래스 인스턴스 생성
정적 팩터리로 생성된 인스턴스

=== 잘못된 상속 설계 테스트 ===
부모 생성자 호출
자식 메서드 호출 (초기화되지 않은 상태)
NPE 발생 가능성 있음
```

## 주요 학습 내용
1. 상속용 클래스 문서화 방법
2. protected 메서드 활용
3. 상속 금지 기법
4. 생성자 작성 시 주의사항

## 추가 실습 제안
1. 다른 도형 클래스 추가해보기
2. 상속 대신 컴포지션으로 변경해보기
3. Cloneable/Serializable 구현해보기
4. 추가 테스트 케이스 작성해보기