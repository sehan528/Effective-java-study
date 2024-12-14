# Item 13 예제 실행 가이드

## 프로젝트 구조
```
item13/
├── PhoneNumber.java        - 기본 타입 필드만 가진 클래스의 clone 예제
├── Stack.java             - 가변 객체를 포함하는 클래스의 clone 예제
├── HashTable.java         - 복잡한 자료구조의 clone 예제
└── CloneTest.java         - 테스트 실행 클래스
```

## 실행 방법
1. 모든 Java 파일을 item13 패키지 아래에 위치시킵니다.
2. CloneTest 클래스를 실행합니다.

## 학습 포인트

### 1. PhoneNumber 클래스
* Cloneable 인터페이스 구현
* 기본 타입 필드에 대한 clone 구현
* 공변 반환 타입 활용
* 불변 객체의 clone 처리

### 2. Stack 클래스
* 가변 객체를 포함하는 클래스의 clone
* 배열의 복사본 생성
* clone 실패 시 예외 처리
* 깊은 복사와 얕은 복사의 차이

### 3. HashTable 클래스
* 연결 리스트를 포함한 복잡한 자료구조의 clone
* 깊은 복사 구현
* 내부 클래스의 복사 처리
* 순환 참조 처리

### 4. 실행 결과 분석
* 객체 동일성 vs 동등성 검증
* clone 메서드의 규약 준수 확인
* 복사본의 독립성 검증

## 기대 실행 결과
```
=== PhoneNumber Clone 테스트 ===
원본: 123-456-7890
복제본: 123-456-7890
원본 == 복제본: false
원본.equals(복제본): true

=== Stack Clone 테스트 ===
원본 스택에서 pop: second
복제된 스택에서 pop: second
스택이 독립적으로 동작함 확인

=== HashTable Clone 테스트 ===
원본과 복제본이 다른 객체임: true
깊은 복사가 제대로 수행됨
```

## 주요 학습 내용
1. Cloneable 인터페이스의 올바른 구현 방법
2. 가변 객체와 불변 객체의 clone 차이점
3. 깊은 복사와 얕은 복사의 적절한 사용
4. clone 메서드의 재정의 시 고려사항
5. 복사 생성자와 복사 팩터리 메서드의 대안 검토

## 추가 실습 제안
1. equals와 hashCode 메서드도 함께 구현해보기
2. 복사 생성자를 사용한 버전으로 변경해보기
3. 다양한 필드 타입에 대한 clone 동작 테스트
4. 순환 참조가 있는 자료구조에서의 clone 구현