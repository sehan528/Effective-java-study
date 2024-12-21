package chapter4.item18.ex2;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//ForwardingSet 에 상속이 아닌 위임 즉 HashSet의 메서드만 호출해서 중복호출 할 일 없음
public class InstrumentedSet<E> extends ForwardingSet<E>{
    private int addCount = 0;
    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

    public static void main(String[] args) {
        Set<String> hashSet = new HashSet<>();
        InstrumentedSet<String> instrumentedSet = new InstrumentedSet<>(hashSet);

        //위임 받았기 떄문에 add, addAll 이 독립적으로 작동
        instrumentedSet.addAll(List.of("1", "2", "3"));
        System.out.println(instrumentedSet.getAddCount());
        for (String s : instrumentedSet) {
            System.out.println(s);
        }
    }
}
