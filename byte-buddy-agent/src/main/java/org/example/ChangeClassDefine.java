package org.example;

import org.springframework.stereotype.Component;

/**
 * @author zhangzicheng
 * @date 2021/03/06
 */
@Component
public class ChangeClassDefine {

    /**
     * beanName
     */
    private String name;

    /**
     * 方法上的描述
     *
     * @param name 参数
     * @return 返回值
     * @throws java.lang.Exception
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
