# 비검사 경고를 제거하라

- 경고를 제거할 수는 없지만 타입 안전한다고 확신할 수 있다면 @SuppressWarnings("unchecked") 어노테이션을 달아 경고 숨기기
- @SuppressWarning 어노테이션은 클래스 전체부터 지역변수 선언까지 범위를 자유롭게 지정가능하지만 최대한 좁은 범위에 지정할것

- 아래 예시에서는 a.getClass()로 제네릭타입의 배열이 어떤 타입일지 유추가 가능함 따라서 타입 안전성을 보장할 수 있음 (올바른 타입을 전달한다고 가정할시)
```java
public class SuppressWarningsEx {
   private Object[] elements;
   private int size;
   
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked") T[] result =
                    (T[]) Arrays.copyOf(elements, size, a.getClass());
            return result;
        }

        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
}
```