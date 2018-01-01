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

package com.twodragonlake.mvc.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口拦截器.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2018/8/9 10:13
 */
public interface RequestInterceptor {
    /**
     * 拦截器描述
     */
    String description();

    /**
     * 前置拦截方法
     *
     * @param request  http request
     * @param response http response
     * @return true:请求继续，false:请求终止
     * @throws Exception 异常
     */
    boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 后置拦截方法，如果有异常抛出，则该拦截器不走
     *
     * @param request   HttpServletRequest
     * @param response  HttpServletResponse
     * @param returnObj Object
     * @throws Exception 异常
     */
    void postHandle(HttpServletRequest request, HttpServletResponse response, Object returnObj) throws Exception;

    /**
     * 操作完成拦截方法，和postHandle的区别在于：不管有没有异常抛出，该拦截方法必走
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    void commitHandle(HttpServletRequest request, HttpServletResponse response);
}
