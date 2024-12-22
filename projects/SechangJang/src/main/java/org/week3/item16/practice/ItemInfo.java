package org.week3.item16.practice;

/**
 * 실전 캡슐화 예제
 * - 내부적으로는 cost를 사용하지만 외부에는 price로 노출
 * - 이름은 변경 불가능하도록 설계
 */
public class ItemInfo {
    private final String name;   // 불변
    private double cost;         // 내부 구현용
    private double taxRate = 0.2; // 세금율

    public ItemInfo(String name, double price) {
        this.name = name;
        this.cost = calculateCostFromPrice(price);
    }

    public String getName() { 
        return name; 
    }

    public double getPrice() {
        return calculatePriceFromCost(cost);
    }

    public void setPrice(double price) {
        this.cost = calculateCostFromPrice(price);
    }

    // 내부 구현: 가격과 비용 간의 변환
    private double calculatePriceFromCost(double cost) {
        return cost * (1 + taxRate);
    }

    private double calculateCostFromPrice(double price) {
        return price / (1 + taxRate);
    }

    @Override
    public String toString() {
        return String.format("%s %d원", name, (int)getPrice());
    }
}