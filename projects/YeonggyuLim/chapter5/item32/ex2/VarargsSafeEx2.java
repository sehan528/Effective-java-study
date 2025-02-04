package chapter5.item32.ex2;

import java.util.ArrayList;
import java.util.List;

public class VarargsSafeEx2 {

    //리스트로 바꿔 쓰는게 안전한 이유
    //런타임시 제네릭 타입이 소거 돼도 이미 내부 타입이 고정 되어 있어 타입 안정성 보장 가능
    static <T> List<T> flatten(List<List<? extends T>> lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }


}
