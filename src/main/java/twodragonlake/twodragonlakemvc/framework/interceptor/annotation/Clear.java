/**
 * @author Big Martin
 *
 */

package twodragonlake.twodragonlakemvc.framework.interceptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import twodragonlake.twodragonlakemvc.framework.interceptor.RequestInterceptor;

/**
 * 清除的拦截器列表，仅用于方法上清除类上的某几个拦截器
 * 
 * @author dingxiangyong 2016年8月12日 上午10:35:44
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Clear
{
	Class<? extends RequestInterceptor>[] value() default {};
}