# Item 20 예제 실행 가이드

## 프로젝트 구조
```
item20/
├── PaymentService.java          - 기본 인터페이스
├── AbstractPaymentService.java  - 골격 구현 클래스
├── CreditCardPayment.java      - 구체 구현 클래스
├── MixinExample.java           - 믹스인 패턴 예제
└── InterfaceTest.java          - 테스트 실행 클래스
```

## 실행 방법
1. 모든 Java 파일을 item20 패키지 아래에 위치시킵니다.
2. InterfaceTest 클래스를 실행합니다.

## 학습 포인트

### 1. 인터페이스와 디폴트 메서드
* 결제 시스템 인터페이스 설계
* 디폴트 메서드를 통한 기본 구현 제공
* 확장 가능한 API 설계

### 2. 골격 구현 패턴
* 공통 코드 재사용
* 추상 클래스를 통한 기능 제공
* 템플릿 메서드 패턴 활용

### 3. 다중 구현의 장점
* 믹스인 인터페이스 활용
* 유연한 기능 확장
* 계층 없는 타입 설계

## 기대 실행 결과
```
=== 결제 시스템 테스트 ===
기본 보안 검사 수행...
신용카드 결제 진행: $100.00
결제 완료 알림 전송

=== 다중 구현 테스트 ===
로깅 기능 추가됨
보안 기능 추가됨
신용카드 결제 처리중...

=== 골격 구현 테스트 ===
결제 전 유효성 검사
표준 결제 프로세스 시작
결제 완료 후 처리
```

## 주요 학습 내용
1. 인터페이스 설계 원칙
2. 디폴트 메서드 활용
3. 골격 구현 패턴 적용
4. 다중 구현을 통한 기능 확장

## 추가 실습 제안
1. 새로운 결제 방식 추가해보기
2. 다른 믹스인 인터페이스 만들어보기
3. 골격 구현 확장해보기
4. 다중 인터페이스 상황 처리해보기