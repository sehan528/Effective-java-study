package chapter4.item22.ex1;


//상수 인터페이스를 잘못 상용한 예
//상수를 공개할 목적이면 차라리 그 클래스나 인터페이스 자체에 추가하기
//또는 인스턴스화 할 수 없는 유틸리티 클래스에 담아 공개하기
public interface PhysicalConstants {
    static final double AVOGADROS_NUMBER = 6.022;
    static final double BOLTZMANN_CONSTANT = 1.380;
    static final double ELECTRON_MASS = 9.109;
}
