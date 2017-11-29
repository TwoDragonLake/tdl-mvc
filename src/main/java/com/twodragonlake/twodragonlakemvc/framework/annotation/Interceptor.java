package com.twodragonlake.twodragonlakemvc.framework.annotation;

/**
 * 拦截器注解
 * 
 * @author dingxiangyong 2016年8月11日 下午5:33:09
 */
public @interface Interceptor
{
	/**
	 * 拦截路径
	 * 
	 * @return
	 */
	String path() default "";
}
