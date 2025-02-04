package chapter5.item28.ex3;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChooserV2<T> {
    private final T[] choiceArray;

    //컴파일 후 제네릭 타입이 소거됨 따라서 런타임 시점에서는 어떤 타입이 들어온지 모름
    //타입 안정성을 보장 할 수 없음
    public ChooserV2(Collection<T> choices) {
        choiceArray = (T[]) choices.toArray();
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
