package com.twodragonlake.twodragonlakemvc.framework.domain;

import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.twodragonlake.twodragonlakemvc.framework.enums.RequestDataType;
import com.twodragonlake.twodragonlakemvc.framework.enums.ReturnType;
import com.twodragonlake.twodragonlakemvc.framework.interceptor.RequestInterceptor;

/**
 * 路由信息承载器
 * 
 * @author Big Martin
 *
 */
public class RouteInfoHolder
{
	/**
	 * uri, like 'test.do'
	 */
	private String pattern;

	/**
	 * json or page.method use only
	 */
	private ReturnType returnType;

	/**
	 * formdata or json
	 */
	private RequestDataType requestDataType;

	private Method method;

	/**
	 * controller instance
	 */
	private Object instance;

	/**
	 * 接口拦截器栈
	 */
	private Set<RequestInterceptor> interceptorStack;

	public RouteInfoHolder(String pattern, RequestDataType requestDataType, ReturnType returnType, Method method,
			Object instance)
	{
		super();
		this.pattern = pattern;
		this.requestDataType = requestDataType;
		this.returnType = returnType;
		this.method = method;
		this.instance = instance;
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public ReturnType getReturnType()
	{
		return returnType;
	}

	public void setReturnType(ReturnType returnType)
	{
		this.returnType = returnType;
	}

	public Method getMethod()
	{
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
	}

	public Object getInstance()
	{
		return instance;
	}

	public void setInstance(Object instance)
	{
		this.instance = instance;
	}

	public RequestDataType getRequestDataType()
	{
		return requestDataType;
	}

	public void setRequestDataType(RequestDataType requestDataType)
	{
		this.requestDataType = requestDataType;
	}

	public Set<RequestInterceptor> getInterceptorStack()
	{
		return interceptorStack;
	}

	public void setInterceptorStack(Set<RequestInterceptor> interceptorStack)
	{
		this.interceptorStack = interceptorStack;
	}

	/**
	 * 执行前置拦截器方法
	 * 
	 * @return
	 */
	public boolean doInterceptPre(HttpServletRequest request, HttpServletResponse response)
	{

		return true;
	}

}
