package chapter3.item14.ex3;

import java.util.Arrays;
import java.util.Comparator;

import static java.util.Comparator.*;

public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;
    private int hashCode;

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

    //해시코드를 지연 초기화 하는 메서드
    //필요한 시점까지 지연해뒀다 해시코드 계산 해두고 재사용
    //불변객체가 아닐 경우 멀티 스레드 환경에서 위험
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Short.hashCode(areaCode);
            result = 31 * result + Short.hashCode(prefix);
            result = 31 * result + Short.hashCode(lineNum);
            hashCode = result;
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%03d-%03d-%04d", areaCode, prefix, lineNum);
    }


    //비교자 생성 메서드를 활용
    private static final Comparator<PhoneNumber> COMPARATOR =
            //static import 로 코드 간결하게
            comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNum);

    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }

    public static void main(String[] args) {
        PhoneNumber pn1 = new PhoneNumber((short) 415, (short) 555, (short) 1212);
        PhoneNumber pn2 = new PhoneNumber((short) 408, (short) 555, (short) 1234);
        PhoneNumber pn3 = new PhoneNumber((short) 415, (short) 555, (short) 4321);

        PhoneNumber[] numbers = {pn1, pn2, pn3};

        Arrays.sort(numbers, COMPARATOR);

        for (PhoneNumber pn : numbers) {
            System.out.println(pn);
        }
    }
}
