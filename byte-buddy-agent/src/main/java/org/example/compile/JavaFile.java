package org.example.compile;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * @author SuccessZhang
 * @date 2020/7/17
 */
public class JavaFile extends SimpleJavaFileObject {

    /**
     * 用于保存源代码
     */
    private String content;

    public JavaFile(String name, String content) {
        super(URI.create(name + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = content;
    }

    /**
     * 跟踪获取源码的调用链路。
     *
     * @see com.sun.tools.javac.api.JavacTaskImpl#call()
     * @see com.sun.tools.javac.api.JavacTaskImpl#doCall()
     * @see com.sun.tools.javac.main.Main#compile(String[], String[], com.sun.tools.javac.util.Context, com.sun.tools.javac.util.List, Iterable)
     * @see com.sun.tools.javac.main.JavaCompiler#compile(com.sun.tools.javac.util.List, com.sun.tools.javac.util.List, Iterable)
     * @see com.sun.tools.javac.main.JavaCompiler#parseFiles(Iterable)
     * @see com.sun.tools.javac.main.JavaCompiler#parse(javax.tools.JavaFileObject)
     * @see com.sun.tools.javac.main.JavaCompiler#readSource(javax.tools.JavaFileObject)
     * @see javax.tools.JavaFileObject#getCharContent(boolean)
     * @see JavaFile#getCharContent(boolean)
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return this.content;
    }

}
