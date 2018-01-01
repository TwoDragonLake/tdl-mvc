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

import java.util.Map;

/**
 * 路由匹配结果.
 *
 * @author : dingxiangyong
 * @version : 1.0
 * @since : 2016/11/25 22:56
 */
public class RouteMatchResult {

    /**
     * 路由匹配到的映射信息
     */
    private RouteInfoHolder mappingInfo;

    /**
     * 路由变量，如：路由, a/{b}/c，b--->'D'
     */
    private Map<String, String> routeParams;

    public RouteInfoHolder getMappingInfo() {
        return mappingInfo;
    }

    public void setMappingInfo(RouteInfoHolder mappingInfo) {
        this.mappingInfo = mappingInfo;
    }

    public Map<String, String> getRouteParams() {
        return routeParams;
    }

    public void setRouteParams(Map<String, String> routeParams) {
        this.routeParams = routeParams;
    }

    @Override
    public String toString() {
        return "RouteMatchResult{" +
                "mappingInfo=" + mappingInfo +
                ", routeParams=" + routeParams +
                '}';
    }
}
