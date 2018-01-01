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
 * @filename ContextFactory.java
 * @createtime 2015年7月22日
 * @author dingxiangyong
 * @comment
 */
package com.twodragonlake.mvc.context;


import com.twodragonlake.mvc.DispatchServlet;
import com.twodragonlake.mvc.util.StringUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * @author Big Martin
 */
public class ContextFactory {

    /**
     * application context
     */
    private static ApplicationContext context;

    /**
     * logger
     */
    public static final Logger logger = Logger.getLogger(DispatchServlet.class);

    /**
     * load configuration
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext(ServletContext servletContext) {
        return getApplicationContext(servletContext, null, true);
    }

    /**
     * load configuration
     */
    public static ApplicationContext getApplicationContext(ServletContext servletContext, ServletConfig sc,
                                                           boolean isNeedReload) {
        // check whether cached
        if (context != null && !isNeedReload) {
            return context;
        }

        if (StringUtil.isEmpty(sc.getInitParameter("scanPackage"))) {
            throw new RuntimeException("Failed to load context, please set scanPackage in init-param of DispatchServlet.");
        }
        if (StringUtil.isEmpty(sc.getInitParameter("viewPrefix"))) {
            throw new RuntimeException("Failed to load context, please set viewPrefix in init-param of DispatchServlet.");
        }
        if (StringUtil.isEmpty(sc.getInitParameter("viewSuffix"))) {
            throw new RuntimeException("Failed to load context, please set viewSuffix in init-param of DispatchServlet.");
        }

        context = new ApplicationContext();
        context.setScanPackage(sc.getInitParameter("scanPackage"));
        context.setViewPrefix(sc.getInitParameter("viewPrefix"));
        context.setViewSuffix(sc.getInitParameter("viewSuffix"));

        return context;
    }
}
