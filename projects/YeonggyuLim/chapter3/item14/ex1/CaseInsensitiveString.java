package chapter3.item14.ex1;

public final class CaseInsensitiveString implements Comparable<CaseInsensitiveString>{
    private final String s;
    public CaseInsensitiveString(String s) {
        this.s = s;
    }
    //객체 참조 필드가 하나뿐인 경우
    //두 객체 동일하면 : 0
    //첫 번째 객체 < 두 번째 객체 : 음수
    //첫 번째 객체 > 두 번째 객체 : 양수
    //compareTo는 대소문자 구분 없이 문자열 비교

    @Override
    public int compareTo(CaseInsensitiveString cis) {
        return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
    }
}
