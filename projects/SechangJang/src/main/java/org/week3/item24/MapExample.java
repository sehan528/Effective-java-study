package org.week3.item24;

/**
 * Map.Entry 스타일의 정적 멤버 클래스 예제
 */
public class MapExample<K, V> {
    // 정적 멤버 클래스로 Entry 구현
    public static class Entry<K, V> {
        private final K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
        public void setValue(V value) { this.value = value; }

        @Override
        public String toString() {
            return "키: " + key + ", 값: " + value;
        }
    }

    // Entry 사용 예시 메서드
    public Entry<K, V> createEntry(K key, V value) {
        return new Entry<>(key, value);
    }
}