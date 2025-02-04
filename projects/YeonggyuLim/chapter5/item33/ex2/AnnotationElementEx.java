package chapter5.item33.ex2;


import jdk.jfr.AnnotationElement;

import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;

public class AnnotationElementEx {

    static Annotation getAnnotation(AnnotatedElement element,
                                    String annotationTypeName) {
        Class<?> annotationType = null;
        //어노테이션 타입 로드
        //어노테이션 타입 이름을 동적으로 클래스 객체로 변환
        try {
            annotationType = Class.forName(annotationTypeName);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
        //어노테이션 반환
        //클래스가 어노테이션의 서브타입인지 확인
        return element.getAnnotation(
                annotationType.asSubclass(Annotation.class));

    }

    @Deprecated
    static class MyClass {
    }

    public static void main(String[] args) {
        AnnotatedElement element = MyClass.class;

        String annotationTypeName = "java.lang.Deprecated";
        Annotation annotation = AnnotationElementEx.getAnnotation(element, annotationTypeName);
        if (annotation != null) {
            System.out.println("어노테이션 : " + annotation);
        } else {
            System.out.println("어노테이션 x");
        }
    }
}
