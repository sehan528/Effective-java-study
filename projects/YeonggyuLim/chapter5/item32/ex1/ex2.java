package chapter5.item32.ex1;

public class ex2 {
    static <T> T[]toArray(T... args) {
        return args;
        //제네릭 매개변수 배열의 참조를 노출

    }

    public static void main(String[] args) {
        String[] strings = toArray("A", "B", "C");

        //참조값이 같은 string 을 Object 에 넣어서 타입이 변환됨 즉 공변적
        //ArrayStoreException 발생
        Object[] objects = strings;
        objects[0] = 42;
        String s = strings[0];
    }
}
