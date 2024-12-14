package chapter3.item13.ex6;

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

        Entry deepCopy() {
            Entry result = new Entry(key, value, next);
            for (Entry p = result; p.next != null; p = p.next) {
                p.next = new Entry(p.next.key, p.next.value, p.next.next);
            }
            return result;
        }


    }

    //상속용 클래스에는 Cloneable 구현하면 안됨
    //상속 클래스에서 활용시 하위 클래스의 필드가 제대로 복사 되지 않을 수 있고 불변성이 깨질 수 있음
    //protected 로 선언후 CloneNotSupportedException 으로 던지거나
    //아래 처럼 clone 메서드를 하위 클래스에서 사용하지 못하게 퇴화 시키면 됨

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
