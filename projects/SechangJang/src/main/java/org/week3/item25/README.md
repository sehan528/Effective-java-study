### Item 25 예제 실행 가이드

## 프로젝트 구조
```
item25/
├── Main.java               - 톱레벨 클래스 중복 정의 문제와 해결법 테스트
├── IncorrectExample.java   - 톱레벨 클래스 중복 정의 문제를 보여주는 예제
└── CorrectExample.java     - 톱레벨 클래스를 분리하거나 중첩 클래스로 해결한 예제
```

## 실행 방법
1. 모든 Java 파일을 `item25` 디렉토리에 위치시킵니다.
2. 다음 명령어로 컴파일 및 실행합니다.
   ```bash
   javac Main.java IncorrectExample.java CorrectExample.java
   java item25.Main
   ```

## 학습 포인트
### 1. 톱레벨 클래스 중복 정의 문제
* 컴파일러가 어느 소스 파일을 먼저 처리하느냐에 따라 결과가 달라짐.
* 코드 관리와 디버깅이 어렵고 예기치 않은 동작이 발생할 가능성.

### 2. 문제 해결 방법
* 톱레벨 클래스는 한 파일에 하나씩만 작성.
* 필요한 경우, 정적 멤버 클래스로 전환해 관리.

## 기대 실행 결과
```
=== 톱레벨 클래스 중복 정의 문제 ===
컴파일 순서: Main.java -> IncorrectExample.java
결과: pancake

컴파일 순서: IncorrectExample.java -> Main.java
결과: potpie

=== 문제 해결 사례 ===
결과: pancake
톱레벨 클래스 분리로 일관성 유지
```
