package org.week2.item13;

/**
 * 기본 타입 필드만을 가진 클래스의 clone 구현 예제
 * clone 메서드의 가장 단순한 구현을 보여줍니다.
 */
public class PhoneNumber implements Cloneable {
    private final int areaCode;
    private final int prefix;
    private final int lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        // 유효성 검증을 통해 불변식을 보장
        rangeCheck(areaCode, 999, "지역코드");
        rangeCheck(prefix, 999, "프리픽스");
        rangeCheck(lineNum, 9999, "가입자 번호");
        
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    private static void rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
    }

    @Override
    public PhoneNumber clone() {
        try {
            // 기본 타입 필드만 있으므로 super.clone()으로 충분
            return (PhoneNumber) super.clone();
        } catch (CloneNotSupportedException e) {
            // 일어날 수 없는 일이지만 CloneNotSupportedException은 검사 예외이므로 처리 필요
            throw new AssertionError();
        }
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

    @Override 
    public String toString() {
        return String.format("%03d-%03d-%04d", areaCode, prefix, lineNum);
    }
}