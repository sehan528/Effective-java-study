package org.week2.item13;

/**
 * 복잡한 자료구조(연결 리스트)를 포함하는 클래스의 clone 구현 예제
 * 깊은 복사의 중요성과 구현 방법을 보여줍니다.
 */
public class HashTable implements Cloneable {
    private Entry[] buckets = new Entry[10];

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        // 엔트리의 깊은 복사를 반복적으로 구현
        Entry deepCopy() {
            // 첫 번째 엔트리 생성
            Entry result = new Entry(key, value, next);
            // 다음 엔트리들을 순회하며 복사
            for (Entry p = result; p.next != null; p = p.next) {
                p.next = new Entry(p.next.key, p.next.value, p.next.next);
            }
            return result;
        }
    }

    public void put(Object key, Object value) {
        int hash = Math.abs(key.hashCode() % buckets.length);
        buckets[hash] = new Entry(key, value, buckets[hash]);
    }

    public Object get(Object key) {
        int hash = Math.abs(key.hashCode() % buckets.length);
        for (Entry e = buckets[hash]; e != null; e = e.next) {
            if (e.key.equals(key)) return e.value;
        }
        return null;
    }

    @Override
    public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            // 버킷 배열의 깊은 복사 수행
            result.buckets = new Entry[buckets.length];
            
            // 각 버킷의 연결 리스트를 깊은 복사
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null)
                    result.buckets[i] = buckets[i].deepCopy();
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}