# Item 24: 멤버 클래스는 되도록 static으로 만들라

## 중첩 클래스(Nested Class)의 종류

### 1. 정적 멤버 클래스
```java
public class OuterClass {
    // 정적 멤버 클래스
    public static class StaticMemberClass {
        void doSomething() {
            // 외부 클래스 참조 없이 독립적으로 동작
        }
    }
}

// 사용 예
OuterClass.StaticMemberClass staticMember = new OuterClass.StaticMemberClass();
```

### 2. (비정적) 멤버 클래스
```java
public class OuterClass {
    private String outerField = "outer";
    
    // 비정적 멤버 클래스
    public class MemberClass {
        void doSomething() {
            // 외부 클래스의 필드에 직접 접근 가능
            System.out.println(outerField);
            System.out.println(OuterClass.this.outerField);
        }
    }
}

// 사용 예
OuterClass outer = new OuterClass();
OuterClass.MemberClass member = outer.new MemberClass();
```

### 3. 익명 클래스
```java
public interface MyInterface {
    void doSomething();
}

// 익명 클래스 사용 예
MyInterface anonymous = new MyInterface() {
    @Override
    public void doSomething() {
        System.out.println("Anonymous class");
    }
};
```

### 4. 지역 클래스
```java
public class OuterClass {
    void someMethod() {
        // 지역 클래스
        class LocalClass {
            void doSomething() {
                System.out.println("Local class");
            }
        }
        LocalClass local = new LocalClass();
        local.doSomething();
    }
}
```

## 정적 멤버 클래스 사용이 권장되는 이유

### 1. 메모리 효율성
- 외부 클래스에 대한 참조를 저장하지 않음
- 불필요한 객체 참조가 없어 메모리 누수 방지

### 2. 성능 향상
```java
// 비정적 멤버 클래스 - 비권장
public class OuterClass {
    public class NonStaticMember {
        // 숨은 외부 클래스 참조 존재
    }
}

// 정적 멤버 클래스 - 권장
public class OuterClass {
    public static class StaticMember {
        // 독립적으로 존재
    }
}
```

### 3. 명확한 의도 전달
- 외부 클래스와의 독립성을 명시적으로 표현
- 코드 가독성 향상

## 비정적 멤버 클래스가 필요한 경우

### 1. 어댑터 패턴 구현
```java
public class MySet<E> extends AbstractSet<E> {
    // 어댑터로 사용되는 비정적 멤버 클래스
    private class MyIterator implements Iterator<E> {
        @Override
        public boolean hasNext() {
            // 외부 클래스의 상태에 접근 필요
            return false;
        }
        
        @Override
        public E next() {
            return null;
        }
    }
}
```

### 2. 외부 클래스 참조가 필요한 경우
```java
public class Calculator {
    private int value;
    
    // 계산 작업에 외부 상태가 필요한 경우
    public class Operation {
        public void add(int num) {
            value += num; // 외부 클래스의 value 사용
        }
    }
}
```

## 중첩 클래스 선택 가이드라인

### 1. 클래스 사용 범위에 따른 선택
```
if (클래스가 한 메서드 안에서만 사용) {
    if (인스턴스 생성 지점이 한 곳 && 적합한 인터페이스 존재) {
        익명 클래스 사용
    } else {
        지역 클래스 사용
    }
} else {
    멤버 클래스 사용
}
```

### 2. 멤버 클래스 static 여부 결정
```
if (바깥 인스턴스 참조 필요) {
    비정적 멤버 클래스 사용
} else {
    정적 멤버 클래스 사용
}
```

## 핵심 정리

1. **기본 원칙**
   - 멤버 클래스는 일단 static으로 만들어라
   - 필요한 경우에만 비정적으로 변경

2. **static 사용 시 장점**
   - 메모리 사용량 감소
   - 성능 향상
   - 의도 명확성

3. **비정적 멤버 클래스 사용 시점**
   - 어댑터 구현 시
   - 외부 클래스 참조가 반드시 필요할 때

4. **주의사항**
   - 불필요한 참조 보유는 메모리 누수의 원인
   - 참조가 눈에 보이지 않아 디버깅 어려움