/**
 * @filename Route.java
 * @time 2015.7.12
 * @author Big Martin
 * @comment 
 */
package com.twodragonlake.twodragonlakemvc.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.twodragonlake.twodragonlakemvc.framework.enums.RequestDataType;
import com.twodragonlake.twodragonlakemvc.framework.enums.ReturnType;

/**
 * @author Big Martin
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping
{
	/**
	 * The primary mapping expressed by this annotation.
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * The parameters of the mapped request, narrowing the primary mapping.
	 * 
	 * @return
	 */
	String[] params() default {};

	/**
	 * Controller return json or page. NOTE. this param method use only.
	 * 
	 * @return
	 */
	ReturnType returnType() default ReturnType.PAGE;

	/**
	 * 请求数据类型，JSON/FORMDATA/ALL
	 * 
	 * @return
	 */
	RequestDataType requestDataType() default RequestDataType.FORMDATA;
}
