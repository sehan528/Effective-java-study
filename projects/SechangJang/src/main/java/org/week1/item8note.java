java는 객체에 대한 소멸자를 제공한다.

Finalizer 언제 써야할까?

안전장치로 이중 체크

public class Item8 {
    private boolean closed;
    public void close() throws Exception {
        // 객체를 닫는다.
    }
    @Override
    public void finalize() throws Throwable {
        if(!this.closed) {
            close();
        }
    }
}

close()로 제 역할은 했더라도 finalize() 를 통해 한번 더 역할을 수행한다.