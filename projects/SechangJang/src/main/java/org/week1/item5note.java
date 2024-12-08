의존 객체 주입 예시 1. @Configuration


상식 쌓기 차원에서 @Configuration의 명세를 가져오다. @Configuration은 @Component를 포함하고 있고,
@Component가 Singleton이기에 자연스럽게 @Configuration 또한 Singleton 이다.

Spring의 Configuration 실제 명세.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
    //    ...
}


Config class 내부에 직접 정의하여 사용하지 말라!
-> 여러 자원을 활용해야 할 경우 적합하지 않다. 이 상황에선 환경 (live, dev, local) 별로 다른 값들이 들어가야 할 때 적절하지 않다.

@Configuration
public  class ZeroBaseConfig {
    private static final String address = "서울시 강남구"; // address는 대문자로 표시 되어야할 것 이다. (INSTANCE)
    private static final zipcode;
    public ZeroBaseConfig() {
        this.address = "서울시 강남구";
    }
}

1. 다른 서버의 URL이라던가 서버마다 다른 key 값을 가지고 있어야 경우에 있어 Config class에 고정적인 값을 넣을려면 케이스마다 별도의 필드를 만들어 사용해야한다.
ex) devaddress , liveaddres 등 ... 서버 기준 (환경 변수)으로 분기처리를 하여 나눴어야 합니다.

Solution?
Property Injection 을 사용하라.

application.yml에 등록된 정보.

zerobase:
    address: '서울시 강남구'

@Configuration
public  class ZeroBaseConfig {
    @Value("${zerobase.address}")
    private String address;
}

dependcy가 property에 걸려있고 property의 값을 주입하는 경우.
application.yml에 다음과 같은 값이 실행할 때 읽어와서 Configuration에 싱글톤으로 만들어 해당 값을 @Value 을 통해 주입하게 된다.
application.yml 은 보통 dev, local, live로 파일을 나눠 사용합니다. 즉 클래스 외부 설정에 의해 주입을 받는 형태.


의존 객체 주입 예시 2. Constructor Injection의 경우 Test, flexibility를 높일 수 있다.

고정해 놓은 값에 비해 훨씬 유연해진 class (pattern Injection 가능) Test code 작성 시 injection 하기도 편리하다.
public class PhonePatternChecker {
    private final String pattern;
    public PhonePatternChecker(String pattern) {
        this.pattern = pattern; // 각 나라마다 전화번호 패턴,자릿수가 다르다.
    }
    public boolean isValid(String phone) { }
}



핵심 : Resource를 직접 명시하지 말고, Dependency Injection을 사용하자.
Config class를 생각없이 사용하고 있는지 , Singleton을 이해하고 있는지 , Dependency Injection (DI) 를 잘 이해하고 있는지