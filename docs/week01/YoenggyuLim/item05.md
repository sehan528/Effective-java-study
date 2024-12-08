# **의존성 주입 (Dependency Injection, DI)**

- 많은 클래스가 하나 이상의 **자원(Dependency)**에 의존할 경우, 해당 자원을 직접 생성하는 대신 **외부에서 주입**받는 방식인 의존성 주입(DI)을 고려해야 함.
- 이 방식은 **객체 간의 결합도를 낮추고, 유연성과 테스트 용이성**을 높이는 데 기여.

---

## **의존성 주입의 핵심 개념**

- **의존성(Dependency)**: 클래스가 작동하기 위해 필요한 외부 자원(예: 데이터베이스, API 클라이언트 등).
- **의존성 주입**: 클래스 내부에서 자원을 직접 생성하지 않고, 생성자, 세터, 또는 필드를 통해 외부에서 주입받는 방식.

---

## **의존성 주입의 장점**

1. **종속성 감소 (Decoupling)**:
    - 클래스가 특정 구현체에 의존하지 않고, **인터페이스 또는 추상 클래스**에 의존하도록 설계할 수 있음.
    - 이를 통해 코드의 재사용성과 확장성이 높아짐.

2. **유연성 증가**:
    - 주입받는 자원을 쉽게 변경할 수 있어, 환경(예: 개발/운영)이나 요구사항에 따라 다른 구현체를 사용할 수 있음.

3. **테스트 용이성**:
    - 실제 구현 대신 **Mock 객체**를 주입하여 테스트를 실행할 수 있음.
    - 외부 리소스(예: 데이터베이스, 네트워크 등)에 의존하지 않고도 단위 테스트를 작성할 수 있음.

4. **프레임워크와의 통합**:
    - 스프링(Spring) 같은 프레임워크는 의존성 주입을 기본적으로 지원하여, 복잡한 의존성 관리 작업을 자동화.
