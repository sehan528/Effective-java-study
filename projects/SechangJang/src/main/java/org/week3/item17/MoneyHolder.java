package org.week3.item17;
import java.util.*;
/**
 * 컬렉션을 포함한 불변 클래스 예제
 * - 내부 컬렉션에 대한 방어적 복사
 * - 불변성 보장을 위한 다양한 방법 제시
 */
public final class MoneyHolder {
    private final List<Integer> amounts;

    public MoneyHolder(List<Integer> amounts) {
        // 방어적 복사를 통한 불변성 보장
        this.amounts = new ArrayList<>(amounts);
    }

    // 잘못된 방법: 내부 리스트 직접 노출
    public List<Integer> getAmountsUnsafe() {
        return amounts;
    }

    // 올바른 방법 1: 불변 리스트 반환
    public List<Integer> getAmountsSafe() {
        return Collections.unmodifiableList(amounts);
    }

    // 올바른 방법 2: 방어적 복사본 반환
    public List<Integer> getAmountsDefensive() {
        return new ArrayList<>(amounts);
    }

    @Override
    public String toString() {
        return amounts.toString();
    }
}