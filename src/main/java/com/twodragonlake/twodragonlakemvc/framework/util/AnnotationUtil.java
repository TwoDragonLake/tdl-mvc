/**
 * @filename AnnotationUtil.java
 * @createtime 2015.7.12
 * @author Big Martin
 * @comment 
 */
package com.twodragonlake.twodragonlakemvc.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * @author Big Martin
 *
 */
public class AnnotationUtil {
    
    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        Assert.notNull(clazz, "Class must not be null");
        A annotation = clazz.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        //在当前clazz的所有接口中查找
        for (Class<?> ifc : clazz.getInterfaces()) {
            annotation = findAnnotation(ifc, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }
        //isAssignableFrom   是用来判断一个类Class1和另一个类Class2是否相同或是另一个类的超类或接口
        //此处判断Annotation是不是clazz的超类或者接口，不是返回true
        //在当前clazz的注解的超类型后者接口上查找
        if (!Annotation.class.isAssignableFrom(clazz)) {
            for (Annotation ann : clazz.getAnnotations()) {
                annotation = findAnnotation(ann.annotationType(), annotationType);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        //在当前clazz的父类上查找
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null || superClass == Object.class) {
            return null;
        }
        return findAnnotation(superClass, annotationType);
    }
    
    public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
        Assert.notNull(method, "Method must not be null");
        
        A annotation = method.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        
        return null;
    }
    
    public static <A extends Annotation> A findAnnotation(Field field, Class<A> annotationType) {
        Assert.notNull(field, "Method must not be null");
        
        A annotation = field.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        
        return null;
    }
}
