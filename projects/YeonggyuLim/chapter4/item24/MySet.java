package chapter4.item24;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

//바깥 인스턴스에서 접근할 일 없으면 무조건 정적 멤버 클래스로 생성
//그렇지 않으면 바깥 클래스의 인스턴스를 수거하지 못해서 메모리 누수가 발생할 수 있음
//이 경우는 비정적 멤버 클래스 --> 주로 자신의 반복자를 구현할떄 사용

//정적 멤버클래스 --> 외부 클래스의 인스턴스와 완전히 독립적으로 동작할 경우
//비정적 멤버 클래스 --> 외부 클래스 인스턴스의 연관되어 동작
//익명 클래스 --> 특정 클래스나 인터페이스를 즉석에서 구현하기 위해 사용
//지역 클래스 --> 정의된 블록 내부에서만 사용 가능
// 인스턴스 생성하는 지점이 단 한곳이고 해당 타입으로 쓰기에 적합한 클래스, 인터페이스가 이미 있다면 익명 클래스 그렇지 않으면 지역 클래스로 사용
//쉽게 말해 코드가 간단하면 익명 아니면 지역
public class MySet <E> extends AbstractSet<E> {
    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    @Override
    public int size() {
        return 0;
    }

    protected MySet() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    private class MyIterator implements Iterator<E> {
        @Override
        public void remove() {
            Iterator.super.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Iterator.super.forEachRemaining(action);
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            return null;
        }
    }
}
