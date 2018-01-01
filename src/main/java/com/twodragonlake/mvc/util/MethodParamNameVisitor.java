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

package com.twodragonlake.mvc.util;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * asm的方法名字查看器(主内容参考自网络).
 *
 * @author : dixingxing
 * @version : 1.0
 * @since : 2016/1/18 11:25
 */
public final class MethodParamNameVisitor {

    /**
     * 方法名字查看器结果缓存
     */
    private static Map<String, String[]> methodParamNamesCache = new HashMap<>();

    private MethodParamNameVisitor() {
    }

    /**
     * 比较参数类型是否一致
     *
     * @param types   asm的类型({@link Type})
     * @param classes java 类型({@link Class})
     * @return boolean
     */
    private static boolean sameType(Type[] types, Class<?>[] classes) {
        // 个数不同
        if (types.length != classes.length) {
            return false;
        }

        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(classes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取方法参数
     *
     * @param paramTypes Class
     * @return String
     * @since 2017年1月18日 下午3:56:17
     */
    private static String getMethodParamString(Class<?>[] paramTypes) {
        StringBuilder sb = new StringBuilder();

        for (Class<?> paramType : paramTypes) {
            sb.append(".");
            sb.append(paramType.getName());
        }
        return sb.toString();
    }

    /**
     * 获取方法的参数名
     *
     * @param m Method
     * @return String
     */
    public static String[] getMethodParamNames(final Method m) {
        // 先取缓存中取
        String key = m.getDeclaringClass().getName() + "." + m.getName() + getMethodParamString(m.getParameterTypes());

        if (methodParamNamesCache.containsKey(key)) {
            return methodParamNamesCache.get(key);
        }

        // 缓存中没有，则通过ASM取
        final String[] paramNames = new String[m.getParameterTypes().length];

        // get clazz name
        final Class<?> clazz = m.getDeclaringClass();

        // load class
        InputStream is = clazz.getResourceAsStream(ClassUtil.getClassFileName(clazz));

        if (is == null) {
            return null;
        }

        // get classReader
        ClassReader reader;

        try {
            reader = new ClassReader(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        reader.accept(new ClassVisitor(Opcodes.ASM4) {
            @Override
            public MethodVisitor visitMethod(final int access, final String name, final String desc,
                                             final String signature, final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                // 方法名相同并且参数个数相同
                if (!name.equals(m.getName()) || !sameType(args, m.getParameterTypes())) {
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
                MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM4, v) {
                    @Override
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
                                                   int index) {
                        int i = index - 1;
                        // 如果是静态方法，则第一就是参数
                        // 如果不是静态方法，则第一个是"this"，然后才是方法的参数
                        if (Modifier.isStatic(m.getModifiers())) {
                            i = index;
                        }
                        if (i >= 0 && i < paramNames.length) {
                            paramNames[i] = name;
                        }
                        super.visitLocalVariable(name, desc, signature, start, end, index);
                    }

                };
            }
        }, 0);
        methodParamNamesCache.put(key, paramNames);
        return paramNames;
    }

}
