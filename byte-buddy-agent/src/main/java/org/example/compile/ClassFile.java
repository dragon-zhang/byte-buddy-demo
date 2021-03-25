package org.example.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author SuccessZhang
 * @date 2020/7/17
 */
public class ClassFile extends SimpleJavaFileObject {

    /**
     * 用于保存字节码
     */
    private ByteArrayOutputStream outputStream;

    public ClassFile(String name) {
        super(URI.create(name + Kind.CLASS.extension), Kind.CLASS);
        this.outputStream = new ByteArrayOutputStream();
    }

    /**
     * 跟踪获取字节码输出流的调用链路。
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
     * @see JavaFileManager#getJavaFileForOutput(JavaFileManager.Location, String, Kind, FileObject)
     * @see ClassFileManager#getJavaFileForOutput(JavaFileManager.Location, String, Kind, FileObject)
     * @see JavaFileObject#openOutputStream()
     * @see ClassFile#openOutputStream()
     * <p>
     * 跟踪字节码的“翻译”过程。
     * @see com.sun.tools.javac.jvm.ClassWriter#writeClassFile(OutputStream, com.sun.tools.javac.code.Symbol.ClassSymbol)
     */
    @Override
    public OutputStream openOutputStream() {
        return this.outputStream;
    }

    public byte[] getBytes() {
        return this.outputStream.toByteArray();
    }
}
