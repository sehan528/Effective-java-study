package chapter3.item13.ex4;

public class HashTable implements Cloneable {

    private Entry[] buckets;

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        public Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        //깊은복사의 재귀 하는 방식 배열의 요소의 참조값이 다름,
        //하지만 이 방식은 체인의 길이가 길지 않을때만 효율적 (스택 오버플로우 위험)
        Entry deepCopy() {
            return new Entry(key, value,
                    next == null ? null : next.deepCopy());
        }


    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
