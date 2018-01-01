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

package com.twodragonlake.mvc.interceptor.annotation;


import com.twodragonlake.mvc.interceptor.RequestInterceptor;

import java.lang.annotation.*;

/**
 * 清除的拦截器列表，仅用于方法上清除类上的某几个拦截器.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2018/8/12 10:35
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Clear {
    Class<? extends RequestInterceptor>[] value() default {};
}