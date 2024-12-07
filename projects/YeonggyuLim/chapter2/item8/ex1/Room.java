package chapter2.item8.ex1;

import java.lang.ref.Cleaner;

public class Room implements AutoCloseable{
    //Room 객체가 더 이상 사용 되지 않을때 리소스 정리를 돕는 Cleaner 클래스
    private static final Cleaner cleaner = Cleaner.create();

    //Runnable 구현으로 Cleaner 가 다른 스레드에서 수행
    private static class State implements Runnable {
        int numJunkPiles;

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }
        @Override
        public void run() {
            System.out.println("방 청소");
            numJunkPiles = 0;
        }
    }
    private final State state;

    private final Cleaner.Cleanable cleanable;

    //룸 객체 생성시 내부 클래스 state 에 정리 해야 할 쓰레기 더미 수 등록
    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    //룸 객체를 명시적으로 정리 하는것
    @Override
    public void close() throws Exception {
        cleanable.clean();
    }
}
