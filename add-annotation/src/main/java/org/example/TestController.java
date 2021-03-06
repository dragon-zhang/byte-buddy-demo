package org.example;

/**
 * 类上的描述
 *
 * @author zhangzicheng
 * @version 1.0.0
 * @date 2021/03/01
 * @exception Exception
 * @throws Exception
 * @link java.lang.Exception
 * @see Exception
 * @since 1.0.0
 */
public class TestController {

    /**
     * beanName
     */
    private String name;

    /**
     * 方法上的描述
     *
     * @param name 参数
     * @return 返回值
     * @throws Exception
     * @author zhangzicheng
     * @version 1.0.0
     */
    public String test(String name) throws Exception {
        return this.name + " say hello, " + name;
    }

    public void setBeanName(String name) {
        this.name = name;
    }
}
