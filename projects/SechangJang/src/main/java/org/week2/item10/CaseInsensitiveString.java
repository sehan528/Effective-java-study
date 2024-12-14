package org.week2.item10;
import java.util.Objects;

/**
 * 대칭성을 위배하는 equals 예시
 * Learning Point: equals의 대칭성 위배 케이스
 */
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 잘못된 equals 구현 - 대칭성 위배!
    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString)
            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
        if (o instanceof String)  // String과 비교할 때 한쪽으로만 작동!
            return s.equalsIgnoreCase((String) o);
        return false;
    }
}