package org.week2.item12;

/**
 * toString 메서드의 format을 명시한 예제
 * Learning Point: 포맷을 명시한 toString 구현
 */
public class PhoneNumber {
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

    /**
     * 이 전화번호의 문자열 표현을 반환한다.
     * 이 문자열은 "XXX-YYY-ZZZZ" 형태의 12글자로 구성된다.
     * XXX는 지역 코드, YYY는 프리픽스, ZZZZ는 가입자 번호다.
     * 각각의 대문자는 10진수 숫자 하나를 나타낸다.
     */
    @Override 
    public String toString() {
        return String.format("%03d-%03d-%04d", 
            areaCode, prefix, lineNum);
    }

    // toString이 반환한 값에 포함된 정보를 얻을 수 있는 API 제공
    public int getAreaCode() { return areaCode; }
    public int getPrefix() { return prefix; }
    public int getLineNum() { return lineNum; }
}