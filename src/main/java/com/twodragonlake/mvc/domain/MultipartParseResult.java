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

import com.twodragonlake.mvc.multipart.CommonsMultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 多媒体文件解析结果.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2016/11/25 22:56
 */
public class MultipartParseResult {

    /**
     * 多个文件（这里用list的原因是支持多个文件同时上传）
     */
    private Map<String, List<CommonsMultipartFile>> files;

    /**
     * 参数
     */
    private Map<String, String> fields;

    public Map<String, List<CommonsMultipartFile>> getFiles() {
        return files;
    }

    public void setFiles(Map<String, List<CommonsMultipartFile>> files) {
        this.files = files;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "MultipartParseResult{" + "files=" + files +
                ", fields=" + fields +
                '}';
    }
}
