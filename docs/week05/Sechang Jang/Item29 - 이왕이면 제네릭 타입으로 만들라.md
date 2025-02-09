코드를 작성하다 보면 **같은 기능을 수행하는 클래스가 타입만 다른 경우**를 마주하게 된다. (대표적인 예로 API 등..) 예를 들어, `Integer`를 저장하는 스택이 필요해 `IntegerStack`을 만들었는데, 이후 `StringStack`이 필요해 또다시 유사한 클래스를 만드는 경우가 있다. 이럴 경우, **중복 코드가 늘어나고 유지보수가 어려워지는 문제**가 발생합니다. 이 문제를 해결하기 위해 이 문제를 해결하기 위해 `Object` 기반 클래스를 사용하는 경우도 있지만, 불 필요한 형변환과 런타임 오류 **(`ClassCastException`)가 발생한다.**

이번 아이템에선 코드의 안정성과 디버깅 시간을 감소를 위해 **클래스를 제네릭 타입으로 만들어 타입 안전성을 확보하는 방법** 과 **제네릭하지 않은 클래스를 제네릭으로 변환하는 과정**을 학습한다. 기존에 **제네릭하지 않은 클래스를 제네릭으로 변환하는 과정에서 생길 수 있는 문제** (제네릭 배열 생성 오류, 힙 오염 (Heap Pollution) 에 대해서도 학습함으로서 **데이터를 제네릭하게 처리하는 방법**을 알아본다.

---

## 일반 클래스를 제네릭 타입으로 변경.

- 일반 클래스를 제네릭 타입 클래스로 바꾼다고 해도 클라이언트에는 아무런 해가 없다.

**[Object 기반 스택 - 제네릭이 절실한 강력후보]**

```java
public class ObjectStack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public ObjectStack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제

        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

- **설명**:
    - 이 클래스는 `Object` 타입의 배열을 사용하여 스택을 구현합니다.
    - **문제점**:
        - `pop` 메서드는 `Object` 타입을 반환하므로, 클라이언트는 반환된 객체를 원하는 타입으로 매번 형변환해야 합니다.
        - 만약 스택에 다른 타입의 객체가 들어있다면, 런타임에 `ClassCastException`이 발생할 수 있습니다. → 스택 동작하는 도중에 타입 검증하는 경우의 수가 없다는 의미
    - **해결책**: 이 클래스를 제네릭 타입으로 변환하여 타입 안정성을 보장해야 합니다.

위와 같은 코드 상태로는 스택에서 꺼낸 객체를 형변환해야 하는데 이때 런타임 오류가 발생할 가능성이 있다. 따라서 제네릭 타입 클래스로 바꾸는것이 좋다. 일반 클래스에서 제네릭 타입 클래스로 만드는 시작은 타입 매개변수를 추가하는 것이다. 이때 타입이름으로 보통 `E`를 사용한다.

**[제네릭 스택으로 변경] // 초기 트라이**

```java
public class GenericStack<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public GenericStack() {
        this.elements = new E[DEFAULT_INITIAL_CAPACITY]; // 오류발생
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        E result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제

        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

- **설명**:
    - 이 클래스는 제네릭 타입 `E`를 사용하여 스택을 구현하려고 합니다.
    - **문제점**:
        - `new E[DEFAULT_INITIAL_CAPACITY];`에서 오류가 발생합니다.
        - 제네릭 타입 `E`는 **실체화 불가 타입**이므로, 배열을 생성할 수 없습니다.
    

위 코드의 생성자에서 오류가 발생한다. [아이템 28](https://velog.io/@alkwen0996/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-%EC%95%84%EC%9D%B4%ED%85%9C28-%EB%B0%B0%EC%97%B4%EB%B3%B4%EB%8B%A4%EB%8A%94-%EB%A6%AC%EC%8A%A4%ED%8A%B8%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC)에서 설명한 것처럼 `E`와 같은 실체화 불가 타입으로는 배열을 만들 수 없기 때문이다. 위 문제를 해결하는 두 가지 방법이 있다.

## 타입 매개변수 오류를 해결하는 두가지 방법

### **WAY 1. `Object` 배열을 생성하고 그 다음 제네릭 배열로 형변환하는 제네릭 배열 생성을 금지하는 제약을 대놓고 우회하는 방법.**

**[Object 배열 생성 시 배열 형변환]**

```java
...
public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
}

public GenericStack() {
    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY]; // 경고 메시지 발생
}

private void ensureCapacity() {
    if (elements.length == size) {
        elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
...
// 비검사 형변환 경고 메시지 발생
Unchecked cast: 'java.lang.Object[]' to 'E[]
```

- **설명**:
    - 핵심 : 초기 할당 시, `Object` 로 배열을 생성한 다음 제네릭 배열로 형변환합니다.
    - 이 방법은 **비검사 형변환 경고**를 발생시키지만, `@SuppressWarnings("unchecked")`를 사용하여 경고를 숨길 수 있습니다.
    - **장점**:
        - 코드가 간결하고 가독성이 좋습니다.
        - 배열을 한 번만 생성하면 됩니다.
    - **단점**:
        - 힙 오염(Heap Pollution)이 발생할 수 있습니다. (배열의 런타임 타입이 `Object[]`이지만, 컴파일 타임 타입은 `E[]`입니다.)

- 위 코드에서는 비검사 형변환 경고 메시지가 발생한다.
- 위 코드에서 `elements` 배열은 `private` 필드에 저장된 후 클라이언트로 반환되거나 다른 메서드에 전달되지 않는다.
- `push` 메서드를 통해 배열에 저장되는 원소의 타입은 항상 `E`이다.
- 위 두가지 이유로 이 비검사 형변환은 확실히 안전하다.
- 비검사 형변환 경고를 없애기 위해 범위를 최대한 줄여 `@SuppressWarnings("unchecked")`를 사용하여 경고를 숨긴다.

**[비검사 형변환 경고 숨김]**

```java
...
public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
}

// 배열 elements는 push(E)로 넘어온 E인스턴스만 남는다.
// 타입 안정성을 보장하지만 이 배열의 런타임 타입은 Object[] 이다.
@SuppressWarnings("unchecked")
public GenericStack() {
    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY]; // 경고 메시지 발생
}

private void ensureCapacity() {
    if (elements.length == size) {
        elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
...
// 비검사 형변환 경고 메시지 발생
Unchecked cast: 'java.lang.Object[]' to 'E[]
```

- 생성자가 배열 생성말고는 따로 하는 기능이 없기 때문에 생성자 전체에서 경고를 숨겨도 된다.

### **WAY 2. `elements` 필드의 타입을 `E[]`에서 `Object[]`로 바꾸는 방법.**

**[Object 배열은 그대로 두고 pop() 사용시 형변환]**

```java
// 비검사 경고를 적절히 숨긴다.
public class GenericStack<E> {
    private Object[] elements;
	...
    public GenericStack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
		// push에서 E 타입만 허용하므로 이 형변환은 안전하다.
        @SuppressWarnings("unchecked")
        E result = (E) elements[--size];
        elements[size] = null; // 다 쓴 참조 해제

        return result;
    }
	...
}
```

- **설명**:
    - `elements` 필드의 타입을 `Object[]`로 변경하고, `pop` 메서드에서 `E`로 형변환합니다.
    - **장점**:
        - 힙 오염이 발생하지 않습니다.
    - **단점**:
        - `pop` 메서드에서 매번 형변환이 필요합니다.
        - 코드가 조금 더 복잡해집니다.

## 제네릭 배열 생성을 제거하는 두 방법의 장단점

| **방법** | **장점** | **단점** |
| --- | --- | --- |
| **`Object` 배열 형변환** | 코드가 간결하고 가독성이 좋음. 배열을 한 번만 생성하면 됨. | 힙 오염이 발생할 수 있음. |
| **`Object[]` 사용 및 형변환** | 힙 오염이 발생하지 않음. | `pop` 메서드에서 매번 형변환이 필요함. 코드가 조금 더 복잡해짐. |

## 정리

- **제네릭 타입**을 사용하면 타입 안정성을 보장할 수 있고, 클라이언트가 형변환을 할 필요가 없어집니다.
- 제네릭 배열 생성을 우회하는 두 가지 방법 중, 상황에 맞는 방법을 선택하여 사용하면 됩니다.
    - **첫 번째 방법**: 코드가 간결하고 가독성이 좋지만, 힙 오염이 발생할 수 있습니다.
    - **두 번째 방법**: 힙 오염이 발생하지 않지만, 매번 형변환이 필요합니다.