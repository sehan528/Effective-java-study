# 추상 클래스보다는 인터페이스를 우선하라
- 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다.
- 다중 구현이 가능함 상속은 단일 상속만 가능

- 골격 구현을 사용한 구체 클래스
- `equals hashcode` 같은 `Object` 메서드는 `디폴트 메서드`로 제공하면 안됨
- 객체의 고유한 동작을 정의해야하기 때문
- `디폴트 메서드`를 재정의 하지않을경우 하위 클래스에서 문제 발생할 수 있으니 차라리 하위클래스에서 재정의하게 `Objects 메서드`들을 `디폴트 메서드`로 제공하지 않는게 좋음
```java
public class ex {
    //골격 구현을 상속받아서 사용하는 구체 클래스 골격 구현 클래스에서 추상메서드로 선언된 메서드들을 구현
    //필요한 메서드만 구현하기 위해 사용
    static List<Integer> intArrayAsList(int[] a) {
        Objects.requireNonNull(a);

        return new AbstractList<Integer>() {
            @Override
            public Integer get(int i) {
                return a[i];
            }

            @Override
            public Integer set(int i, Integer val) {
                int oldVal = a[i];
                a[i] = val;
                return oldVal;
            }

            @Override
            public int size() {
                return a.length;
            }
        };
    }
}
```

- 골격 구현 클래스
```java
//골격 구현 클래스는 인터페이스를 구현하는 추상클래스
//인터페이스를 구현하면 보통 모든 메서드를 구현해야 하지만 추상 클래스는 아님
//구현하지 않으면 추상 메서드로 남겨서 상속받은 클래스에서 반드시 구현해야함
//물론 추상 메서드가 아니어도 구체 클래스에서 재정의 할 수 있음

public abstract class AbstractMapEntry<K, V> implements Map.Entry<K, V> {
    //추상 클래스라 Objects 메서드 재정의 가능
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        Map.Entry<?,?> e = (Map.Entry) o;
        return Objects.equals(e.getKey(), getKey())
                && Objects.equals(e.getValue(), getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey())
                ^Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}
```

- 단순 구현
- 골격 구현의 작은 변종
- 골격 구현과 같이 상속을 위해 인터페이스를 구현한 것 하지만 추상클래스가 아님

