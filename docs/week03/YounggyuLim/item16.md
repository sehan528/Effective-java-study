# `public` 클래스에서는 `public` 필드가 아닌 접근자 메서드를 사용하라

- 퇴보한 클래스는 `public`이어서는 안됨
- 내부 표현을 바꿀 수 도 없고 불변식을 보장 할 수 업기 때문
```java
//퇴보한 클래스 예시
class Point {
    public double x;
    public double y;
}
```

- `getter`, `setter` 를 이용해 데이터를 캡슐화
- 캡슐화가 지켜짐 --> 객체 내부 구현을 숨기고 외부와의 인터페이스를 통해서만 접근 하는것
```java
class PointEx {
    private double x;
    private double y;

    public PointEx(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
```

## `public` 클래스 에서는 가변 필드를 노출하지 말자
- 불변 클래스도 상대적으로 덜 위험할 뿐 위험할 수 는 있음
- package-private 클래스나 private 중첩 클래스에서는 종종 (불변 or 가변)필드를 노출 하는 편이 나을 수도 있음
