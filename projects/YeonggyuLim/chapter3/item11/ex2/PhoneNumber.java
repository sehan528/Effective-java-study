package chapter3.item11.ex2;

public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(short areaCode, short prefix, short lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max) {
            throw new IllegalArgumentException(arg + ": " + val);
        }
        return (short) val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof  PhoneNumber)) {
            return false;
        }
        PhoneNumber pn = (PhoneNumber) o;
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }

    @Override
    public int hashCode() {
        //가독성 + 명확하고 일관된 해시 코드 생성을 위해 박싱클래스.hashcode() 사용
        //참조형일 경우 null 이면 0을 사용해서 계산, 계산이 복잡해질거 같으면 필드의 표준형을 만들어 사용
        //표준형은 Hello, hello 대소문자 구별없이 동일하게 처리한다거나 배열이면 [a,c,b] --> [a, b, c] 정렬된 기준
        //필드가 배열일 경우 핵심 원소 각각을 별도 필드처럼 다룸 배열에 핵심원소가 없을경우 상수 0처리 추천
        //배열에 핵심원소가 단 하나도 존재하지 않는다면 Arrays.hashCode 사용
        //Objects 클래스에서 제공하는 hashCode 메서드는 성능이 그렇게 좋지 않아 성능에 민감하지 않을때만 사용하자
        int result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        return result;
    }
}
