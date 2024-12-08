package chapter2.item5.ex2;

public class Service {
    //의존성 주입을 받는 방법
    private final Repository repository;
    public Service(Repository repository) {
        this.repository = repository;
    }

    public void execute() {
        repository.save();
    }
}
