package chapter5.item29.ex1;

import java.util.Arrays;
import java.util.EmptyStackException;

public class StackV2<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public StackV2() {
        //직접 제네릭 타입 배열을 생성할 수 없으므로 Object 타입으로 만들고 캐스팅해서 우회
        //또한 push 가 E 타입만 받으므로 내부 타입이 안전
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    //push 시 E 타입만 받으니까 타입 안정성이 있음
    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        @SuppressWarnings("unchecked")
        E result = (E) elements[--size];
        elements[size] = null;
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
