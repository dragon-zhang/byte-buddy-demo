package org.example.compile;

import lombok.Getter;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * @author SuccessZhang
 * @date 2020/7/17
 */
public class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    @Getter
    private ClassFile classFile;

    public ClassFileManager() {
        super(COMPILER.getStandardFileManager(null, null, StandardCharsets.UTF_8));
    }

    public boolean compile(JavaFile javaFile) {
        JavaCompiler.CompilationTask task = COMPILER.getTask(null, this, null, null, null,
                Collections.singletonList(javaFile));
        return task.call();
    }

    /**
     * 跟踪获取字节码容器的调用链路。
     *
     * @see com.sun.tools.javac.api.JavacTaskImpl#call()
     * @see com.sun.tools.javac.api.JavacTaskImpl#doCall()
     * @see com.sun.tools.javac.main.Main#compile(String[], String[], com.sun.tools.javac.util.Context, com.sun.tools.javac.util.List, Iterable)
     * @see com.sun.tools.javac.main.JavaCompiler#compile(com.sun.tools.javac.util.List, com.sun.tools.javac.util.List, Iterable)
     * @see com.sun.tools.javac.main.JavaCompiler#compile2()
     * @see com.sun.tools.javac.main.JavaCompiler#generate(Queue)
     * @see com.sun.tools.javac.main.JavaCompiler#generate(Queue, Queue)
     * @see com.sun.tools.javac.main.JavaCompiler#genCode(com.sun.tools.javac.comp.Env, com.sun.tools.javac.tree.JCTree.JCClassDecl)
     * @see com.sun.tools.javac.jvm.ClassWriter#writeClass(com.sun.tools.javac.code.Symbol.ClassSymbol)
     * @see JavaFileManager#getJavaFileForOutput(Location, String, JavaFileObject.Kind, FileObject)
     * @see ClassFileManager#getJavaFileForOutput(Location, String, JavaFileObject.Kind, FileObject)
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
        this.classFile = new ClassFile(className);
        return this.classFile;
    }

}