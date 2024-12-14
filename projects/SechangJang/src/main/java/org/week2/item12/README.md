# Item 12 예제 실행 가이드

## 프로젝트 구조
```
item12/
├── PhoneNumber.java         - 포맷을 명시한 toString 예제
├── Medicine.java           - 포맷을 명시하지 않은 toString 예제
└── ToStringTest.java       - 테스트 실행 클래스
```

## 실행 방법
1. 모든 Java 파일을 item12 패키지 아래에 위치시킵니다.
2. ToStringTest 클래스를 실행합니다.

## 학습 포인트

### 1. PhoneNumber 클래스
- 명확한 포맷을 가진 toString 구현
- 포맷 문서화
- 접근자 메서드 제공

### 2. Medicine 클래스
- 유연한 포맷의 toString 구현
- 향후 변경 가능성을 고려한 문서화

### 3. 실행 결과 분석
- toString의 다양한 활용 사례
- 컬렉션에서의 활용
- 디버깅 메시지에서의 활용

## 기대 실행 결과
```
전화번호: 707-867-5309
전화번호부: {707-867-5309=제니}
약물 정보: [약물 9: 유형-사랑, 냄새=테러빈유]
에러 발생: IllegalArgumentException: area code: 1000
```

## 주요 학습 내용
1. toString 메서드의 목적과 활용
2. 포맷 명시 여부에 따른 장단점
3. 접근자 메서드 제공의 중요성
4. 디버깅과 로깅에서의 활용