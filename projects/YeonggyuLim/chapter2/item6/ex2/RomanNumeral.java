package chapter2.item6.ex2;

import java.util.regex.Pattern;

public class RomanNumeral {
    //정규 표현식 객체를 매번 생성함
    public static boolean isRomanNumeral(String s) {
        return s.matches("^(?i)(M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3}))$");
    }


    //개선된 버전에서는 싱글톤 패턴과 유사하게 하나의 인스턴스만 final 로 선언하여 재사용
    //진짜 싱글톤 패턴을 쓰면 안되나?
    //이 코드에서는 추가적인 상태 관리나 확장이 필요 없으므로 굳이 싱글턴 패턴을 사용할 이유가 없음
    //추가상태 :
    // 1. 카운터, 캐시데이터, 설정 값, 세션정보 등 동적 데이터 관리
    // 2. 리소스 관리 : 파일 핸들, db 연결
    // 3. 다양한 로직 수행
    private static final Pattern ROMAN = Pattern.compile(
            "^(?i)(M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3}))$"
    );

    public static boolean isRomanNumeralV2(String s) {
        return ROMAN.matcher(s).matches();
    }
}
