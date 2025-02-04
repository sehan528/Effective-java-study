package chapter5.item31.ex2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChooserV3<T> {
    private final List<T> choiceList;


    //제네릭 리스트는 애초에 컴파일전에 타입이 다를경우 컴파일 에러가 나서
    //제네릭 타입이 소거된 이후에도 내부 타입으로 문제가 생길 일이 없음
//    public ChooserV3(Collection<T> choices) {
//        choiceList = new ArrayList<>(choices);
//    }

    //이와 같이 수정하면서 Collection<Number> 타입에 Integer 등 타입을 전달 가능
    //즉 하위타입이 전달 가능해짐
    public ChooserV3(Collection<? extends T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
