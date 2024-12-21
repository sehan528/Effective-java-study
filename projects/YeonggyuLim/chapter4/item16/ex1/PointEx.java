package chapter4.item16.ex1;

class PointEx {
    //캡슐화가 지켜짐 --> 객체 내부 구현을 숨기고 외부와의 인터페이스를 통해서만 접근 하는것


    private double x;
    private double y;

    public PointEx(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
