객체의 인스턴스 생성 상황과 의미 다시생각
        특정 메서드 실행 Method Area를 부르게 됨 특정 타입 변수 선언 시 Method Area의 Stack에 적용되고
        그 객체가 인스턴스를 생성한다면 Stack이아니라 Heap 영역에 할당되게 됩니다 (new, newarray )
    인스턴스들은 서로 호출된 방향 순으로 참조 구조를 가지고 있다. 실제 스택 영역과 연결이 되어 유효성이 있는것을 Reachable Objects 라고 부른다.
        그리고 더 이상 사용되지 않는 것들은 Unreachable Objects라고 부릅니다. = Heap에 있긴하지만 아무도 참조하지 않는
여기서 JAVA는 GC는 바로 Unreachable OBjecct를 제거하진 않습니다. 한 사이클 돌고 지우는데 그것이 고정적이지 않음.

Array 를 잘 쓰고 있는지? , 메모리 구조에 대해 이해 하고 있는지 (Heap, Stack, Method area) , Gabage Collector (GC) 가 동작하는 원리에 대해 잘 이해하고 있는지 , JVM