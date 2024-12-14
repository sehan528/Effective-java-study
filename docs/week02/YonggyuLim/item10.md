# `equals` 메서드 재정의 규약과 필요한 경우

## `equals` 메서드를 재정의해야 하는 경우

### 1. 각 인스턴스가 본질적으로 고유하지 않고, 값에 따라 동등성을 비교해야 하는 경우
- 값 객체(Value Object)는 논리적 동치성을 비교하기 위해 `equals` 메서드를 재정의해야 한다.
- 예시: `Integer`, `String` 등 값이 중요하고, 값이 같은지 비교해야 하는 클래스들.

### 2. 인스턴스의 논리적 동치성을 검사해야 하는 경우
- 두 객체가 동등한 값을 가졌을 때 `equals`가 `true`를 반환하도록 해야 한다.

### 3. 상위 클래스에서 이미 재정의한 `equals` 메서드가 하위 클래스에도 적합한 경우
- 상속받은 클래스가 부모 클래스에서 제공한 `equals` 메서드를 그대로 사용해도 무방할 때.

### 4. 클래스가 `private`이거나 `package-private`이고 `equals` 메서드를 호출할 일이 없는 경우
- 외부에서 `equals` 메서드를 호출할 일이 없다면 `equals`를 재정의할 필요가 없을 수 있다.

## `equals` 메서드 일반 규약

`equals` 메서드를 재정의할 때는 다음과 같은 규약을 따라야 한다.

### 1. **반사성 (Reflexivity)**
- null이 아닌 모든 값에 대해 참조 값 `x`에 대해 `x.equals(x)`는 항상 `true`이어야 한다.
- 예: `someObject.equals(someObject)`는 항상 `true`이다.

### 2. **대칭성 (Symmetry)**
- `x.equals(y)`가 `true`이면 `y.equals(x)`도 `true`여야 한다.
- 예: 만약 `x.equals(y)`가 `true`라면 `y.equals(x)`도 반드시 `true`여야 한다.

### 3. **추이성 (Transitivity)**
- `x.equals(y)`가 `true`이고 `y.equals(z)`가 `true`라면 `x.equals(z)`도 `true`이어야 한다.
- 예: `a.equals(b)`가 `true`이고, `b.equals(c)`가 `true`라면, `a.equals(c)`도 `true`여야 한다.

### 4. **일관성 (Consistency)**
- `x.equals(y)`를 여러 번 호출해도 항상 일관된 결과를 반환해야 한다. 즉, `true` 또는 `false`가 계속 동일해야 한다.
- 예: `a.equals(b)`가 `true`라면, 같은 객체에 대해 여러 번 호출해도 항상 `true`여야 한다.

### 5. **Null-아님 (Non-null)**
- `null`이 아닌 모든 참조 값 `x`에 대해 `x.equals(null)`은 반드시 `false`여야 한다.
- 예: `someObject.equals(null)`은 항상 `false`여야 한다.

## `equals` 메서드를 재정의하면서 값을 추가할 때의 문제점

- **문제**: `equals` 메서드를 재정의하여 값을 추가할 경우, **구체 클래스를 확장**하면서 새 값을 추가하는 것은 `equals` 규약을 만족시키는 데 어려움이 있을 수 있다.
- **해결 방법**:
    - `getClass()`를 사용하여 정확한 타입을 검사하면 문제를 해결할 수 있다. 하지만 이는 **리스코프 치환 원칙 (Liskov Substitution Principle, LSP)**을 위반할 수 있다.
    - **리스코프 치환 원칙**: 상위 타입의 객체는 하위 타입의 객체로 교체해도 기존 프로그램이 올바르게 동작해야 한다는 원칙이다. `instanceof`나 `getClass()`를 사용하면 이 원칙을 위배할 수 있다.

## `equals` 메서드 재정의 시 우회 방법

### 1. **상속 대신 컴포지션을 사용**
- 패키지 ex4의 ColorPoint 클래스 참조
- 상속 대신 **컴포지션**을 사용하면, 하위 클래스에서 추가된 값이 상위 클래스의 `equals` 메서드 규약을 방해하지 않도록 할 수 있다.
- 예시: `Point`를 상속하는 대신 `Point` 객체를 필드로 가지는 방식.

### 2. **추상 클래스를 활용**
- 추상 클래스를 활용하여 `equals` 메서드를 필요한 대로 구현할 수 있다.
- 하위 클래스에서 `equals`를 재정의하되, 상위 클래스에서 제공하는 규약을 지킬 수 있는 방법을 선택한다.

