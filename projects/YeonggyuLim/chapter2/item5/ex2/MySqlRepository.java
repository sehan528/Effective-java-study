package chapter2.item5.ex2;

public class MySqlRepository implements Repository{
    @Override
    public void save() {
        System.out.println("MySQL 저장");
    }
}
