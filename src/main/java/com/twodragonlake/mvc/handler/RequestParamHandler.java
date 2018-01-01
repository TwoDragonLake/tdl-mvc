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

package com.twodragonlake.mvc.handler;

import com.alibaba.fastjson.JSON;
import com.twodragonlake.mvc.domain.MultipartParseResult;
import com.twodragonlake.mvc.domain.ParamModel;
import com.twodragonlake.mvc.domain.RouteInfoHolder;
import com.twodragonlake.mvc.domain.RouteMatchResult;
import com.twodragonlake.mvc.enums.RequestDataType;
import com.twodragonlake.mvc.enums.RequestType;
import com.twodragonlake.mvc.exception.BeanInstantiationException;
import com.twodragonlake.mvc.multipart.CommonsMultipartFile;
import com.twodragonlake.mvc.multipart.MultipartFile;
import com.twodragonlake.mvc.util.ClassUtil;
import com.twodragonlake.mvc.util.MethodParamNameVisitor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求参数处理器.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2015/7/12 21:20
 */
public class RequestParamHandler {

    /**
     * all java declared object types
     */
    private static final List<String> OTHER_OBJECT_TYPE_LIST = new ArrayList<String>();

    /**
     * user's data to add to request for return
     */
    private static final String RESULT_TYPE = "java.util.Map";

    /**
     * http servlet request
     */
    private static final String REQUEST_TYPE = "javax.servlet.http.HttpServletRequest";

    /**
     * http servlet response
     */
    private static final String RESPONSE_TYPE = "javax.servlet.http.HttpServletResponse";

    /**
     * 多文件类型
     */
    private static final String MULTIPART_TYPE = "wang.moshu.smvc.framework.multipart.MultipartFile";

    private static Logger logger = LogManager.getLogger(RequestParamHandler.class);

    private static Map<Class<?>, Object> cachedInstance = new HashMap<Class<?>, Object>();

    // init java inner object type
    static {
        OTHER_OBJECT_TYPE_LIST.add(BasicType.STRING);
        OTHER_OBJECT_TYPE_LIST.add(BasicType.BOOLEAN);
        OTHER_OBJECT_TYPE_LIST.add(BasicType.BYTE);
        OTHER_OBJECT_TYPE_LIST.add(BasicType.DOUBLE);
        OTHER_OBJECT_TYPE_LIST.add(BasicType.INTEGER);
        OTHER_OBJECT_TYPE_LIST.add(BasicType.FLOAT);
        OTHER_OBJECT_TYPE_LIST.add(BasicType.LONG);
    }

    /**
     * Convenience method to instantiate a class using its no-arg constructor.
     *
     * @param clazz class to instantiate
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     */
    @SuppressWarnings("unchecked")
    public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
        // if cached , return
        if (cachedInstance.containsKey(clazz)) {
            return (T) cachedInstance.get(clazz);
        }

        // if not cached, then new it
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            T instance = clazz.newInstance();
            cachedInstance.put(clazz, instance);
            return instance;
        } catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
        }
    }

    /**
     * get all defined fields' of a class
     *
     * @param clazz Class
     * @return <T> Field[]
     * @throws BeanInstantiationException 异常
     */
    public static <T> Field[] getClassFieldNames(Class<T> clazz) throws BeanInstantiationException {
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }

        return clazz.getDeclaredFields();
    }

    /**
     * get method params for invoking
     *
     * @param routeResult RouteMatchResult
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param requestType RequestType
     * @return ParamModel
     */
    @SuppressWarnings("rawtypes")
    public static ParamModel getMethodParams(RouteMatchResult routeResult, HttpServletRequest request,
                                             HttpServletResponse response, RequestType requestType) {
        ParamModel paramModel = new ParamModel();
        RouteInfoHolder mappingInfo = routeResult.getMappingInfo();

        // get all param types
        Class<?>[] paramTypes = mappingInfo.getMethod().getParameterTypes();

        // 路由参数
        Map<String, String> routeParams = routeResult.getRouteParams();

        // 多文件参数
        MultipartParseResult multipartParseResult = null;
        if (isMultipart(request)) {
            multipartParseResult = parseMultipart(request);
        }

        String[] paramNames = MethodParamNameVisitor.getMethodParamNames(mappingInfo.getMethod());

        Object[] objs = new Object[paramTypes.length];
        int resultMapIndex = ParamModel.NO_RESULT_MAP;

        // iterate all types
        for (int i = 0; i < paramTypes.length; i++) {
            // if String or something basic object type
            if (OTHER_OBJECT_TYPE_LIST.contains(paramTypes[i].getName())) {
                // objs[i] = request.getParameter(paramNames[i]);
                if (paramNames != null) {
                    objs[i] = getRequestParam(request, paramNames[i], requestType, paramTypes[i].getName(), routeParams);
                }
            }

            // if map type , means results
            else if (RESULT_TYPE.equals(paramTypes[i].getName())) {
                objs[i] = new HashMap();
                resultMapIndex = i;
            }

            // if user wanna get http servlet request
            else if (REQUEST_TYPE.equals(paramTypes[i].getName())) {
                objs[i] = request;
            }

            // if user wanna get http servlet response
            else if (RESPONSE_TYPE.equals(paramTypes[i].getName())) {
                objs[i] = response;
            }

            // 如果匹配多文件上传
            else if (MULTIPART_TYPE.equals(
                    paramTypes[i].isArray() ? paramTypes[i].getComponentType().getName() : paramTypes[i].getName())) {
                if (multipartParseResult != null && paramNames != null && !CollectionUtils.isEmpty(multipartParseResult.getFiles())) {
                    // 如果是MultipartFile数组则全部返回，如果是但是文件则只返回第一个
                    objs[i] = paramTypes[i].isArray()
                            ? multipartParseResult.getFiles().get(paramNames[i]).toArray(new MultipartFile[0])
                            : multipartParseResult.getFiles().get(paramNames[i]).get(0);
                }
            }

            // other user defined type
            else {
                objs[i] = setUserModel(mappingInfo, paramTypes[i], request, requestType, multipartParseResult);
            }
        }

        // set paramModel
        paramModel.setParams(objs);
        paramModel.setResultMapIndex(resultMapIndex);
        return paramModel;
    }

    /**
     * 获取请求参数
     *
     * @param request       HttpServletRequest
     * @param paramName     参数名字
     * @param requestType   请求类型，get/post
     * @param paramTypeName 请求参数类型名字
     * @return Object
     */
    private static Object getRequestParam(HttpServletRequest request, String paramName, RequestType requestType,
                                          String paramTypeName) {
        return getRequestParam(request, paramName, requestType, paramTypeName, null);
    }

    /**
     * 获取请求参数
     *
     * @param request       HttpServletRequest
     * @param paramName     参数名字
     * @param requestType   请求类型，get/post
     * @param paramTypeName 请求参数类型名字
     * @param routeParams   路由参数
     * @return Object
     */
    private static Object getRequestParam(HttpServletRequest request, String paramName, RequestType requestType,
                                          String paramTypeName, Map<String, String> routeParams) {
        Object value = null;
        try {
            String originalValue;
            // 优先走路由参数
            if (routeParams != null && routeParams.containsKey(paramName)) {
                originalValue = routeParams.get(paramName);
            } else {
                originalValue = request.getParameter(paramName);
            }

            // 如果不存在参数，则直接返回
            if (StringUtils.isEmpty(originalValue)) {
                return null;
            }

            String valueStr;
            // 如果是GET方式，则取到参数重新编码（get中文只支持iso8859_1，好蛋疼）
            if (RequestType.GET.equals(requestType)) {
                valueStr = new String(originalValue.getBytes("iso8859_1"), "utf-8");
            }
            // post 默认utf-8编码
            else {
                valueStr = originalValue;
            }

            // url转码
            valueStr = URLDecoder.decode(valueStr, "utf-8");

            // 转换至具体类型
            if (BasicType.STRING.equals(paramTypeName)) {
                value = valueStr;
            } else if (BasicType.INTEGER.equals(paramTypeName)) {
                value = Integer.valueOf(valueStr);
            } else if (BasicType.FLOAT.equals(paramTypeName)) {
                value = Float.valueOf(valueStr);
            } else if (BasicType.DOUBLE.equals(paramTypeName)) {
                value = Double.valueOf(valueStr);
            } else if (BasicType.LONG.equals(paramTypeName)) {
                value = Long.valueOf(valueStr);
            } else if (BasicType.BOOLEAN.equals(paramTypeName)) {
                value = Boolean.valueOf(valueStr);
            } else if (BasicType.BYTE.equals(paramTypeName)) {
                value = Byte.valueOf(valueStr);
            }

            return value;

        } catch (UnsupportedEncodingException e1) {
            return null;
        }
    }

    /**
     * 读取RequestBody内容
     *
     * @param request HttpServletRequest
     * @return RequestBody内容
     */
    public static String readLine(HttpServletRequest request) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            InputStream in = request.getInputStream();
            byte[] buf = new byte[1024];
            for (; ; ) {
                int len = in.read(buf);
                if (len == -1) {
                    break;
                }
                if (len > 0) {
                    baos.write(buf, 0, len);
                }
            }
            byte[] bytes = baos.toByteArray();
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            return null;
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                // skip
            }
        }
    }

    private static Object setUserModel(RouteInfoHolder mappingInfo, Class<?> paramType, HttpServletRequest request,
                                       RequestType requestType, MultipartParseResult multipartParseResult) {
        // 解析json
        if (mappingInfo.getRequestDataType() == RequestDataType.JSON) {
            return setUserModelJson(paramType, request);
        }
        // 解析form(多文件必须用表单方式提交)
        else {
            return setUserModelForm(paramType, request, requestType, multipartParseResult);
        }
    }

    private static Object setUserModelJson(Class<?> paramType, HttpServletRequest request) {
        String line = null;
        // 先去get的值
        try {
            if (StringUtils.isNotEmpty(request.getQueryString())) {
                line = URLDecoder.decode(request.getQueryString(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            // do nothing
        }
        // 没有取到则取post参数值
        if (StringUtils.isEmpty(line)) {
            line = readLine(request);
        }

        // 如果没有传入参数，则默认使用空值
        if (null == line) {
            logger.info("请求值为null");
            try {
                return instantiate(paramType);
            } catch (BeanInstantiationException e) {
                logger.error("convert json failed! readLine = {" + null + "}");
                throw new RuntimeException(e);
            }
        }

        try {
            return JSON.parseObject(line, paramType);
        } catch (Exception e) {
            logger.error("convert json failed! readLine = {" + line + "}");
            throw new RuntimeException(e);
        }
    }

    private static Object setUserModelForm(Class<?> paramType, HttpServletRequest request, RequestType requestType,
                                           MultipartParseResult multipartParseResult) {
        try {
            Object userModel = instantiate(paramType);

            // instant object
            // Field[] fields = getClassFieldNames(paramType);
            Field[] fields = ClassUtil.getDeclaredFields(paramType, true);

            String fieldName;
            Object fieldValue;
            BeanWrapper wrapper = new BeanWrapperImpl(userModel);
            // iterate fields
            for (Field field : fields) {
                fieldName = field.getName();

                // 多文件传输的特殊性，因此取值还是取文件都从之前的多文件解析结果中取
                if (multipartParseResult != null) {
                    if (MULTIPART_TYPE.equals(field.getType().isArray() ? field.getType().getComponentType().getName()
                            : field.getType().getName())) {
                        // 如果是MultipartFile数组则全部返回，如果是但是文件则只返回第一个
                        fieldValue = CollectionUtils.isEmpty(multipartParseResult.getFiles()) ? null
                                : field.getType().isArray() ? multipartParseResult.getFiles().get(fieldName).toArray()
                                : multipartParseResult.getFiles().get(fieldName).get(0);
                    } else {
                        fieldValue = CollectionUtils.isEmpty(multipartParseResult.getFields()) ? null
                                : multipartParseResult.getFields().get(fieldName);
                    }
                }
                // 非文件上传模式
                else {
                    fieldValue = getRequestParam(request, fieldName, requestType, BasicType.STRING);
                }

                try {
                    // if (fieldValue != null)
                    // {
                    wrapper.setPropertyValue(fieldName, fieldValue);
                    // }
                } catch (Exception ex) {
                    String sb = "Can't set field [" + fieldName + "] of class[" + paramType.getName() +
                            "].";
                    logger.warn(sb, ex);
                }
            }

            return userModel;
        } catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error happened when setting fields of class [" + paramType.getName() + "].", e);
            }
        }

        return null;
    }

    /**
     * 是否是多文件请求
     *
     * @param request HttpServletRequest
     * @return boolean
     */
    private static boolean isMultipart(HttpServletRequest request) {
        return request != null && ServletFileUpload.isMultipartContent(request);
    }

    /**
     * 解析多文件请求
     *
     * @param request HttpServletRequest
     * @return MultipartParseResult
     */
    private static MultipartParseResult parseMultipart(HttpServletRequest request) {
        MultipartParseResult result = new MultipartParseResult();
        // 待文件
        Map<String, List<CommonsMultipartFile>> files = null;
        // 其他非文件参数,表单域集合
        Map<String, String> fields = null;

        // servlet文件上传操作类
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

        try {
            // 可以上传多个文件
            List<FileItem> list = upload.parseRequest(request);

            // 校验文件格式
            for (FileItem item : list) {
                // 如果为input输入文本
                if (item.isFormField()) {
                    // lazy初始化
                    if (fields == null) {
                        fields = new HashMap<>();
                    }
                    // 插入至表单域集合，待后面用
                    fields.put(item.getFieldName(), item.getString("utf-8"));// 页面编码需要保持utf-8
                    continue; // 结束
                }

                // lazy初始化
                if (files == null) {
                    files = new HashMap<>();
                }

                // 如果对该KEY未添加过则初始化容器
                files.computeIfAbsent(item.getFieldName(), k -> new ArrayList<>());
                // 文件解析好放入结果集中
                files.get(item.getFieldName()).add(new CommonsMultipartFile(item));

            }
            result.setFiles(files);
            result.setFields(fields);

        } catch (FileUploadException e) {
            logger.error("文件上传发生异常FileUploadException", e);
        } catch (Exception e) {
            logger.error("文件上传发生异常Exception", e);
        }

        return result;
    }

    /**
     * 基本类型
     */
    interface BasicType {
        String STRING = "java.lang.String";
        String BOOLEAN = "java.lang.Boolean";
        String BYTE = "java.lang.Byte";
        String DOUBLE = "java.lang.Double";
        String INTEGER = "java.lang.Integer";
        String FLOAT = "java.lang.Float";
        String LONG = "java.lang.Long";
    }
}
