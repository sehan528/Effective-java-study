# Item 2: 생성자에 매개변수가 많다면 빌더를 고려하라

빌더를 이해하기 전 객체를 생성하는 기존 방법 (Telescoping, JavaBeans) 에 대해 알아보자.

## 1. 점층적 생성자 패턴 (Telescoping Constructor Pattern)
가장 단순하지만 확장이 어려운 패턴입니다.

```java
public class NutritionFacts {
    private final int servingSize;  // 필수
    private final int servings;     // 필수
    private final int calories;     // 선택
    private final int fat;          // 선택
    private final int sodium;       // 선택
    private final int carbohydrate; // 선택

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
    }
}
```

- 필수 값(servingSize, servings)부터 시작해서 선택적 매개변수를 하나씩 추가하며 생성자를 만듦
- 사용 예: `NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 35, 27);`

**단점:**
- 매개변수가 많아지면 클라이언트 코드를 작성하기 어려움
- 같은 타입의 매개변수가 연속될 경우 실수하기 쉬움
- 매개변수 순서를 바꾸면 컴파일러가 찾지 못하는 버그 발생 가능

## 2. 자바빈즈 패턴 (JavaBeans Pattern)
기본 생성자로 객체를 만들고 setter로 값을 설정하는 방식입니다.

```java
public class NutritionFacts {
    private int servingSize = -1;  // 필수
    private int servings = -1;     // 필수
    private int calories = 0;      // 선택
    private int fat = 0;          // 선택
    private int sodium = 0;       // 선택
    private int carbohydrate = 0; // 선택

    public NutritionFacts() { }

    // Setters
    public void setServingSize(int servingSize) { this.servingSize = servingSize; }
    public void setServings(int servings) { this.servings = servings; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setFat(int fat) { this.fat = fat; }
    public void setSodium(int sodium) { this.sodium = sodium; }
    public void setCarbohydrate(int carbohydrate) { this.carbohydrate = carbohydrate; }
}
```

- 매개변수가 없는 생성자로 객체를 만들고 setter로 값을 설정
- 사용 예:
```java
NutritionFacts cocaCola = new NutritionFacts();
cocaCola.setServingSize(240);
cocaCola.setServings(8);
cocaCola.setCalories(100);
cocaCola.setSodium(35);
cocaCola.setCarbohydrate(27);
```

**단점:**
- 객체 하나를 만들려면 여러 메서드 호출 필요
- 객체가 완전히 생성되기 전까지 일관성(consistency)이 무너진 상태
- 불변 객체로 만들 수 없음

## 3. 빌더 패턴 (Builder Pattern)
점층적 생성자 패턴의 안전성 + 자바빈즈 패턴의 가독성을 겸비한 패턴입니다.

### 순수 자바로 구현한 빌더 패턴
```java
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 - 기본값으로 초기화
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) { calories = val; return this; }
        public Builder fat(int val) { fat = val; return this; }
        public Builder sodium(int val) { sodium = val; return this; }
        public Builder carbohydrate(int val) { carbohydrate = val; return this; }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }
}
```

### Lombok을 활용한 빌더 패턴
```java
@Data
@Builder(builderMethodName = "hiddenBuilder")
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    @Builder.Default private final int calories = 0;
    @Builder.Default private final int fat = 0;
    @Builder.Default private final int sodium = 0;
    @Builder.Default private final int carbohydrate = 0;

    // 필수 값을 강제하는 정적 팩터리 메서드
    public static NutritionFactsBuilder builder(int servingSize, int servings) {
        return hiddenBuilder()
                .servingSize(servingSize)
                .servings(servings);
    }
}
```

**사용 예시:**
```java
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
        .calories(100)
        .sodium(35)
        .carbohydrate(27)
        .build();
```

**장점:**
1. 객체 생성이 직관적이고 읽기 쉬움
2. 매개변수 순서에 구애받지 않음
3. 필수 매개변수와 선택 매개변수를 구분할 수 있음
4. 불변 객체를 만들 수 있음
5. 계층적으로 설계된 클래스와 함께 사용하기 좋음

**단점:**
1. 객체 생성 전에 빌더부터 만들어야 함
2. 생성 비용이 추가됨 (성능에 민감한 상황에서 문제될 수 있음)
3. 코드가 장황해져 매개변수가 4개 이하면 오히려 생성자 패턴이 나을 수 있음

## 빌더 패턴의 계층적 사용
빌더 패턴은 계층적으로 설계된 클래스에서 특히 유용합니다.

```java
public abstract class Pizza {
    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }
    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);
        
        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();
        
        // 하위 클래스는 이 메서드를 재정의하여 this를 반환해야 함
        protected abstract T self();
    }

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone();
    }
}
```

- 재귀적 타입 한정을 이용한 하위 클래스에서도 형변환 없이 메서드 연쇄 지원
- `self()`를 통한 형변환 없는 메서드 연쇄 구현
- 공변 반환 타이핑을 통해 클라이언트가 형변환을 신경 쓰지 않아도 됨

## 결론
- 생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 고려하라
- 특히 매개변수 중 다수가 필수가 아니거나 같은 타입이면 더욱 그렇다
- 빌더는 점층적 생성자보다 클라이언트 코드를 읽고 쓰기가 훨씬 간결하고, 자바빈즈보다 훨씬 안전하다