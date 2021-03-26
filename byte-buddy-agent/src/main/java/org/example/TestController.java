package org.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.MemberAttributeExtension;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import org.example.compile.ClassFileManager;
import org.example.compile.JavaFile;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 类上的描述
 *
 * @author zhangzicheng
 * @version 1.0.0
 * @date 2021/03/01
 * @exception Exception
 * @throws Exception
 * @link Exception
 * @see Exception
 * @since 1.0.0
 */
@RestController
public class TestController implements BeanNameAware {

    /**
     * beanName
     */
    private String name;

    @Resource
    private ClassFileManager classFileManager;

    @Resource
    private ChangeClassDefine changeClassDefine;

    /**
     * 方法上的描述
     *
     * @param param 参数
     * @return 返回值
     */
    @GetMapping("/test")
    public String test(@RequestParam String param) {
        return name + " say hello, " + param;
    }

    @GetMapping("/redefine")
    public String redefine() throws Exception {
        ByteBuddyAgent.install();
        Class<ChangeClassDefine> klass = ChangeClassDefine.class;
        ByteBuddy byteBuddy = new ByteBuddy();
        DynamicType.Builder<?> builder = byteBuddy.redefine(klass);
        boolean classPresent = klass.isAnnotationPresent(Deprecated.class);
        System.out.println("pre " + klass.isAnnotationPresent(Deprecated.class));
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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = classLoader.getResource("/").getPath();
        System.out.println("saveIn->" + path);
        DynamicType.Unloaded<?> unloaded = builder.make();
        unloaded.saveIn(new File(path));
        unloaded.load(classLoader, ClassReloadingStrategy.fromInstalledAgent());
        try {
            return "redefined";
        } finally {
            System.out.println("after " + klass.isAnnotationPresent(Deprecated.class));
        }
    }

    @GetMapping("/replace")
    public String replace() throws Exception {
        Instrumentation instrumentation = ByteBuddyAgent.install();
        String source = "package org.example;\n" +
                "\n" +
                "import org.springframework.beans.factory.BeanNameAware;\n" +
                "import org.springframework.web.bind.annotation.GetMapping;\n" +
                "import org.springframework.web.bind.annotation.RequestParam;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "\n" +
                "/**\n" +
                " * @author zhangzicheng\n" +
                " * @date 2021/03/06\n" +
                " */\n" +
                "@RestController\n" +
                "public class ChangeClassDefine implements BeanNameAware {\n" +
                "\n" +
                "    /**\n" +
                "     * beanName\n" +
                "     */\n" +
                "    private String name;\n" +
                "\n" +
                "    /**\n" +
                "     * 方法上的描述\n" +
                "     *\n" +
                "     * @param name 参数\n" +
                "     * @return 返回值\n" +
                "     * @throws java.lang.Exception\n" +
                "     * @author zhangzicheng\n" +
                "     * @version 1.0.0\n" +
                "     */\n" +
                "    @GetMapping(\"/change/test\")\n" +
                "    public String test(@RequestParam String name) throws Exception {\n" +
                "        return \"replaced\";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void setBeanName(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "}\n";
        JavaFile javaFile = new JavaFile(ChangeClassDefine.class.getSimpleName(), source);
        if (!classFileManager.compile(javaFile)) {
            System.out.println("编译失败");
            return "failed";
        }
        instrumentation.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) ->
                classFileManager.getClassFile(ChangeClassDefine.class.getName()).getBytes(), true);
        instrumentation.retransformClasses(ChangeClassDefine.class);
        return "replaced";
    }

    @GetMapping("/changeMethod")
    public String changeMethod() throws Exception {
        ByteBuddyAgent.install();
        Class<ChangeClassDefine> klass = ChangeClassDefine.class;
        ByteBuddy byteBuddy = new ByteBuddy();
        DynamicType.Unloaded<?> unloaded = byteBuddy.redefine(klass)
                .method(ElementMatchers.named("test"))
                .intercept(FixedValue.value("changed"))
                .make();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = classLoader.getResource("/").getPath();
        System.out.println("saveIn->" + path);
        unloaded.saveIn(new File(path));
        unloaded.load(classLoader, ClassReloadingStrategy.fromInstalledAgent());
        return "changed";
    }

    @GetMapping("/check")
    public String check() throws Exception {
        System.out.println(changeClassDefine + " " + changeClassDefine.hashCode());
        return changeClassDefine.test("haha");
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }
}
