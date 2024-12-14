package org.week2.item12;

/**
 * toString 메서드의 format을 명시하지 않은 예제
 * Learning Point: 포맷을 명시하지 않은 toString 구현
 */
public class Medicine {
    private final String name;
    private final String type;
    private final String smell;

    public Medicine(String name, String type, String smell) {
        this.name = name;
        this.type = type;
        this.smell = smell;
    }

    /**
     * 이 약물에 관한 대략적인 설명을 반환한다.
     * 다음은 이 설명의 일반적인 형태이나,
     * 상세 형식은 정해지지 않았으며 향후 변경될 수 있다.
     * 
     * "[약물 #9: 유형-사랑, 냄새=테러빈유, 겉모습=먹물]"
     */
    @Override
    public String toString() {
        return String.format("[약물 %s: 유형-%s, 냄새=%s]",
            name, type, smell);
    }

    // 접근자 메서드 제공
    public String getName() { return name; }
    public String getType() { return type; }
    public String getSmell() { return smell; }
}