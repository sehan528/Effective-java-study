# Item 15: 클래스와 멤버의 접근 권한을 최소화하라

## 1. 기본 원칙

### 1.1 정보 은닉의 중요성
프로그램 요소의 접근성을 가능한 한 최소한으로 해야 하는 이유는 **잘 설계된 컴포넌트**와 직접적인 연관이 있습니다. 여기서 잘 설계된 컴포넌트란:
- 클래스 내부 데이터와 구현 정보를 외부 컴포넌트로부터 얼마나 잘 숨겼는가로 평가됩니다.
- 오직 API를 통해서만 다른 컴포넌트와 소통하며 서로의 내부 동작 방식에는 전혀 개의치 않습니다.

이러한 정보 은닉의 장점은 다음과 같습니다:

1. **시스템 개발 속도 향상**
   - 여러 컴포넌트를 병렬로 개발할 수 있기 때문입니다.

2. **시스템 관리 비용 감소**
   - 각 컴포넌트를 더 빨리 파악할 수 있습니다.
   - 디버깅이 용이합니다.
   - 다른 컴포넌트로 교체하는 부담도 적습니다.

3. **성능 최적화에 도움**
   - 완성된 시스템을 프로파일링해 최적화할 컴포넌트를 정확히 찾을 수 있습니다.
   - 다른 컴포넌트에 영향을 주지 않고 해당 컴포넌트만 최적화할 수 있습니다.

4. **소프트웨어 재사용성 증가**
   - 의존성이 낮은 컴포넌트는 그 자체로 독립적인 가치가 있습니다.
   - 낯선 환경에서도 유용하게 쓰일 가능성이 큽니다.

5. **큰 시스템 제작 난이도 감소**
   - 시스템 전체가 완성되지 않은 상태에서도 개별 컴포넌트의 동작을 검증할 수 있습니다.

## 2. 접근 제한자의 올바른 사용

### 2.1 기본 원칙
모든 클래스와 멤버의 접근성을 가능한 한 좁혀야 합니다. 달리 말하면:
- 소프트웨어가 올바로 동작하는 한 가장 낮은 접근 수준을 부여해야 합니다.

### 2.2 톱레벨 클래스와 인터페이스
가능한 두 가지 접근 수준:
1. **package-private**: 패키지 내에서만 사용
2. **public**: 공개 API로 사용

주의사항:
- public 클래스는 그 패키지의 API인 반면, package-private 클래스는 내부 구현에 속합니다.
- 한 클래스에서만 사용하는 package-private 클래스는 해당 클래스의 private static 중첩 클래스로 구현을 고려해야 합니다.

### 2.3 클래스의 멤버(필드, 메서드, 중첩 클래스, 중첩 인터페이스)
가능한 네 가지 접근 수준:

1. **private**: 멤버를 선언한 톱레벨 클래스에서만 접근 가능
2. **package-private**: 멤버가 소속된 패키지 안의 모든 클래스에서 접근 가능
3. **protected**: package-private의 접근 범위를 포함하며, 이 멤버를 선언한 클래스의 하위 클래스에서도 접근 가능
4. **public**: 모든 곳에서 접근 가능

### 2.4 멤버 접근성 제한 원칙

1. **public 클래스의 인스턴스 필드는 되도록 public이 아니어야 합니다.**
   - 이유: 필드가 가변 객체를 참조하거나, final이 아닌 인스턴스 필드를 public으로 선언하면 그 필드에 담을 수 있는 값을 제한할 수 없습니다.
   - 결과: 스레드 안전성을 보장할 수 없습니다.

2. **예외: public static final 필드**
   - 조건: 해당 클래스가 표현하는 추상 개념을 완성하는 데 꼭 필요한 구성요소로써의 상수인 경우
   - 주의: 반드시 기본 타입이나 불변 객체를 참조해야 합니다.

3. **주의: 길이가 0이 아닌 배열은 모두 변경 가능**
   ```java
   // 보안 허점이 존재하는 코드
   public static final Thing[] VALUES = { ... };
   ```

   해결 방법:
   ```java
   // 방법 1: private 배열 + public 불변 리스트
   private static final Thing[] PRIVATE_VALUES = { ... };
   public static final List<Thing> VALUES = 
       Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

   // 방법 2: private 배열 + 복사본 반환
   private static final Thing[] PRIVATE_VALUES = { ... };
   public static final Thing[] values() {
       return PRIVATE_VALUES.clone();
   }
   ```

## 3. 모듈 시스템 (Java 9+)

Java 9에서 추가된 모듈 시스템으로 두 가지 암묵적 접근 수준이 추가되었습니다:
- 패키지가 클래스들의 묶음이듯, 모듈은 패키지들의 묶음입니다.
- 모듈은 자신이 속하는 패키지 중 공개(export)할 것들을 선언할 수 있습니다.
- 그러나 비모듈 방식이 여전히 주로 사용되고 있습니다.

## 4. 정리

프로그램 요소의 접근성은 가능한 한 최소한으로 하되:
1. 각 요소의 접근성을 최대한 좁힐 것
2. 단, 공개 API는 신중히 설계한 후 그 외의 모든 멤버는 private으로 만들 것
3. private 멤버를 package-private까지 풀어주는 일은 자주 있을 수 있으나, 그 이상은 안됨
4. 권한을 풀어주는 일이 잦다면 컴포넌트를 더 분해해야 하는 신호일 수 있음
5. public 클래스는 상수용 public static final 필드 외에는 어떠한 public 필드도 가져서는 안됨