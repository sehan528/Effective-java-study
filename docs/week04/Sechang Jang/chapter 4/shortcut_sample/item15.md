# Item 15: 클래스와 멤버의 접근 권한을 최소화하라

## Why? (왜 이렇게 해야 할까?)

- **더 좋은 코드 품질**: 다른 코드가 내 코드에 덜 의존하게 되어 유지보수가 쉬워짐.  
- **버그 예방**: 중요한 내부 데이터가 외부에서 실수로 수정되지 않음.  
- **안전성**: 데이터 구조와 로직의 변경이 시스템 전체에 영향을 미치지 않음.  
- **팀워크 향상**: 코드의 각 부분이 독립적으로 작동하므로 다른 팀원과의 충돌이 줄어듦.  

요약: **"내 코드가 다른 사람 때문에 망가지지 않고, 다른 사람 코드 때문에 내가 망가지지 않는다!"**


## Please Do (이렇게 하세요)  

### 1. **멤버 필드에는 `private`를 기본값으로**  
```java
public class GoodExample {
    private String name;  // 외부에서 직접 접근 불가
    private int age;      // 캡슐화로 안전하게 보호됨

    // 안전한 접근을 위해 getter와 setter 제공
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

### 2. **public 필드는 꼭 필요한 경우에만**  
- **불변 객체**를 사용해야 한다면 `public static final`을 허용.  
```java
public class Constants {
    public static final int MAX_USERS = 100;  // 변경 불가!
}
```

### 3. **배열은 절대 직접 노출하지 마세요**  
- 대신 **불변 리스트**나 **복사본 반환**을 사용.  
```java
public class GoodArrayExample {
    private static final String[] PRIVATE_VALUES = {"A", "B", "C"};

    public static final List<String> VALUES = 
        Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES)); // 불변 리스트로 노출
}
```

## Please Don't (이렇게는 하지 마세요)  

### 1. **모든 것을 public으로 만들지 마세요**  
```java
public class BadExample {
    public String name;  // 누구나 접근 가능, 마음대로 변경 가능
    public int age;      // 이 필드도 아무나 바꿀 수 있음
}
```
- **문제점**: 이름이나 나이를 코드 외부에서 마음대로 변경 가능 → **버그 유발**


### 2. **배열을 public으로 노출하지 마세요**  
```java
public class BadArrayExample {
    public static final String[] VALUES = {"A", "B", "C"}; // 누구나 수정 가능!
}
VALUES[0] = "Hacked!";  // 배열 내용이 수정되어 전체 시스템이 망가질 위험!
```


### 3. **필요하지 않은 protected 멤버를 사용하지 마세요**  
```java
public class BadInheritanceExample {
    protected int id;  // 외부에서 접근이 가능하고, 하위 클래스에서도 자유롭게 사용 가능
}
```
- **문제점**: 하위 클래스가 `id` 필드를 의존하게 되어, 내부 구현을 변경하기 어렵게 만듦.  


## 요약 (Cheat Sheet)

- **최소화**: 클래스와 멤버의 접근 권한은 가능한 한 낮춰라.  
- **안전하게**: public static final 배열은 쓰지 말고, 불변 리스트나 방어적 복사 사용.  
- **계획적으로**: public, protected 멤버는 꼭 필요한 경우에만 사용.  



