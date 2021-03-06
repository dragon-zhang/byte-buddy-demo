package org.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.MemberAttributeExtension;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author zhangzicheng
 * @date 2021/03/06
 */
public class ByteBuddyDemo {

    public static void main(String[] args) throws Exception {
        Class<TestController> klass = TestController.class;
        ByteBuddy byteBuddy = new ByteBuddy();
        DynamicType.Builder<?> builder = byteBuddy.redefine(klass);
        boolean classPresent = klass.isAnnotationPresent(Deprecated.class);
        if (!classPresent) {
            builder = builder.annotateType(AnnotationDescription.Builder.ofType(Deprecated.class).build());
        }
        for (Field field : klass.getDeclaredFields()) {
            field.setAccessible(true);
            boolean fieldPresent = field.isAnnotationPresent(Deprecated.class);
            if (!fieldPresent) {
                builder = builder.field(ElementMatchers.named(field.getName()))
                        .annotateField(AnnotationDescription.Builder
                                .ofType(Deprecated.class).build());
            }
        }
        for (Method method : klass.getDeclaredMethods()) {
            method.setAccessible(true);
            boolean methodPresent = method.isAnnotationPresent(Deprecated.class);
            if (!methodPresent) {
                //只加注解，不改变原有的方法体，找了好久...
                builder = builder.visit(new MemberAttributeExtension.ForMethod()
                        .annotateMethod(AnnotationDescription.Builder
                                .ofType(Deprecated.class).build())
                        .on(ElementMatchers.named(method.getName())));
            }
        }
        String path = klass.getResource("/").getPath();
        builder.make()
                .saveIn(new File(path));
        System.out.println("redefine successfully !!!");
    }

}
