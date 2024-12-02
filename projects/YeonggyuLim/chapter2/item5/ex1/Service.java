package chapter2.item5.ex1;

public class Service {
    //직접 자원을 명시하는 경우
    //Service 가 Repository 에 종속돼있음 유연하지 못함
    private final Repository repository = new Repository();

    public void execute() {
        repository.save();
    }
}
