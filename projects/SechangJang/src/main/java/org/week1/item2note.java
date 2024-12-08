public class item2note {
    public static void main(String[] args) {
    }
}


public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int solium;
    private final int carbohydrate;
}

객체를 생성하고 파라미터를 채워나가는 3가지 방법 점층적 생성자 패턴, 자바빈즈, 빌더

점층적 생성자 패턴. 즉 여러개의 생성자를 두는 방법은 코드에 혼란함을 야기한다. 예제 처럼 같은 타입의 parameter가 많아지면 어떤 순서에 어떤 값을 넣어야 되는지 등 명확치 않아 실수하기 쉽다.
ex) 6개 까지의 생성자 만들었는데 중간에 하나 추가 되어서 파라매터 인덱싱이 꼬인다면 나머지 생성자들도 전부 꼬일 수 있다. // 인지 못하는 에러가 되기 쉽기에 더 위험하다.

Lombok 없이 Builder를 직접 만든다면 다음과 같다.

public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;

    public static class Builder {
        private final int servingSize;
        private final int servings;
        private final int calories = 0;
        // 1. 빌더를 만드는 생성자. (필수 요소 생성)
        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }
        // 2. 빌더 체인을 통해 추가 되는 추가 빌더
        public Builder calories(int val) {
            calories = val;
            return this;
        }
        // 3. 빌드 메서드 호출 (상위 객체 res)
        public NutritionFacts build() {
            return  new NutritionFacts(this);
        }
    }
}

Lombok을 사용한 경우

@Data
@Builder(builderMethodNmae = "hiddenBuilder")
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    @Builder.Default private final int calories = 0;
    @Builder.Default private final int fat = 0;
    @Builder.Default private final int solium = 0;
    @Builder.Default private final int carbohydrate = 0;
    // 필수 빌더 지정 = 'NutritionFactsBuilder' 를 만들기 위해선 반드시 2개의 값을 넣어야함. (필수 값 안 넣을 시 제약)
    // = 처음엔 이 히든 빌더를 만들고 그 이후엔 롬복에서 제공하는 빌더 패턴을 사용하겠음.
    public static NutritionFactsBuilder builder(int servingSize int servings) {
        return hiddenBuilder()
            .servingSize(servingSize);
            .servings(servings);
    }
}

