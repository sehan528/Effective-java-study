# Item 10 equals 메서드 예제 실행 가이드

## 프로젝트 구조
```
item10/
├── Point.java                  - 기본 2차원 좌표 클래스
├── ColorPoint.java             - 잘못된 상속 구현 예시
├── ColorPointComposition.java  - 컴포지션 패턴 적용 예시
├── CaseInsensitiveString.java  - 대칭성 위배 예시
├── Color.java                  - 색상 열거형
└── EqualsExampleTest.java      - 테스트 실행 클래스
```

## 실행 방법
1. 모든 Java 파일을 item10 패키지 아래에 위치시킵니다.
2. EqualsExampleTest 클래스를 실행합니다.

## 학습 포인트

### 1. Point 클래스
- equals 메서드의 기본적인 구현 방법
- 필수 단계: 참조 비교 → 타입 검사 → 캐스팅 → 필드 비교

### 2. ColorPoint 클래스
- 잘못된 상속 관계 예시
- equals 대칭성 위배 케이스
- Point와의 비교에서 발생하는 문제점

### 3. ColorPointComposition 클래스
- 컴포지션 패턴을 사용한 올바른 구현
- Point 객체를 필드로 포함
- equals 규약을 준수하는 방법

### 4. CaseInsensitiveString 클래스
- String과의 상호운용성 문제
- 대칭성 위배의 실제 예시

## 기대 실행 결과
```
=== Point equals 테스트 ===
p1.equals(p2): true
p1.equals(p3): false

=== ColorPoint equals 테스트 (잘못된 예시) ===
p.equals(cp): true
cp.equals(p): false

=== ColorPointComposition equals 테스트 (올바른 예시) ===
cp1.equals(cp2): true
cp1.equals(p): false

=== CaseInsensitiveString equals 테스트 ===
cis.equals(s): true
s.equals(cis): false
```

## 주요 학습 내용
1. equals 메서드의 올바른 구현 방법
2. 상속 관계에서 발생할 수 있는 equals 문제
3. 컴포지션을 통한 문제 해결
4. equals 규약(반사성, 대칭성, 추이성, 일관성)의 중요성