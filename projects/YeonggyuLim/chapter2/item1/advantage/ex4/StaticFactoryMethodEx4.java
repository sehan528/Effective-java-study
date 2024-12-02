package chapter2.item1.advantage.ex4;

public class StaticFactoryMethodEx4 {
    /* 정적 팩토리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 됨
    이 처럼 DB 인터페이스를 구현 안해도 일단은 작성 가능
    대표적인 예시로 JDBC가 있음
    일단은 프레임워크가 구현체(드라이버)와 분리 시켜놓고 작성은 할 수 있게 함 추후에 드라이버가 동적 로드 돼서 연결
     */
    public static Database getDatabase(String type) {
        if (type.equals("mysql")) {
            throw new UnsupportedOperationException("아직 지원하지 않는 DB");
        } else if (type.equals("postgresql")) {
            throw new UnsupportedOperationException("아직 지원하지 않는 DB");
        }
        throw new IllegalArgumentException("알수 없는 DB : " + type);
    }
}
