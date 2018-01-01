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

import com.twodragonlake.mvc.DispatchServlet;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Response 工具类.
 *
 * @author : dixingxing
 * @version : 1.0
 * @since : 2018/1/18 11:02
 */
public class ResponseUtil {

    /**
     * 默认字符编码集
     */
    private static final String CHARSET = "UTF-8";

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(DispatchServlet.class);

    /**
     * 向请求发起方输出字符串
     *
     * @param response HttpServletResponse
     * @param list     输出的字符列表
     */
    public static void write(HttpServletResponse response, String[] list) {
        write(response, null, CHARSET, list);
    }

    /**
     * 向请求发起方输出字符串
     *
     * @param response HttpServletResponse
     * @param header   响应头属性
     * @param charSet  字符编码集
     * @param list     输出的字符列表
     */
    public static void write(HttpServletResponse response, Map<String, String> header, String charSet, String[] list) {
        if (null != header && header.isEmpty()) {
            for (Entry<String, String> et : header.entrySet()) {
                response.setHeader(et.getKey(), et.getValue());
            }
        } else {
            response.setHeader("content-type", "text/html;charset=UTF-8");
        }

        if (StringUtil.isEmpty(charSet)) {
            charSet = CHARSET;
        }

        response.setCharacterEncoding(charSet);

        PrintWriter out;
        try {
            out = response.getWriter();
            if (null != list) {
                for (String str : list) {
                    out.println(str);
                }
            }
        } catch (IOException e) {
            logger.error("write error", e);
        }
    }

}
