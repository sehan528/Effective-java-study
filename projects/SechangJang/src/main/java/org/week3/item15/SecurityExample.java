package org.week3.item15;

import java.util.*;

/**
 * 보안을 고려한 접근 제어 예제
 */
public class SecurityExample {
    // 외부에서 접근할 수 없는 내부 데이터
    private final List<String> secretData;

    public SecurityExample() {
        this.secretData = new ArrayList<>();
        secretData.add("민감한 정보 1");
        secretData.add("민감한 정보 2");
    }

    // 잘못된 방법: 내부 리스트 직접 반환
    public List<String> getSecretDataUnsafe() {
        return secretData;  // 외부에서 수정 가능!
    }

    // 올바른 방법 1: 불변 리스트로 반환
    public List<String> getSecretDataSafe() {
        return Collections.unmodifiableList(secretData);
    }

    // 올바른 방법 2: 방어적 복사
    public List<String> getSecretDataDefensive() {
        return new ArrayList<>(secretData);
    }
}