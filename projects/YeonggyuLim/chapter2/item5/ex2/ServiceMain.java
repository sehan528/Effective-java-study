package chapter2.item5.ex2;

public class ServiceMain {
    public static void main(String[] args) {
        //service 가 특정 Repository 에 종속되지 않음 좀더 유연하게 사용가능
        Repository mySql = new MySqlRepository();
        Service service = new Service(mySql);
        service.execute();
    }
}
