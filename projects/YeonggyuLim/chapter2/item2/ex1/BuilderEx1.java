package chapter2.item2.ex1;

public class BuilderEx1 {
    public static void main(String[] args) {
        //필수 필드만
        NutritionFacts basicFacts = new NutritionFacts.Builder(240, 8).build();

        //필수 필드 + 선택 필드
        NutritionFacts fullFacts = new NutritionFacts.Builder(240, 8)
                .calories(100)
                .fat(1)
                .sodium(2)
                .build();

        System.out.println(basicFacts.toString());
        System.out.println(fullFacts.toString());
    }
}
