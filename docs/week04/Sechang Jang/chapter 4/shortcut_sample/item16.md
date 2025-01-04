
# Item 16: public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라


## Why? (왜 이렇게 해야 할까?)

1. **캡슐화**: 클래스 내부 구현을 숨기고, 외부와의 상호작용을 통제할 수 있음.
2. **유지보수성**: 내부 필드 표현 방식을 바꿔도 API는 그대로 유지 가능.
3. **불변성 보장**: 유효성 검사를 통해 잘못된 값 설정을 방지.
4. **API 안정성**: public 필드는 외부에 노출되어 변경이 어려움 → 접근자 메서드로 유연하게 관리.

**요약**: “public 필드 노출은 유혹적이지만, 나중에 큰 후회로 돌아옵니다. 접근자 메서드로 제어하세요.”


## Please Do (이렇게 하세요!)

### 1. **접근자 메서드를 사용해 캡슐화**
```java
public class GoodExample {
    private int age;  // 외부에서 직접 접근 불가

    public int getAge() { 
        return age; 
    }

    public void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("나이는 음수가 될 수 없습니다.");
        }
        this.age = age;
    }
}
```

- **장점**: 
  - 나이에 대한 유효성 검사 가능
  - 나이 계산 로직 변경 시 API 유지 가능


### 2. **불변 데이터는 초기화 후 수정 불가**
```java
public class ImmutableExample {
    private final int width;  // 불변 필드
    private final int height;

    public ImmutableExample(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("크기는 음수일 수 없습니다.");
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
```

- **장점**:
  - 객체의 일관성을 유지
  - 불변식 보장


### 3. **필드 변경 시 부수 작업 수행**
```java
public class Notifier {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        notifyChange();  // 상태 변경 시 이벤트 발생
    }

    private void notifyChange() {
        System.out.println("상태가 변경되었습니다: " + status);
    }
}
```

- **장점**:
  - 상태 변경을 감지하고 추가 작업을 자동으로 수행

---

## Please Don't (이렇게는 하지 마세요!)

### 1. **public 필드를 노출**
```java
public class BadExample {
    public int age;  // 누구나 수정 가능
}
```

- **문제점**: 
  - 나이에 대한 유효성 검사 불가능
  - API 변경 시 모든 클라이언트 코드 수정 필요


### 2. **유효성 검사를 생략**
```java
public class AnotherBadExample {
    private int age;

    public void setAge(int age) {
        this.age = age;  // 유효성 검사 없음
    }
}
```

- **문제점**:
  - 잘못된 값 설정 가능 → 시스템 전체에 오류 유발


### 3. **불변 데이터를 노출**
```java
public class MutableArray {
    public final String[] names = {"Alice", "Bob"};  // 배열 노출
}

// 클라이언트 코드
mutableArray.names[0] = "Hacked!";  // 데이터가 변조됨
```

- **문제점**:
  - 외부에서 데이터 변경 가능 → 데이터 안정성 손실


## 요약 (Cheat Sheet)

- **Do**: private 필드 + getter/setter 사용. 
- **Do**: 불변 데이터는 변경 불가능하게 설계.
- **Don’t**: public 필드를 직접 노출.
- **Don’t**: 유효성 검사나 추가 작업을 생략.