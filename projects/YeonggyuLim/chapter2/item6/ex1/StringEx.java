package chapter2.item6.ex1;

public class StringEx {
    public static void main(String[] args) {
        //힙 메모리에 새로운 String 객체 생성
        String s = new String("bikini");

        // 상수풀에 bikini 만들고 재활용
        String s2 = "bikini";

        System.out.println(s == s2);
    }
}
