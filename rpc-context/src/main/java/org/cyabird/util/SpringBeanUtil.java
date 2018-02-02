package org.cyabird.util;

import org.springframework.beans.factory.BeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @create: 2018-01-25
 * @description:
 */
public class SpringBeanUtil {
    public static <T> List<T> getMultiBeans(BeanFactory beanFactory, String names, String pattern, Class<T> clazz) {
        String[] nameArr = names.split(pattern);
        List<T> beans = new ArrayList<>();
        for (String name : nameArr) {
            if (name != null && name.length() > 0) {
                beans.add(beanFactory.getBean(name, clazz));
            }
        }
        return beans;
    }
}
