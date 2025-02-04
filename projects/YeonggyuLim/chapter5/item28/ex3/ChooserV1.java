package chapter5.item28.ex3;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChooserV1 {
    private final Object[] choiceArray;

    public ChooserV1(Collection choices) {
        choiceArray = choices.toArray();
    }

    //choose 호출할때 마다 Object 를 원하는 타입으로 형변환 해줘야함
    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
