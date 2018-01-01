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
 * @filename PathUtil.java
 * @createtime 2015.7.19
 * @author Big Martin
 * @comment referenced from spring AntPathMatcher
 */
package com.twodragonlake.mvc.util;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Path工具类.
 * referenced from spring.AntPathMatcher
 *
 * @author : Big Martin
 * @version : 1.0
 * @since : 2018/1/18 11:02
 */
public class PathUtil {

    /**
     * Default path separator: "/"
     */
    public static final String DEFAULT_PATH_SEPARATOR = "/";

    /**
     * 路径变量正则表达式
     */
    public static final String URL_VARIATE_REGEX = "^\\{([a-zA-Z0-9_])*}$";

    private static final PathUtil INSTANCE = new PathUtil();

    public static PathUtil getInstance() {
        return INSTANCE;
    }

    public boolean match(String pattern, String path) {
        return doMatch(pattern, path, true, null);
    }

    public boolean matchStart(String pattern, String path) {
        return doMatch(pattern, path, false, null);
    }

    /**
     * Actually match the given <code>path</code> against the given
     * <code>pattern</code>.
     *
     * @param pattern   the pattern to match against
     * @param path      the path String to test
     * @param fullMatch whether a full pattern match is required (else a pattern
     *                  match as far as the given base path goes is sufficient)
     * @return <code>true</code> if the supplied <code>path</code> matched,
     * <code>false</code> if it didn't
     */
    protected boolean doMatch(String pattern, String path, boolean fullMatch, Map<String, String> uriTemplateVariables) {

        String pathSeparator = DEFAULT_PATH_SEPARATOR;
        if (path.startsWith(pathSeparator) != pattern.startsWith(pathSeparator)) {
            return false;
        }

        String[] pattDirs = StringUtil.tokenizeToStringArray(pattern, pathSeparator);
        String[] pathDirs = StringUtil.tokenizeToStringArray(path, pathSeparator);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxStart];
            if ("**".equals(patDir)) {
                break;
            }
            if (matchStrings(patDir, pathDirs[pathIdxStart])) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            // Path is exhausted, only match if rest of pattern is * or **'s
            if (pattIdxStart > pattIdxEnd) {
                return (pattern.endsWith(pathSeparator) == path.endsWith(pathSeparator));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(pathSeparator)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        } else if (pattIdxStart > pattIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }

        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxEnd];
            if (patDir.equals("**")) {
                break;
            }
            if (matchStrings(patDir, pathDirs[pathIdxEnd])) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if (pattDirs[i].equals("**")) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (matchStrings(subPat, subStr)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!pattDirs[i].equals("**")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Combines two patterns into a new pattern that is returned.
     * <p>
     * This implementation simply concatenates the two patterns, unless the
     * first pattern contains a file extension match (such as {@code *.html}. In
     * that case, the second pattern should be included in the first, or an
     * {@code IllegalArgumentException} is thrown.
     * <p>
     * For example:
     * <table>
     * <tr>
     * <th>Pattern 1</th>
     * <th>Pattern 2</th>
     * <th>Result</th>
     * </tr>
     * <tr>
     * <td>/hotels</td>
     * <td>{@code
     * null}</td>
     * <td>/hotels</td>
     * </tr>
     * <tr>
     * <td>{@code null}</td>
     * <td>/hotels</td>
     * <td>/hotels</td>
     * </tr>
     * <tr>
     * <td>/hotels</td>
     * <td>/bookings</td>
     * <td>/hotels/bookings</td>
     * </tr>
     * <tr>
     * <td>/hotels</td>
     * <td>bookings</td>
     * <td>/hotels/bookings</td>
     * </tr>
     * <tr>
     * <td>/hotels/*</td>
     * <td>/bookings</td>
     * <td>/hotels/bookings</td>
     * </tr>
     * <tr>
     * <td>/hotels/&#42;&#42;</td>
     * <td>/bookings</td>
     * <td>/hotels/&#42;&#42;/bookings</td>
     * </tr>
     * <tr>
     * <td>/hotels</td>
     * <td>{hotel}</td>
     * <td>/hotels/{hotel}</td>
     * </tr>
     * <tr>
     * <td>/hotels/*</td>
     * <td>{hotel}</td>
     * <td>/hotels/{hotel}</td>
     * </tr>
     * <tr>
     * <td>/hotels/&#42;&#42;</td>
     * <td>{hotel}</td>
     * <td>/hotels/&#42;&#42;/{hotel}</td>
     * </tr>
     * <tr>
     * <td>/*.html</td>
     * <td>/hotels.html</td>
     * <td>/hotels.html</td>
     * </tr>
     * <tr>
     * <td>/*.html</td>
     * <td>/hotels</td>
     * <td>/hotels.html</td>
     * </tr>
     * <tr>
     * <td>/*.html</td>
     * <td>/*.txt</td>
     * <td>IllegalArgumentException</td>
     * </tr>
     * </table>
     *
     * @param pattern1 the first pattern
     * @param pattern2 the second pattern
     * @return the combination of the two patterns
     * @throws IllegalArgumentException when the two patterns cannot be combined
     */
    public String combine(String pattern1, String pattern2) {
        if (StringUtil.isEmpty(pattern1) && StringUtil.isEmpty(pattern2)) {
            return "";
        } else if (StringUtil.isEmpty(pattern1)) {
            return pattern2;
        } else if (StringUtil.isEmpty(pattern2)) {
            return pattern1;
        } else if (!pattern1.contains("{") && match(pattern1, pattern2)) {
            return pattern2;
        } else if (pattern1.endsWith("/*")) {
            if (pattern2.startsWith("/")) {
                // /hotels/* + /booking -> /hotels/booking
                return pattern1.substring(0, pattern1.length() - 1) + pattern2.substring(1);
            } else {
                // /hotels/* + booking -> /hotels/booking
                return pattern1.substring(0, pattern1.length() - 1) + pattern2;
            }
        } else if (pattern1.endsWith("/**")) {
            if (pattern2.startsWith("/")) {
                // /hotels/** + /booking -> /hotels/**/booking
                return pattern1 + pattern2;
            } else {
                // /hotels/** + booking -> /hotels/**/booking
                return pattern1 + "/" + pattern2;
            }
        } else {
            int dotPos1 = pattern1.indexOf('.');
            if (dotPos1 == -1) {
                // simply concatenate the two patterns
                if (pattern1.endsWith("/") || pattern2.startsWith("/")) {
                    return pattern1 + pattern2;
                } else {
                    return pattern1 + "/" + pattern2;
                }
            }
            String fileName1 = pattern1.substring(0, dotPos1);
            String extension1 = pattern1.substring(dotPos1);
            String fileName2;
            String extension2;
            int dotPos2 = pattern2.indexOf('.');
            if (dotPos2 != -1) {
                fileName2 = pattern2.substring(0, dotPos2);
                extension2 = pattern2.substring(dotPos2);
            } else {
                fileName2 = pattern2;
                extension2 = "";
            }
            String fileName = fileName1.endsWith("*") ? fileName2 : fileName1;
            String extension = extension1.startsWith("*") ? extension2 : extension1;

            return fileName + extension;
        }
    }

    public String combine2(String pattern1, String pattern2) {
        if (StringUtil.isEmpty(pattern1) && StringUtil.isEmpty(pattern2)) {
            return "";
        } else if (StringUtil.isEmpty(pattern1)) {
            return pattern2;
        } else if (StringUtil.isEmpty(pattern2)) {
            return pattern1;
        } else if (!pattern1.contains("{") && match(pattern1, pattern2)) {
            return pattern2;
        } else if (pattern1.endsWith("/*")) {
            if (pattern2.startsWith("/")) {
                // /hotels/* + /booking -> /hotels/booking
                return pattern1.substring(0, pattern1.length() - 1) + pattern2.substring(1);
            } else {
                // /hotels/* + booking -> /hotels/booking
                return pattern1.substring(0, pattern1.length() - 1) + pattern2;
            }
        } else {
            if (pattern2.startsWith("/")) {
                // /hotels/ + booking -> /hotels/*booking
                return pattern1 + pattern2.substring(0, pattern2.length() - 1);
            } else {
                // /hotels/ + booking -> /hotels/*booking
                return pattern1 + pattern2;
            }
        }
    }

    public boolean matchStrings(String pattern, String str) {
        if (pattern.contains("*")) {
            pattern = pattern.replace("*", ".*");
        }
        return !str.matches(pattern);
    }

    /**
     * 路径匹配，规则：1、*表示匹配所有；2、{XX}表示匹配参数，也匹配所有，但该参数需要返回给调用方
     * 匹配算法：将两个路径按照/进行分割，逐个按照规则匹配
     *
     * @param controllerPath 控制器路径
     * @param requestPath    请求路径
     * @return boolean
     * @author xiangyong.ding@weimob.com
     * @since 2016年11月25日 下午9:58:50
     */
    public boolean matchPath(String controllerPath, String requestPath, Map<String, String> routeParams) {
        // *处理
        if (controllerPath.contains("*")) {
            controllerPath = controllerPath.replace("*", ".*");
        }

        // 待匹配路径
        String[] toMatchPaths = requestPath.split(DEFAULT_PATH_SEPARATOR);
        String[] paths = controllerPath.split(DEFAULT_PATH_SEPARATOR);

        // 如果路径长度不相等，直接结束
        if (toMatchPaths.length != paths.length) {
            return false;
        }

        // Map<String, String> pathVars = new HashMap<String, String>();
        // 逐段匹配
        for (int i = 0; i < paths.length; i++) {
            // 如果为路由变量
            if (paths[i].matches(URL_VARIATE_REGEX)) {
                String routeVar = paths[i];
                routeParams.put(routeVar.substring(1, paths[i].length() - 1), toMatchPaths[i]);
                continue;
            }

            // 其他情况，按照正则表达式匹配
            if (!toMatchPaths[i].matches(paths[i])) {
                return false;
            }
        }

        return true;
    }

    public String removeLastSlash(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        if (str.endsWith("/")) {
            return str.substring(0, str.length() - 1);
        }

        return str;
    }

}
