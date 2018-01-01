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

import java.util.Collection;

/**
 * Array工具类.
 *
 * @author : Big Martin
 * @version : 1.0
 * @since : 2018/1/18 11:02
 */
public final class ArrayUtil {

    /**
     * determine whether array is empty : null or size of array is 0.
     *
     * @param array T[]
     * @return true:empty
     */
    public static <T> boolean isArrayEmpty(T[] array) {
        return array != null && array.length != 0;
    }

    /**
     * determine whether array is empty : null or size of array is 0.
     *
     * @param collection Collection<T>
     * @return true:empty
     */
    public static <T> boolean isContrainerEmpty(Collection<T> collection) {
        return collection != null && collection.size() != 0;
    }
}
