Instance 를 막는 Util Class

잘못된 예제
public class PatternUtil {
    private static final String PATTERN = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\";
    private PatternUtil() {}
    public static boolean isEmailValid(String email) {
        return email.matches(PATTERN);
    }
    public String getPattern() {
        return PATTERN;
    }
}

Q. 어디가 잘못된걸까?
이 코드를 만든 사람은 여러 패턴을 체크하여 "이메일, 전화번호" 등 유효성 파악이 목적이였을 것 이다.
코드를 만든 사람A는 퇴사하고 새로운 개발자 B가 해당 코드의 String PATTERN 자체가 필요한 경우가 생겼다.
String PATTERN 을 다시 public 으로 열지는 못해 별도로 'public String getPattern()' 라는 별도의 메서드를 생성하게 되버린다.
-> Human error의 시작점이 될 수 있다. 만약 생성자를 private 으로 하나 만들어뒀다면 인스턴스화 불가했다면 사전 차단이 가능했을 것.

