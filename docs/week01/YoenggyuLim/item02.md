# **객체 생성 패턴 비교: 점층적 생성자 패턴, 자바 빈즈 패턴, 빌더 패턴**

## **1. 점층적 생성자 패턴**
- 필요한 필드를 생성자에 **순차적으로 추가**하는 방식 (여러 생성자를 사용함).

### **단점**
1. **매개변수가 많으면 가독성이 저하됨**
    - 매개변수의 순서와 의미를 혼동하기 쉬움.
2. **매개변수가 많아질수록 유지보수가 어려움**
    - 새로운 필드가 추가될수록 생성자가 늘어나 코드가 비대해짐.

---ㅋ

## **2. 자바 빈즈 패턴**
- 기본 생성자로 객체를 생성한 후, **세터 메서드**를 통해 필요한 값을 설정하는 방식.

### **단점**
1. **객체가 완전한 상태이기 이전에 접근 가능**
    - 객체가 생성된 후 필드가 설정되지 않으면 불완전한 상태로 사용될 위험이 있음.
2. **스레드 안전성 부족**
    - 멀티스레드 환경에서 세터 사용 시 데이터의 일관성이 깨질 수 있음.

---

## **3. 빌더 패턴**
- 복잡한 객체를 유연하게 생성하기 위한 패턴.
- **점층적 생성자 패턴의 안정성**과 **자바 빈즈 패턴의 가독성**을 결합한 방식.

### **단점**
1. **구현의 러닝 커브 존재**
    - 빌더 클래스를 작성해야 하며, 구현이 상대적으로 복잡함.
2. **규모가 작으면 오히려 불편할 수 있음**
    - 필드가 적고 간단한 객체를 생성할 경우 오히려 비효율적일 수 있음.

---

# **객체 생성 패턴 비교 표**

| **특징**       | **점층적 생성자 패턴**                          | **자바 빈즈 패턴**                       | **빌더 패턴**                      |
|----------------|-----------------------------------------------|-----------------------------------------|------------------------------------|
| **가독성**     | 낮음 (매개변수 순서 헷갈림)                   | 높음 (세터로 각 필드 명확히 설정)        | 매우 높음 (메서드 이름으로 명확)    |
| **유연성**     | 낮음 (모든 필드 포함 생성자 필요)             | 높음 (필드 선택 가능)                   | 매우 높음 (필수/선택 구분 가능)     |
| **불변성 보장** | 보장                                         | 보장되지 않음 (세터 사용)               | 보장                               |
| **구현 복잡성**| 간단                                         | 간단                                   | 복잡 (빌더 클래스 필요)            |
| **사용 사례**  | 필드가 적고 간단한 객체                      | 필드가 많고, 설정 순서가 중요하지 않은 경우 | 필드가 많고, 설정 순서가 중요한 경우 |


### **결론**
- 필드가 많고 객체 생성 로직이 복잡하다면 **빌더 패턴**이 가장 유연하고 안정적.
- 간단한 객체 생성에는 **점층적 생성자 패턴** 또는 **자바 빈즈 패턴**도 적합.
- 빌더 패턴의 장점을 체득하기 위해 **많이 구현해보는 것**이 중요함.
