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
 * @filename XMLParser.java
 * @createtime 2015.7.21
 * @author dingxiangyong
 * @comment To parse xml file.
 */
package com.twodragonlake.mvc.util;

import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.ServletContextPropertyUtils;
import org.springframework.web.util.WebUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class XMLParser {

    /**
     * Parse xml file to user object directly.But xml file should follow this
     * rule: if object name is Obj, xml structure should be
     * <Objs> <Obj> <field1><field1>..<Obj><Objs>
     *
     * @param filePath xmlFile path
     * @param clazz    xml mapped clazz
     * @return object
     */
    public <T> T parseXml2Object(ServletContext servletContext, String filePath, Class<T> clazz) {
        return parseXml2Object(servletContext, filePath, clazz, new HashMap<>());
    }

    /**
     * Parse xml file to user object directly.But xml file should follow this
     * rule: if object name is Obj, xml structure should be
     * <Objs> <Obj> <field1><field1>..<Obj><Objs>
     *
     * @param filePath          xmlfile path
     * @param clazz             xml mapped clazz
     * @param nodeName2FieldMap xml node name to class field name map
     * @return object
     */
    public static <T> T parseXml2Object(ServletContext servletContext, String filePath, Class<T> clazz,
                                        Map<String, String> nodeName2FieldMap) {
        T result;

        try {
            // Resolve property placeholders before potentially resolving a real
            // path.
            String location = ServletContextPropertyUtils.resolvePlaceholders(filePath, servletContext);

            // Leave a URL (e.g. "classpath:" or "file:") as-is.
            if (!ResourceUtils.isUrl(location)) {
                // Consider a plain file path as relative to the web application
                // root directory.
                location = WebUtils.getRealPath(servletContext, location);
            }

            // Write log message to server log.
            servletContext.log("Initializing mvc from [" + location + "]");

            String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
            File file = ResourceUtils.getFile(resolvedLocation);
            if (!file.exists()) {
                throw new FileNotFoundException("Log4j config file [" + resolvedLocation + "] not found");
            }

            // get physic file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            String className = clazz.getSimpleName();

            // 1.get document root element
            Element root = document.getDocumentElement();

            // check root name
            if (!(className).equals(root.getNodeName()) && !nodeName2FieldMap.containsKey(root.getNodeName())) {
                return null;
            }

            // 2.get next level eles of root
            // instance object
            result = clazz.newInstance();

            for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // get nodeName set method
                    String nodeName = node.getNodeName();
                    String nodeValue = node.getFirstChild().getNodeValue();

                    // if nodeName and nodeValue are not empty
                    if (!StringUtil.isEmpty(nodeName) && !StringUtil.isEmpty(nodeValue)) {
                        if (nodeName2FieldMap.containsKey(nodeName)) {
                            nodeName = nodeName2FieldMap.get(nodeName);
                        }
                        String setMethodName = "set" + StringUtil.upperFirst(nodeName);
                        Method setMethod = clazz.getDeclaredMethod(setMethodName, String.class);
                        setMethod.setAccessible(true);

                        setMethod.invoke(result, nodeValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

}
