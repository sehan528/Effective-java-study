package chapter3.item13.ex3;

public class HashTable implements Cloneable{
    
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
        
        
    }
    
    //가변상태를 공유하면 원본과 같은 배열을 공유함 (참조값이 같음) 즉 얕은복사
    @Override
    public Object clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = buckets.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
