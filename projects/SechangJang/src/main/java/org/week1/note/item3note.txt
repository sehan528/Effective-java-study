대표적인 Singletion Pattern

public class Speaker {
    public static final Speaker INSTANCE = new Speaker();
    private Speaker() {
    }
}
INSTANCE가 초기화 되고 나면 고정이 된다. // 명시적인 표현.

1. 생성자를 private으로 호출하여 의도하지 않은 객체 생성을 막는다.
public class Speaker {

    private final Speaker INSTANCE = new Speaker();
    private Speaker() {}

    public static Speaker getInstance() {
        return INSTANCE;
    }
}
static 멤버를 private으로 막고 하단에 생성된 public static method 를 통해 인스턴스를 가져온다는 차이점 존재.
-> 불필요한 메서드 추가 되었다 생각될 수 있지만 API 바꾸지 않고도 싱글턴 아니게 변경 가능 , 정적 팩터리 제네릭 싱글턴 팩터리 변환 가능 , 정적 팩터리 메서드 참조를 ㄱ오급자(supplier)로 사용할 수 있음.
같이 변형을 가할 수 있다는 장점이 존재함.

2. 상황에 따라 synchronized 나, lazy하게 instance를 생성하는 방법도 있다.

public class Speaker {
    private static Speaker instance;
    private Speaker() {}
    public static Speaker synchronized Speaker getInstance() {
        if (instance == null) {
            instance = new Speaker();
        }
        return instance;
    }
}
instance가 필요할지에 대해 장담할 수 없는 경우 lazy하게 instance를 생성하는 방법
시나리오 : 하나 뿐인 커넥션을 같이 공유하는데 일정 시간 사용하지 않는 경우 해당 커넥션을 끊거나 끊기는 경우.
코드를 보면 인스턴스가 null일떄 다시 새로운 인스턴스를 생성 후 주입하도록 한다. ' private static Speaker instance;' 도 final이 아니다 = 변동될 수 있음을 여지 둠 의미
그렇기에 최초 실행 시엔 null이 들어가 있을 것. DB 연결 시도한다거나 그러면 getConnect (Instance) 등 시도하면서 값이 update 될 것. / DB auto disconnect 또한.

3. [책에서 권장하는] Enum type 으로 Singleton pattern을 사용할 수 있다.Speaker

public enum Speaker {
    INSTANCE;
    private String message;
    public Speaker getInstance() {
        return INSTANCE;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}

실행 코드
Speaker speaker1 = Speaker.INSTANCE.getInstance();
speaker1.setMessage("안내 방송 중입니다.");
Speaker speaker2 = Speaker.INSTANCE;
System.out.println(speaker1.getMessage()); // 안내 방송 중입니다.
System.out.println(speaker2.getMessage()); // 안내 방송 중입니다.

장점 :
직렬화, 리플렉션 공격 방어 가능
단점 :
보기 직관적이지 않음. enum 외 class 상속 시 사용이 불가할 수 있음. (실무에서 잘 보이진 않음. 대부분 id를 사용해서 작업하기 때문에 혼용되면 혼란스러울 것.)

