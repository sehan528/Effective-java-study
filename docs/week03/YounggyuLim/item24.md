# 멤버 클래스는 되도록 `static`으로 만들어라

- 바깥 인스턴스에서 접근할 일 없으면 무조건 정적 멤버 클래스로 생성 
- 비정적 멤버 클래스일시 암묵적으로 바깥 클래스의 인스턴스를 참조 이런 참조로 인해 바깥 클래스의 인스턴스가 GC에서 수거되지 못함 즉 메모리 누수가 일어남
- 아래 예시는 비정적 멤버 클래스 --> 주로 자신의 반복자를 구현할때 사용 (대부분의 반복자는 외부 클래스의 데이터를 받아 들여야 하니까)
```java
public class MySet <E> extends AbstractSet<E> {
    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<E> {
    }
}
```
## 내부 클래스
- 정적 멤버클래스 : 외부 클래스의 인스턴스와 완전히 독립적으로 동작할 경우
- 비정적 멤버 클래스 : 외부 클래스 인스턴스의 연관되어 동작
- 익명 클래스 : 특정 클래스나 인터페이스를 즉석에서 구현하기 위해 사용
- 지역 클래스 : 정의된 블록 내부에서만 사용 가능 인스턴스 생성하는 지점이 단 한곳이고 해당 타입으로 쓰기에 적합한 클래스, 인터페이스가 이미 있다면 익명 클래스 그렇지 않으면 지역 클래스로 사용 쉽게 말해 코드가 간단하면 익명 아니면 지역