# Item 25: 톱레벨 클래스는 한 파일에 하나만 담으라

## 핵심 개념 (Main Ideas)

### 1. 톱레벨 클래스의 분리 원칙
- **정의**: 소스 파일 하나당 톱레벨 클래스를 하나만 담아야 함
- **목적**: 컴파일 순서에 따른 예기치 않은 결과 방지
- **효과**: 코드의 명확성과 유지보수성 향상

### 2. 클래스 파일 분리의 중요성
- **원칙**: 각 톱레벨 클래스는 독립된 소스 파일에 위치
- **이유**: 컴파일 시 발생할 수 있는 모호성 제거
- **방법**: 클래스별로 별도의 .java 파일 생성

## 세부 내용 (Details)

### 1. 잘못된 클래스 파일 구성의 위험성

#### 한 파일에 여러 톱레벨 클래스를 정의한 경우
```java
// Main.java
class Main {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}

// Utensil.java
class Utensil {
    static final String NAME = "pan";
}
class Dessert {
    static final String NAME = "cake";
}

// Dessert.java
class Utensil {
    static final String NAME = "pot";
}
class Dessert {
    static final String NAME = "pie";
}
```

**이 코드가 초래할 수 있는 문제**:
1. **컴파일 순서 의존성**:
   - `javac Main.java Dessert.java` → 컴파일 오류
   - `javac Main.java` → "pancake" 출력
   - `javac Dessert.java Main.java` → "potpie" 출력

2. **유지보수 어려움**:
   - 동일한 클래스 정의가 여러 파일에 존재
   - 의도하지 않은 클래스 재정의 가능성
   - 코드 이해와 디버깅이 어려움

### 2. 올바른 클래스 파일 구성

#### 각 클래스를 별도 파일로 분리
```java
// Utensil.java
public class Utensil {
    static final String NAME = "pan";
}

// Dessert.java
public class Dessert {
    static final String NAME = "cake";
}

// Main.java
public class Main {
    public static void main(String[] args) {
        // 항상 동일한 결과 출력
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}
```

**올바른 구성의 이점**:
1. **명확성**:
   - 각 클래스의 위치가 명확
   - 코드 탐색이 용이
   - 의도하지 않은 중복 정의 방지

2. **유지보수성**:
   - 클래스별 독립적인 수정 가능
   - 버전 관리가 용이
   - 재사용성 향상

### 3. 정적 멤버 클래스를 활용한 대안

#### 여러 클래스를 한 파일에 담아야 할 경우
```java
// Kitchen.java
public class Kitchen {
    // 정적 멤버 클래스로 관련 클래스들을 그룹화
    private static class Utensil {
        static final String NAME = "pan";
        
        static void clean() {
            System.out.println("Cleaning " + NAME);
        }
    }
    
    private static class Dessert {
        static final String NAME = "cake";
        
        static void prepare() {
            System.out.println("Preparing " + NAME);
        }
    }
    
    // 외부에 제공할 API
    public static void prepareKitchen() {
        Utensil.clean();
        Dessert.prepare();
    }
}
```

**이 접근 방식의 장점**:
1. **캡슐화**:
   - 관련 클래스들을 논리적으로 그룹화
   - 내부 구현을 효과적으로 숨김
   - API의 복잡성 감소

2. **코드 구조화**:
   - 관련 기능들을 한 파일에서 관리
   - 클래스 간의 관계가 명확
   - 코드 응집도 향상

## 자주 발생하는 질문과 답변

Q: 여러 작은 유틸리티 클래스들을 어떻게 관리해야 할까요?
A: 다음과 같은 방법을 고려하세요:
```java
// StringUtils.java
public final class StringUtils {
    private StringUtils() {} // 인스턴스화 방지
    
    // 관련 유틸리티 클래스들을 정적 멤버 클래스로 구성
    private static class Validator {
        static boolean isEmpty(String str) {
            return str == null || str.trim().isEmpty();
        }
    }
    
    private static class Formatter {
        static String capitalize(String str) {
            if (Validator.isEmpty(str)) return str;
            return str.substring(0, 1).toUpperCase() + 
                   str.substring(1);
        }
    }
    
    // 공개 API
    public static boolean isEmpty(String str) {
        return Validator.isEmpty(str);
    }
    
    public static String capitalize(String str) {
        return Formatter.capitalize(str);
    }
}
```

Q: 테스트 코드에서는 어떻게 클래스를 구성해야 할까요?
A: 테스트의 특성에 따라 다음과 같이 구성할 수 있습니다:
```java
// ProductServiceTest.java
public class ProductServiceTest {
    // 테스트를 위한 도우미 클래스들
    private static class TestProduct {
        String name;
        double price;
        
        TestProduct(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }
    
    private static class TestProductBuilder {
        public static TestProduct createDefault() {
            return new TestProduct("Test Product", 9.99);
        }
    }
    
    @Test
    void testProductCreation() {
        TestProduct product = TestProductBuilder.createDefault();
        // 테스트 로직
    }
}
```

## 요약 (Summary)

1. **파일 구성 원칙**
   - 소스 파일당 하나의 톱레벨 클래스
   - 컴파일 순서 독립성 확보
   - 명확한 코드 구조 유지

2. **대안적 해결책**
   - 필요한 경우 정적 멤버 클래스 활용
   - 관련 클래스들의 논리적 그룹화
   - 적절한 캡슐화 수준 유지

3. **실무 적용 가이드**
   - 클래스 파일 분리 원칙 준수
   - 예외적인 경우 문서화
   - 유지보수성을 최우선 고려