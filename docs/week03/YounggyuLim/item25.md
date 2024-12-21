# 톱레벨 클래스는 한 파일에 하나만 담아라

- 아래의 경우 참조에 혼란이 올 수 있음
```java
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}

```

```java
public class DuplicateUtensil {
    class Utensil {
        static final String NAME = "spoon";
    }
}

```

- 굳이 톱클래스 파일에 클래스 두개를 작성해야 한다면
- 아래 처럼 정적 멤버 클래스로
- 그냥 클래스를 분리하는걸 권장
```java
public class Test {
public static void main(String[] args) {
System.out.println(Utensil.NAME + Dessert.NAME);
}

    private static class Utensil {
        static final String NAME = "pan";
    }

    private static class Dessert {
        static final String NAME = "cake";
    }
}

```