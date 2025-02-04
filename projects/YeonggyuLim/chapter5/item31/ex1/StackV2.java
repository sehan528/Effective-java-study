package chapter5.item31.ex1;

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

    //Stack<Number> 로 선언후 pushAll(intVal) 호출시 intVal 은 Integer 타입
    //제네릭은 불공변이기 때문에 Number 의 하위타입인 Integer 이지만 다른 취급 따라서 들어갈 수 없음
    public void pushAll(Iterable<E> src) {
        for (E e : src) {
            push(e);
        }
    }

    public static void main(String[] args) {
        StackV2<Number> stackV2 = new StackV2<>();

        Iterable<Integer> intVal = Arrays.asList(1, 2, 3);
        Iterable<Double> doubleVal = Arrays.asList(1.1, 2.2, 3.3);

        //하위 타입이 못들어감
//        stackV2.pushAll(intVal);
//        stackV2.pushAll(doubleVal);

        while (!stackV2.isEmpty()) {
            System.out.println(stackV2.pop());
        }
    }
}
