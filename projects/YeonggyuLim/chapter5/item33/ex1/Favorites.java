package chapter5.item33.ex1;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Favorites {
    private final Map<Class<?>, Object> favorites = new HashMap<>();

    // 이 코드에서는 로타입 넘기면 타입 안정성이 쉽게 깨짐
//        public <T> void putFavorite(Class<T> type, T instance) {
//        favorites.put(Objects.requireNonNull(type), instance);
//    }

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }

    //Class.cast --> 타임 검사를 수행한 후 캐스팅

    //putFavorite 에 type.cast 를 적용하면 타입 불변식을 어기는 일이 없음
    //getFavorite 에 type.cast 를 적용하면 성능은 좀 더 좋음 (값을 가져올 때만 검사하니까)

    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }


    public static void main(String[] args) {
        Favorites f = new Favorites();

        //로타입 넣을시 불변성 쉽게 꺠짐
        //String.class --> Class<String> 제네릭 타입임
        //Class<String>은 String.class 와 같지만
        //Class rawType 은 제네릭 타입이 생략된 그냥 Class 로 선언돼서 로타입
        Class rawType = String.class;
        f.putFavorite(rawType, 123);


        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 0xcafebabe);
        f.putFavorite(Class.class, Favorites.class);

        String favoriteString = f.getFavorite(String.class);
        Integer favoriteInteger = f.getFavorite(Integer.class);
        Class favoriteClass = f.getFavorite(Class.class);
        System.out.printf("%s %x %s%n", favoriteString,
                favoriteInteger, favoriteClass.getName());
    }
}
