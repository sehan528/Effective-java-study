package org.week3.item24;

/**
 * 비정적 멤버 클래스가 필요한 예제
 */
public class UIComponent {
    private boolean isClicked = false;

    // 비정적 멤버 클래스 - 이벤트 리스너
    public class ClickListener {
        public void onClick() {
            // 외부 클래스의 상태 변경이 필요한 경우
            isClicked = true;
            System.out.println("버튼 클릭 이벤트 발생");
        }
    }

    public ClickListener createClickListener() {
        return new ClickListener();
    }

    public boolean isClicked() {
        return isClicked;
    }
}