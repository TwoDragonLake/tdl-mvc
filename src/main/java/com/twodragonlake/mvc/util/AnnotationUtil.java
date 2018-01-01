/*
 * Copyright (C) 2018 The TwoDragonLake Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @filename AnnotationUtil.java
 * @createtime 2015.7.12
 * @author Big Martin
 * @comment
 */
package com.twodragonlake.mvc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 注解工具类.
 *
 * @author : Big Martin
 * @version : 1.0
 * @since : 2018/1/18 11:02
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
