package org.example;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangzicheng
 * @date 2021/03/06
 */
@RestController
public class ChangeClassDefine implements BeanNameAware {

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
    @GetMapping("/change/test")
    public String test(@RequestParam String name) throws Exception {
        return this.name + " say hello, " + name;
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }
}
