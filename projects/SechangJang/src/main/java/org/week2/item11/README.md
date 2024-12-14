# Item 11 예제 실행 가이드

## 프로젝트 구조
```
item11/
├── PhoneNumber.java         - 기본 전화번호 클래스 (hashCode 구현)
├── LazyPhoneNumber.java     - 지연 초기화 예시
└── HashCodeTest.java        - 테스트 실행 클래스
```

## 실행 방법
1. 모든 Java 파일을 item11 패키지 아래에 위치시킵니다.
2. HashCodeTest 클래스를 실행합니다.

## 학습 포인트

### 1. PhoneNumber 클래스
- equals와 hashCode의 올바른 쌍 구현
- 전형적인 hashCode 구현 방식
- Objects.hash를 사용한 간단한 구현

### 2. LazyPhoneNumber 클래스
- hashCode 지연 초기화 패턴
- 성능과 스레드 안전성 고려사항

### 3. 실행 결과 분석
- HashMap/HashSet 동작 확인
- hashCode 구현 방식에 따른 성능 비교

## 기대 실행 결과
```
올바른 hashCode 구현시 조회: 제니
HashSet 포함 여부: true

성능 비교:
일반 구현: XXX ms
Objects.hash: YYY ms
```

## 주요 학습 내용
1. equals와 hashCode의 관계
2. 해시 기반 컬렉션에서의 동작 방식
3. 다양한 hashCode 구현 방식과 성능 특성
4. 지연 초기화 패턴의 활용