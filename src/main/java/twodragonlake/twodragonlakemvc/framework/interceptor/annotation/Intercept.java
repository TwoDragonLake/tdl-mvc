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
 * 拦截列表，作用于类或者方法
 * 
 * @author dingxiangyong 2016年8月12日 上午10:36:56
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Intercept
{
	Class<? extends RequestInterceptor>[] value();
}
