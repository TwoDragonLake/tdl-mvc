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

package com.twodragonlake.mvc.domain;

import com.twodragonlake.mvc.enums.RequestDataType;
import com.twodragonlake.mvc.enums.ReturnType;
import com.twodragonlake.mvc.interceptor.RequestInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 路由信息承载器.
 *
 * @author : Big Martin
 * @version : 1.0
 * @since : 2016/11/25 22:56
 */
public class RouteInfoHolder {

    /**
     * uri, like 'test.do'
     */
    private String pattern;

    /**
     * json or page.method use only
     */
    private ReturnType returnType;

    /**
     * form_data or json
     */
    private RequestDataType requestDataType;

    private Method method;

    /**
     * controller instance
     */
    private Object instance;

    /**
     * 接口拦截器栈
     */
    private Set<RequestInterceptor> interceptorStack;

    public RouteInfoHolder(String pattern, RequestDataType requestDataType, ReturnType returnType, Method method,
                           Object instance) {
        super();
        this.pattern = pattern;
        this.requestDataType = requestDataType;
        this.returnType = returnType;
        this.method = method;
        this.instance = instance;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public RequestDataType getRequestDataType() {
        return requestDataType;
    }

    public void setRequestDataType(RequestDataType requestDataType) {
        this.requestDataType = requestDataType;
    }

    public Set<RequestInterceptor> getInterceptorStack() {
        return interceptorStack;
    }

    public void setInterceptorStack(Set<RequestInterceptor> interceptorStack) {
        this.interceptorStack = interceptorStack;
    }

    /**
     * 执行前置拦截器方法
     */
    public boolean doInterceptPre(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

}
