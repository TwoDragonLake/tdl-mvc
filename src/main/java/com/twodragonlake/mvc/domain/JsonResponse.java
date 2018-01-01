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

import java.io.Serializable;

/**
 * Json返回包装类.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2016/7/13 20:45
 */
public class JsonResponse implements Serializable {

    protected static final long serialVersionUID = 5325521982124983937L;

    // 失败返回码
    public static final String UNKNOWN_ERROR_CODE = "000001";

    // 未登录返回码
    public static final String UN_LOGIN_ERROR_CODE = "000002";

    // 404返回码
    public static final String PAGE_NOT_FOUND_ERROR_CODE = "000404";

    // 500返回码
    public static final String PAGE_ERROR_FOUND_ERROR_CODE = "000500";

    /**
     * 校验码，0：正常，1：异常
     */
    private int code;

    /**
     * 返回数据
     */
    private Object data;

    /**
     * 返回码
     */
    private String returnCode;

    /**
     * 返回消息
     */
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

}
