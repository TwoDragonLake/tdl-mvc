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

package com.twodragonlake.mvc.exception;

/**
 * 业务异常.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2016/7/13 21:37
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -3992962807763740333L;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorInfo;

    public BusinessException() {
        super();
    }

    public BusinessException(String errorCode, String errorInfo) {
        super();
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    public BusinessException(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

}
