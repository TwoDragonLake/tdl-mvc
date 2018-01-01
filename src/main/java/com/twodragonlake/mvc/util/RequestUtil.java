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

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Request related工具类.
 * referenced from spring.AntPathMatcher
 *
 * @author : dixingxing
 * @version : 1.0
 * @since : 2018/1/18 11:02
 */
public class RequestUtil {

    /**
     * get real request url without context path and '/'.
     *
     * @param request HttpServletRequest
     * @return real request url
     */
    public static String getRealRequestURIWithoutPrefix(HttpServletRequest request) {
        String realUri = getRealRequestURI(request);
        return realUri.replaceFirst("/", "");
    }

    /**
     * get real request url without context path.
     *
     * @param request HttpServletRequest
     * @return real request url
     */
    public static String getRealRequestURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        // check contextPath
        // if empty or equals with /, then return it
        if (StringUtil.isEmpty(contextPath) || "/".equals(contextPath)) {
            return requestURI;
        }

        // check requestURI
        if (StringUtil.isEmpty(requestURI)) {
            return requestURI;
        }

        // remove contextPath
        if (requestURI.contains(contextPath)) {
            return requestURI.replaceFirst(contextPath, "");
        }

        return requestURI;
    }

    /**
     * get all parameter from http request
     *
     * @param request HttpServletRequest
     * @return Map
     */
    public static Map<String, String[]> getRequestParamMapping(HttpServletRequest request) {
        return request.getParameterMap();
    }

    /**
     * Get parameter list of request, for example : 'test.do?test1=1&test2=2'
     * --> 'test1=1&test2=2'
     *
     * @param request HttpServletRequest
     * @return if not parameter , return empty list not null.
     */
    public static List<String> getRequestParamStrList(HttpServletRequest request) {
        Assert.notNull(request);

        // get parameter string
        String paramsStr = request.getQueryString();

        if (StringUtils.isEmpty(paramsStr)) {
            return new ArrayList<>();
        }

        String[] params = paramsStr.split("&");

        if (ArrayUtil.isArrayEmpty(params)) {
            return Arrays.asList(params);
        }

        return new ArrayList<>();
    }
}
