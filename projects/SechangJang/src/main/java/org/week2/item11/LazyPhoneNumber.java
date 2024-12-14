package org.week2.item11;

/**
 * 지연 초기화를 사용한 hashCode 메서드 예시
 * Learning Point: hashCode의 지연 초기화 패턴
 */
public class LazyPhoneNumber {
    private final int areaCode, prefix, lineNum;
    private int hashCode; // 자동으로 0으로 초기화

    public LazyPhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = Integer.hashCode(areaCode);
            result = 31 * result + Integer.hashCode(prefix);
            result = 31 * result + Integer.hashCode(lineNum);
            hashCode = result;
        }
        return hashCode;
    }
}