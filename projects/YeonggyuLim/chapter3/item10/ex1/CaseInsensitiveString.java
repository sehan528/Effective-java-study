package chapter3.item10.ex1;

import java.util.Objects;

//대칭성을 위반한 코드
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    //CaseInsensitiveString 타입은 대소문자 구별 x, String 타입은 구별
    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString)
            return s.equalsIgnoreCase(
                    ((CaseInsensitiveString) o)
                            .s);
        if (o instanceof String)
            return s.equalsIgnoreCase((String) o);
        return false;
    }


}
