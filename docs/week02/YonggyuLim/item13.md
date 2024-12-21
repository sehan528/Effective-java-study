
불변객체는 `clone` 해도 별 문제가 생기지 않음 애초에 불변객체는 굳이 clone 할 이유가 없음

## 가변객체의 클론
```java
@Override
    protected Object clone() throws CloneNotSupportedException {
        try {
            Stack result = (Stack) super.clone();
            result.elements = elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
```

`clone`을 재귀적으로 호출해서 복사 result 객체는 원본 객체와 다른 참조값을 갖지만
내부의 배열은 원본과 똑같은 참조값을 갖음 (얕은 복사를 하기 때문)

원본의 배열도 건들면 치명적인 상황(멀티 스레드 환경)이 나올 수 있으므로 그 상황을 피하기 위한 방법으로 깊은 복사를 할 수 있음

## 깊은 복사
- 깊은복사의 재귀 하는 방식 하지만 이 방식은 체인의 길이가 길지 않을때만 효율적 (스택 오버플로우 위험)하지만 이 방식은 체인의 길이가 길지 않을때만 효율적 (스택 오버플로우 위험)
```java
        Entry deepCopy() {
            return new Entry(key, value,
                    next == null ? null : next.deepCopy());
        }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
```

- 깊은 복사의 반복문 방식 체인이 길어도 안정적, 체인이 길거나, 스택 사용을 피해야 할때 적합함
```java
 Entry deepCopy() {
    Entry result = new Entry(key, value, next);
    for (Entry p = result; p.next != null; p = p.next) {
        p.next = new Entry(p.next.key, p.next.value, p.next.next);
    }
    return result;
}

@Override
    public Object clone() throws CloneNotSupportedException {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
```

## 상속용 클래스 에서는 `Cloneable`의 구현을 하면 안됨
- 상속용 클래스에는 `Cloneable` 구현하면 안됨
- 상속 클래스에서 활용시 하위 클래스의 필드가 제대로 복사 되지 않을 수 있고 불변성이 깨질 수 있음
- `protected` 로 선언후 `CloneNotSupportedException` 으로 던지거나
- 아래 처럼 `clone` 메서드를 하위 클래스에서 사용하지 못하게 퇴화 시키면 됨

```java
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
```

## `변환(복사) 생성자`, `변환(복사) 팩토리` 방법
- 배열을 제외한 복사 방법에서는 `clone` 보다는 `변환(복사) 생성자`, `변환(복사) 팩토리`를 사용하는게 유리
- `변환 생성자`, `변환 팩토리 메서드`의 장점
1. 깊은 복사 구현 용이
2. 예외 처리 불필요
3. 상속 문제 해결

```java
public class Example {
    private final int value;

    private Example(int value) {
        this.value = value;
    }

    //변환 생성자 메서드
    public Example(Example other) {
        this.value = other.value;
    }

    //변환 팩토리 메서드
    public static Example copyOf(Example other) {
        return new Example(other.value);
    }
}

```


