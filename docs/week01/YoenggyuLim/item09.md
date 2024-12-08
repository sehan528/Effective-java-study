# **try-finally 보다는 try-with-resources를 이용하자**

### **단점: try-finally**
- 코드의 가독성이 떨어짐.
- 자원을 깜빡하고 해제하지 않을 가능성 존재.
- 예외 발생 시 첫 번째 예외를 잃어버릴 수 있음.
- try 블록에서 예외가 발생한 상태에서 finally 블록에서 또 다른 예외 발생시 첫번째 예외가 사라지고 finally 블록에서 발생한 예외만 남음.

---

### **장점: try-with-resources**
1. **가독성 향상**: 코드가 간결하고 명확.
2. **예외 처리 안정성**:
    - 첫 번째 예외를 유지하고, 자원 해제 중 발생한 예외는 Suppressed로 처리.
    - suppressed Exception (첫번째 예외를 기본 예외로 유지하고 자원 해재 중 발생한 예외를 Suppressed Exception 처리로 예외를 잃지 않음)
3. **AutoCloseable과 호환성**:
    - 표준 라이브러리(네이티브 자원)뿐만 아니라 사용자 정의 자원에서도 사용 가능.
    - 네이티브 자원(파일 핸들, 네트워크 소켓 등)은 대부분 `AutoCloseable`을 구현 중.
    - 사용자 정의 클래스에서 `AutoCloseable`을 구현하면 `try-with-resources`로 안전하게 자원 해제 가능.

---

### **결론**
- 예외 없이 **try-with-resources**를 사용하는 것이 훨씬 유리.
- 가독성과 안정성 측면에서 `try-finally`를 완전히 대체 가능.
