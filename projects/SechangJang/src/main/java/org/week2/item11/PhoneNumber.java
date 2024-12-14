package org.week2.item11;

import java.util.Objects;

/**
 * hashCode 메서드의 다양한 구현 예시를 보여주는 클래스
 * Learning Point: hashCode 구현의 여러 방식과 장단점
 */
public final class PhoneNumber {
    private final int areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "area code");
        this.prefix = rangeCheck(prefix, 999, "prefix");
        this.lineNum = rangeCheck(lineNum, 9999, "line num");
    }

    private static int rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber) o;
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }

    // 1. 전형적인 hashCode 메서드
    @Override
    public int hashCode() {
        int result = Integer.hashCode(areaCode);
        result = 31 * result + Integer.hashCode(prefix);
        result = 31 * result + Integer.hashCode(lineNum);
        return result;
    }

    // 2. 한 줄짜리 hashCode 메서드 - 성능이 살짝 아쉽다
    public int hashCodeAlternative() {
        return Objects.hash(areaCode, prefix, lineNum);
    }
}