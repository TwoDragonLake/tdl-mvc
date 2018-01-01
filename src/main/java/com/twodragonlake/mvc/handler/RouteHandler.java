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
 * @filename RouteHandler.java
 * @createtime 2015.7.12
 * @author Big Martin
 * @comment
 */
package com.twodragonlake.mvc.handler;

import com.twodragonlake.mvc.annotation.RequestMapping;
import com.twodragonlake.mvc.domain.RouteInfoHolder;
import com.twodragonlake.mvc.domain.RouteMatchResult;
import com.twodragonlake.mvc.interceptor.RequestInterceptor;
import com.twodragonlake.mvc.interceptor.annotation.Clear;
import com.twodragonlake.mvc.interceptor.annotation.Intercept;
import com.twodragonlake.mvc.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 路由处理.
 *
 * @author : Big Martin
 * @version : 1.0
 * @since : 2015/7/12 21:20
 */
public class RouteHandler {

    // singleton instance
    private static final RouteHandler INSTANCE = new RouteHandler();

    /**
     * 路由控制器集合
     */
    private Map<String, RouteInfoHolder> routeControls = new HashMap<String, RouteInfoHolder>();

    /**
     * 路由匹配缓存（一次匹配成功后，计入该cache中，避免多次匹配）
     */
    private Map<String, String> routeMatchCache = new HashMap<String, String>();

    private Log logger = LogFactory.getLog(RouteHandler.class);

    private RouteHandler() {

    }

    /**
     * get instance
     *
     * @return RouteHandler
     */
    public static RouteHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Register a handler to mvc
     *
     * @param clazz Class
     */
    public void registerRouteByClass(Class<?> clazz) {
        // find @controller annotation
        Controller controller = AnnotationUtil.findAnnotation(clazz, Controller.class);

        // if it's a controller
        if (controller != null) {
            // 查找RequestMapping注解
            RequestMapping requestMapping = AnnotationUtil.findAnnotation(clazz, RequestMapping.class);
            // 根据各个method的注解注册路由
            if (requestMapping != null) {
                registerRouteByMethod(StringUtil.standardUrlPattern(requestMapping.value()), clazz);
            }
        }
    }

    /**
     * 根据方法注册路由
     *
     * @param classRoute classRoute
     * @param clazz      Class
     * @author xiangyong.ding@weimob.com
     * @since 2016年11月30日 下午11:49:51
     */
    public void registerRouteByMethod(String classRoute, Class<?> clazz) {
        Assert.notNull(clazz, "Class cannot be null.");

        // controller instance
        String beanSpringName = StringUtils.isEmpty(clazz.getAnnotation(Controller.class).value())
                ? StringUtil.lowerFirst(clazz.getSimpleName()) : clazz.getAnnotation(Controller.class).value();
        Object instance = SpringBeanUtils.getBean(beanSpringName);

        Assert.notNull(instance, "Fail to get controller instance, class:" + clazz);

        // 先load控制器上的拦截器
        Set<Class<? extends RequestInterceptor>> classInterceptors = loadInterceptor(clazz);

        // 控制器扫描结果
        Map<Method, RouteInfoHolder> results = new HashMap<Method, RouteInfoHolder>();

        // 扫描所有方法，加载控制器和拦截器
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            // 加载方法上接口配置
            // find @RequestMapping
            RequestMapping requestMapping = AnnotationUtil.findAnnotation(method, RequestMapping.class);

            RouteInfoHolder routeInfoHolder;
            if (requestMapping != null) {
                // create MappingInfo
                routeInfoHolder = new RouteInfoHolder(requestMapping.value(), requestMapping.requestDataType(),
                        requestMapping.returnType(), method, instance);

                // 加载方法上拦截器配置
                Set<Class<? extends RequestInterceptor>> methodInterceptors = loadInterceptor(method);
                methodInterceptors.addAll(classInterceptors);

                // 加载clear注解
                loadClearInterceptor(method, methodInterceptors);

                // 从spring获取拦截器实例
                Set<RequestInterceptor> interceptorInstances = new HashSet<RequestInterceptor>();
                for (Class<? extends RequestInterceptor> itemInterceptorClazz : methodInterceptors) {
                    Object itemInterceptorIns = SpringBeanUtils
                            .getBean(StringUtil.lowerFirst(itemInterceptorClazz.getSimpleName()));

                    // 如果获取实例失败，则丢弃
                    if (itemInterceptorIns == null) {
                        continue;
                    }
                    interceptorInstances.add((RequestInterceptor) itemInterceptorIns);
                }

                // 设定接口拦截器栈
                routeInfoHolder.setInterceptorStack(interceptorInstances);
                results.put(method, routeInfoHolder);

                // 加入控制器路由集合
                routeControls.put(PathUtil.getInstance().combine2(classRoute, requestMapping.value()), routeInfoHolder);
            }
        }
    }

    /**
     * 加载拦截器配置
     *
     * @param obj AnnotatedElement
     * @return Set
     */
    private Set<Class<? extends RequestInterceptor>> loadInterceptor(AnnotatedElement obj) {
        // 这里根据class、method设定的拦截器来决定拦截栈
        Set<Class<? extends RequestInterceptor>> interceptorStack = new HashSet<>();

        // 加载intercept注解
        Intercept intercept = obj.getAnnotation(Intercept.class);
        loadInterceptor0(interceptorStack, intercept);

        // 加载clear注解
        return interceptorStack;
    }

    /**
     * 加载Intercept标签
     *
     * @param interceptorStack Set<Class<? extends RequestInterceptor>>
     * @param classIntercept   Intercept
     */
    private void loadInterceptor0(Set<Class<? extends RequestInterceptor>> interceptorStack, Intercept classIntercept) {
        if (classIntercept != null) {
            Class<? extends RequestInterceptor>[] classInterceptors = classIntercept.value();
            Collections.addAll(interceptorStack, classInterceptors);
        }
    }

    /**
     * 加载clear标签
     *
     * @param obj              AnnotatedElement
     * @param interceptorStack Set<Class<? extends RequestInterceptor>>
     */
    private void loadClearInterceptor(AnnotatedElement obj, Set<Class<? extends RequestInterceptor>> interceptorStack) {
        Clear classClear = obj.getAnnotation(Clear.class);
        if (classClear != null) {
            Class<? extends RequestInterceptor>[] classClears = classClear.value();

            for (Class<? extends RequestInterceptor> itemClear : classClears) {
                interceptorStack.remove(itemClear);
            }
        }
    }

    /**
     * 获取路由
     *
     * @param request HttpServletRequest
     * @return RouteMatchResult
     */
    public RouteMatchResult getRoute(HttpServletRequest request) {
        String requestPath = RequestUtil.getRealRequestURIWithoutPrefix(request);
        requestPath = PathUtil.getInstance().removeLastSlash(requestPath);

        // 路由变量
        Map<String, String> routeParams = new HashMap<String, String>();

        // 优先走匹配结果缓存
        if (routeMatchCache.containsKey(requestPath)) {
            String route = routeMatchCache.get(requestPath);
            // 路由匹配
            if (PathUtil.getInstance().matchPath(route, requestPath, routeParams)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("路由匹配走缓存，url:" + requestPath);
                }
                RouteMatchResult routeResult = new RouteMatchResult();
                routeResult.setMappingInfo(routeControls.get(route));
                routeResult.setRouteParams(routeParams);
                return routeResult;
            }
        }

        // 逐个查找
        Set<String> routes = routeControls.keySet();
        for (String route : routes) {
            if (StringUtils.isEmpty(route)) {
                continue;
            }

            routeParams.clear();
            // 路由匹配
            if (PathUtil.getInstance().matchPath(route, requestPath, routeParams)) {
                // 匹配结果计入缓存
                routeMatchCache.put(requestPath, route);

                RouteMatchResult routeResult = new RouteMatchResult();
                routeResult.setMappingInfo(routeControls.get(route));
                routeResult.setRouteParams(routeParams);
                return routeResult;
            }
        }

        return null;
    }
}
