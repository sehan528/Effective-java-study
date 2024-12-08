package chapter2.item8.ex1;

public class Adult {
    //명시적으로 자원 해제
    public static void main(String[] args) throws Exception {
        Room myRoom = new Room(7); // 방 생성
        try {
            System.out.println("안녕");
            // 방 사용
        } finally {
            myRoom.close();
        }
    }
}
