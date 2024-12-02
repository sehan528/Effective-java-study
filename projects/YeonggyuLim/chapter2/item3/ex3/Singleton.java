package chapter2.item3.ex3;

public enum Singleton {
    //가장 바람직한 방법 직렬화시 추가적인 작업 필요 x
    //리플렉션에 취약하지 않음
    INSTANCE;
    public void doSomething() {
        System.out.println("아무거나");
    }
}
