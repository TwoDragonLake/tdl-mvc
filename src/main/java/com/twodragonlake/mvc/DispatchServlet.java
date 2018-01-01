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

package com.twodragonlake.mvc;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.twodragonlake.mvc.context.ApplicationContext;
import com.twodragonlake.mvc.context.ContextFactory;
import com.twodragonlake.mvc.domain.JsonResponse;
import com.twodragonlake.mvc.domain.ParamModel;
import com.twodragonlake.mvc.domain.RouteInfoHolder;
import com.twodragonlake.mvc.domain.RouteMatchResult;
import com.twodragonlake.mvc.enums.RequestType;
import com.twodragonlake.mvc.enums.ReturnType;
import com.twodragonlake.mvc.exception.BusinessException;
import com.twodragonlake.mvc.handler.RequestParamHandler;
import com.twodragonlake.mvc.handler.RouteHandler;
import com.twodragonlake.mvc.interceptor.RequestInterceptor;
import com.twodragonlake.mvc.util.ClassUtil;
import com.twodragonlake.mvc.util.RequestUtil;
import com.twodragonlake.mvc.util.ResponseUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Servlet implementation class DispatchServlet
 */
public class DispatchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * view prefix
     */
    private static String prefix;

    /**
     * view suffix
     */
    private static String suffix;

    /**
     * mvc application context
     */
    private static ApplicationContext context;

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(DispatchServlet.class);

    /**
     * 设置response属性
     */
    private static Map<String, String> header = new HashMap<String, String>();

    public DispatchServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            ServletConfig sc = getServletConfig();

            // init context
            context = ContextFactory.getApplicationContext(getServletContext(), sc, true);
            if (context == null) {
                logger.error("Failed to load Application context.Failed to startup.");
                return;
            }

            // init view config
            prefix = context.getViewPrefix();
            suffix = context.getViewSuffix();

            // init all user controller
            // get all controller class
            Set<Class<?>> controllerClasses = ClassUtil.getClasses(context.getScanPackage());

            // check whether config scan path
            if (controllerClasses == null || controllerClasses.isEmpty()) {
                logger.warn("Got no classes in scan path!");
            }

            if (controllerClasses != null) {
                for (Class<?> clazz : controllerClasses) {
                    RouteHandler.getInstance().registerRouteByClass(clazz);
                }
            }
            header.put("Cache-Control", "no-cache");
            header.put("content-type", "application/json;charset=UTF-8");
        } catch (Exception ex) {
            logger.error("MVC 初始化失败");
            ex.printStackTrace();
            throw new RuntimeException();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doService(request, response, RequestType.GET);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doService(request, response, RequestType.POST);
    }

    private void doService(HttpServletRequest request, HttpServletResponse response, RequestType requestType)
            throws IOException, ServletException {
        // 设定 UTF-8
        request.setCharacterEncoding("UTF-8");

        String requestUrl = RequestUtil.getRealRequestURI(request);

        // 查找接口配置信息
        RouteMatchResult routeResult = RouteHandler.getInstance().getRoute(request);
        if (routeResult == null || routeResult.getMappingInfo().getMethod() == null) {
            Logger.getLogger(this.getClass()).error("request uri [" + requestUrl + "] did not match any controller.");
            // response.sendError(404);
            JsonResponse returnObj = new JsonResponse();
            returnObj.setReturnCode(JsonResponse.PAGE_NOT_FOUND_ERROR_CODE);// 返回404
            returnObj.setData("404");// 返回404页面
            handleResult(request, response, null, returnObj);

            return;
        }

        // 获取接口对应的方法，准备调用接口方法
        Method targetMethod = routeResult.getMappingInfo().getMethod();

        // 拦截器信息
        Set<RequestInterceptor> interceptorStack = routeResult.getMappingInfo().getInterceptorStack();

        // 返回值
        JsonResponse returnObj = new JsonResponse();
        try {
            // 先调用方法拦截器前置方法
            for (RequestInterceptor interceptor : interceptorStack) {
                // 调用前置拦截器，如果返回false，直接结束
                if (!interceptor.preHandle(request, response)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("请求被拦截器[" + interceptor.description() + "]拦截并直接结束");
                    }
                    return;
                }
            }

            ParamModel paramModel = RequestParamHandler.getMethodParams(routeResult, request, response, requestType);
            Object[] args = paramModel.getParams();
            Object result = targetMethod.invoke(routeResult.getMappingInfo().getInstance(), args);

            // 调用方法拦截器后置方法
            for (RequestInterceptor interceptor : interceptorStack) {
                // 调用后置拦截器
                interceptor.postHandle(request, response, result);
            }

            // 设定输出到页面的数据
            if (paramModel.getResultMapIndex() != ParamModel.NO_RESULT_MAP) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> resultMap = (Map<Object, Object>) args[paramModel.getResultMapIndex()];
                    Set<Object> resultIterator = resultMap.keySet();
                    for (Object key : resultIterator) {
                        request.setAttribute(key.toString(), resultMap.get(key));
                    }
                } catch (ClassCastException ex) {
                    logger.warn("Can't cast user resultmap to Map<Object, Object>.Please Check method["
                            + targetMethod.toGenericString() + "].");
                }
            }

            // 如果用户返回的类型为 JsonResponse类型或其继承类，则直接返回返回对象，不需要再次封装
            if (result instanceof JsonResponse) {
                returnObj = (JsonResponse) result;
                returnObj.setCode(0);
                returnObj.setMessage(
                        StringUtils.isEmpty(returnObj.getMessage()) ? StringUtils.EMPTY : returnObj.getMessage());
                returnObj.setReturnCode(
                        StringUtils.isEmpty(returnObj.getReturnCode()) ? StringUtils.EMPTY : returnObj.getReturnCode());
            } else {
                returnObj.setCode(0);
                returnObj.setData(result);
                returnObj.setMessage(StringUtils.EMPTY);
                returnObj.setReturnCode(StringUtils.EMPTY);
            }
            // 异常处理
        } catch (Exception e) {
            logger.error("", e);
            String resultCode = StringUtils.EMPTY;
            String appendMessage;
            if (e instanceof InvocationTargetException) {
                InvocationTargetException exception = (InvocationTargetException) e;
                Throwable t = exception.getCause();
                if (t instanceof BusinessException) {
                    BusinessException businessException = (BusinessException) t;
                    resultCode = businessException.getErrorCode();
                    appendMessage = businessException.getErrorInfo();
                } else {
                    appendMessage = ((InvocationTargetException) e).getTargetException().getMessage();
                }
            } else {
                if (e instanceof BusinessException) {
                    BusinessException businessException = (BusinessException) e;
                    resultCode = businessException.getErrorCode();
                    appendMessage = businessException.getErrorInfo();
                } else {
                    appendMessage = e.getMessage();
                }
            }

            if (StringUtils.isEmpty(resultCode)) {
                resultCode = JsonResponse.UNKNOWN_ERROR_CODE;
            }

            returnObj.setCode(1);// 异常返回
            returnObj.setData("");
            returnObj.setMessage(appendMessage);
            returnObj.setReturnCode(resultCode);
        } finally {
            // 调用方法拦截器post方法（每次必定执行）
            for (RequestInterceptor interceptor : interceptorStack) {
                // 调用前置拦截器，如果返回false，直接结束
                interceptor.commitHandle(request, response);
            }
        }

        handleResult(request, response, routeResult.getMappingInfo(), returnObj);
    }

    private void handleResult(HttpServletRequest request, HttpServletResponse response, RouteInfoHolder mappingInfo,
                              JsonResponse returnObj) throws ServletException, IOException {
        // 结果处理
        // 啥都不需要返回的情况
        if (mappingInfo != null && mappingInfo.getReturnType().equals(ReturnType.NOTHING)) {
            return;
        }

        // 404情况
        if (returnObj.getReturnCode().equals(JsonResponse.PAGE_NOT_FOUND_ERROR_CODE)) {
            return404(request, response);
            return;
        }

        // 页面接口发生错误，返回500
        if (mappingInfo != null && returnObj.getCode() == 1 && mappingInfo.getReturnType().equals(ReturnType.PAGE)) {
            return500(request, response);
            return;
        }

        // json处理
        if (mappingInfo != null && (mappingInfo.getReturnType().equals(ReturnType.JSON)
                || (mappingInfo.getReturnType().equals(ReturnType.PAGE)
                && StringUtils.isNotEmpty(returnObj.getReturnCode())))) {
            SerializeWriter out = new SerializeWriter();
            JSONSerializer serializer = new JSONSerializer(out);

            // 测试暂用
            serializer.config(SerializerFeature.QuoteFieldNames, true);
            serializer.setDateFormat("yyyy-MM-dd HH:mm:ss");
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            serializer.config(SerializerFeature.PrettyFormat, true);
            serializer.config(SerializerFeature.WriteMapNullValue, true);
            serializer.config(SerializerFeature.WriteNullNumberAsZero, true);
            serializer.config(SerializerFeature.WriteNullStringAsEmpty, true);
            serializer.config(SerializerFeature.WriteNullListAsEmpty, true);

            // 结果序列化json
            serializer.write(returnObj);
            String json = out.toString();
            out.close();

            // 输出结果
            ResponseUtil.write(response, header, "UTF-8", new String[]{json});

            return;
        } else {
            // 走页面返回（404走页面）
            if (returnObj.getData() instanceof String
                    && !returnObj.getReturnCode().equals(JsonResponse.PAGE_NOT_FOUND_ERROR_CODE)) {
                String resultStr = (String) returnObj.getData();
                String resultPage = prefix + resultStr + suffix;
                // dispatch request
                request.getRequestDispatcher(resultPage).forward(request, response);
                return;
            }
        }

        return404(request, response);
    }

    private void return404(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void return500(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
