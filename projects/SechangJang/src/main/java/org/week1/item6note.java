Boxing type 대신 Primitive type을 권장한다.

public static long sum() {
    Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++) {
        sum += i; // 값이 더해질 때 마다 새로운 Long의 Boxing type이 생긴다.
    }
    return sum;
}

Util Class 에서 primitive type을 권장한다.
참 거짓을 response 하는데 Boxing type을 쓰는것은 낭비일 수 있다.
public class PhonePatternUtil {
    private final String pattern;

    public boolean isValid(String phone) {
        // 만약 Boolean 등 굳이? 라는 생각이 들 것 이다.
    }
}

그렇다고 항상 primitive 가 옳은 것은 아니다. 대표적인 Null case를 함께 보자.
물견의 가격이라 생각해보자
int price; 와 Integer price; 즉, price가 0인 것과 null인 것의 의미는 다르다.
0의 의미는 증정품, 할인 등 사유로 0원이 될 수 있고 , Null의 경우 가격이 아직 정해지지 않은 경우로 볼 수 있다.
만약 Null을 표시해야된다면 Boxing을 고려해봐야할 수 있을 것 이다.
API의 경우에도 0 혹은 Null로 받아야 되는 경우가 있을 수 있어서 Optional 하게 받아야 될 경우가 있다.

주의해야 할 내장 Method

static boolean isEmailVaild(String s){
    return s.matches("[a-zA-Z0-9.-]\\\\.[a-zA-Z]{2,6}$");
}

2) String.java
public boolean matches (String expr) {
    return Pattern.matches(expr, this);
}

3) Pattern.java
public static boolean matches(String regex, CharSequence input) { Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(input);
    return m.matches();
}

이메일 체크를 위해 Pattern 을 String으로 넣었다. 다만 위 코드는 결함이 있다.
2) Pattern.matches 을 호출하는데 String의 matches는 결국 Pattern.java 을 호출하는 것 인데
Pattern.java 을 보면 regex(정규표현식) 을 받아서 컴파일을 해준다. 만든 패턴을 가지고 비교를 하는데
이 말은 즉, validation 할 때 마다 매번 정규표현식을 만들고 비교하는 낭비를 행한다는 것이다. (재활용하지 않음.)


Pattern instance가 매번 생성
static boolean isEmailVaild(String s) {
    return s.matches("[a-zA-Z0-9.-]\\\\.[a-zA-Z]{2,6}$");
}

Pattern instance가 한 번만 생성
public class EmailUtil {
    private static final Pattern EMAIL =
        Pattern.compile("[a-zA-Z0-9.-]\\\\.[a-zA-Z]{2,6}$"); // key point.
    static boolean isEmailVaild(String s) {
        return EMAIL.matcher(s).matches();
    }
}

매번 생성되는 인스턴스를 한 번 만 생성되도록 캐싱하는 것이 핵심.

