# **Finalizer와 Cleaner를 피해야 하는 이유**

- **`Finalizer`와 `Cleaner`는 피하는 것이 권장.**
    - **`try-with-resources`**를 사용하는 것이 더 안전하고 명확.
    - Finalizer와 Cleaner는 자원의 해제를 **비동기적으로 처리**하므로, 언제 실행될지 보장할 수 없음.
    - 성능 저하와 예기치 못한 동작의 원인이 될 수 있음.

---

## **Finalizer와 Cleaner의 한계**
1. **성능 문제**
    - Finalizer와 Cleaner는 GC(가비지 컬렉션)와 연동되어 동작하므로 **리소스 해제 시점이 불확실**.
    - 불필요한 GC 작업을 유발해 프로그램 성능에 악영향을 줄 수 있음.

2. **리소스 누수**
    - GC가 실행되지 않으면 Finalizer와 Cleaner도 실행되지 않아 자원이 해제되지 않을 수 있음.

3. **예외 처리 부족**
    - Finalizer와 Cleaner 내에서 예외가 발생하면 무시되며, 프로그램에 심각한 문제를 초래할 수 있음.

---

## **`try-with-resources` 권장 이유**
- **명시적이고 즉시적인 자원 해제**가 가능.
- **명확한 코드 구조**를 제공하며, 자원 누수를 방지할 수 있음.

---

# **Finalizer와 Cleaner가 필요한 경우**
- **네이티브 자원 회수 시**에만 제한적으로 권장.
- 네이티브 자원은 JVM 외부에서 관리되는 자원으로, GC에 의해 관리 X.

---

## **네이티브 자원의 예**
1. **파일 핸들**
    - 파일을 읽고 쓰기 위한 핸들.
    - 예: `FileInputStream`, `RandomAccessFile`.

2. **네트워크 소켓**
    - 네트워크 연결을 생성하는 시스템 리소스.
    - 예: `Socket`, `ServerSocket`.

3. **데이터베이스 연결**
    - JDBC를 통해 생성된 데이터베이스 커넥션.
    - 예: `Connection`, `PreparedStatement`.

4. **쓰레드**
    - 운영체제에서 관리하는 실제 쓰레드.
    - JVM 쓰레드와는 별개로 관리되는 네이티브 쓰레드.

5. **메모리 매핑**
    - 파일을 메모리에 직접 매핑하여 데이터를 읽고 쓰는 경우.
    - 예: `MappedByteBuffer`.

6. **기타 시스템 리소스**
    - 하드웨어 핸들(예: GPU 리소스).
    - 네이티브 라이브러리(예: OpenGL, 네이티브 드라이버).