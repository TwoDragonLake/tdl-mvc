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

package com.twodragonlake.mvc.util;

import com.twodragonlake.mvc.exception.BusinessException;
import org.springframework.util.StringUtils;

/**
 * Assertion utility class that assists in validating arguments.
 *
 * @author : Big Martin
 * @version : 1.0
 * @since : 2018/1/18 11:02
 */
public class Assert {

    /**
     * 邮箱验证正则表达式
     */
    private static final String EMAIL_REGEX = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 手机号码正则表达式
     */
    private static final String MOBILE_REGEX = "^1\\d{10}$";

    /**
     * Assert that an object is not <code>null</code> .
     * <p>
     * <pre class="code">
     * Assert.notNull(clazz, "The class must not be null");
     * </pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }

        if (object instanceof String && "".equals(object)) {
            throw new BusinessException(message);
        }
    }

    /**
     * Assert that an object is not <code>null</code> .
     * <p>
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     *
     * @param object the object to check
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    /**
     * Assert that strings are not email
     *
     * @param str     str
     * @param message message
     */
    public static void email(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            return;
        }

        if (!str.matches(EMAIL_REGEX)) {
            throw new BusinessException(message);
        }
    }

    /**
     * Assert that strings are not mobile.
     *
     * @param str     str
     * @param message message
     */
    public static void mobile(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            return;
        }

        if (!str.matches(MOBILE_REGEX)) {
            throw new BusinessException(message);
        }
    }
}
