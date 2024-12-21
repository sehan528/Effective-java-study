package chapter4.item22.ex3;

import chapter4.item22.ex2.PhysicalConstants;

import static chapter4.item22.ex2.PhysicalConstants.*;

public class Test {
    //스태틱 임포트로 깔끔하게 가독성 좋게
    //인터페이스는 타입을 정의하는 용도로만 사용하기, 상수 공개용 수단으로 사용 x
    double atoms(double mols) {
        return AVOGADROS_NUMBER;
    }
}
