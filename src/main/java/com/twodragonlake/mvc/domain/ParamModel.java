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
 * It is used to maintain parameter info when invoking user's controller.
 */
package com.twodragonlake.mvc.domain;

/**
 * 多媒体文件解析结果.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2016/11/25 22:56
 */
public class ParamModel {

    /**
     * no result to return.
     */
    public static final int NO_RESULT_MAP = -1;

    /**
     * parameters
     */
    private Object[] params;

    /**
     * the result key-value result map index.
     */
    private int resultMapIndex = NO_RESULT_MAP;

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public int getResultMapIndex() {
        return resultMapIndex;
    }

    public void setResultMapIndex(int resultMapIndex) {
        this.resultMapIndex = resultMapIndex;
    }


}
