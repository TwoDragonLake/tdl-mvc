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
 * @filename Route.java
 * @time 2015.7.12
 * @author Big Martin
 * @comment
 */
package com.twodragonlake.mvc.annotation;


import com.twodragonlake.mvc.enums.RequestDataType;
import com.twodragonlake.mvc.enums.ReturnType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Big Martin
 */

/**
 * 拦截器注解.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2016/8/11 17:33
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    /**
     * The primary mapping expressed by this annotation.
     */
    String value() default "";

    /**
     * The parameters of the mapped request, narrowing the primary mapping.
     */
    String[] params() default {};

    /**
     * Controller return json or page. NOTE. this param method use only.
     */
    ReturnType returnType() default ReturnType.PAGE;

    /**
     * 请求数据类型，JSON/FORM_DATA/ALL
     */
    RequestDataType requestDataType() default RequestDataType.FORM_DATA;
}
