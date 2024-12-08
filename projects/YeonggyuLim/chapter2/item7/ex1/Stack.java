package chapter2.item7.ex1;

import java.util.Arrays;

public class Stack {
        private Object[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        public Stack() {
            elements = new Object[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(Object e) {
            ensureCapacity();
            elements[size++] = e;
        }

        public Object pop() {
            if (size == 0)
                throw new IllegalStateException("Stack is empty");

            return elements[--size];
            // 메모리 누수 발생 지점
            // 배열에서 해당 객체의 참조를 제거하지 않음
        }

        /* 객체 참조 해제를 위한 코드
        public Object pop() {
            if (size == 0)
                throw new IllegalStateException("Stack is empty");

            Object result =  elements[--size];
            elements[size] = null;
            return result;

        }
         */

        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }
}
