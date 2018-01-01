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
 * @filename ApplicationContext.java
 * @createtime 2015.7.21
 * @author Big Martin
 * @comment
 */
package com.twodragonlake.mvc.context;

/**
 * Store framework configuration and other basic information.
 *
 * @author Big Martin
 */
public class ApplicationContext {

    /**
     * user's controller package that need to scan.
     */
    private String scanPackage;

    /**
     * view prefix
     */
    private String viewPrefix;

    /**
     * view suffix
     */
    private String viewSuffix;

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public String getViewPrefix() {
        return viewPrefix;
    }

    public void setViewPrefix(String viewPrefix) {
        this.viewPrefix = viewPrefix;
    }

    public String getViewSuffix() {
        return viewSuffix;
    }

    public void setViewSuffix(String viewSuffix) {
        this.viewSuffix = viewSuffix;
    }

    @Override
    public String toString() {
        return "ApplicationContext [scanPackage=" +
                scanPackage +
                ", viewPrefix=" +
                viewPrefix +
                ", viewSuffix=" +
                viewSuffix +
                "]";
    }

}
