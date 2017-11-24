/**
 * @filename RouteHandler.java
 * @createtime 2015.7.12
 * @author Big Martin
 * @comment 
 */
package twodragonlake.twodragonlakemvc.framework.handler;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import twodragonlake.twodragonlakemvc.framework.annotation.RequestMapping;
import twodragonlake.twodragonlakemvc.framework.domain.RouteInfoHolder;
import twodragonlake.twodragonlakemvc.framework.domain.RouteMatchResult;
import twodragonlake.twodragonlakemvc.framework.interceptor.RequestInterceptor;
import twodragonlake.twodragonlakemvc.framework.interceptor.annotation.Clear;
import twodragonlake.twodragonlakemvc.framework.interceptor.annotation.Intercept;
import twodragonlake.twodragonlakemvc.framework.util.AnnotationUtil;
import twodragonlake.twodragonlakemvc.framework.util.Assert;
import twodragonlake.twodragonlakemvc.framework.util.PathUtil;
import twodragonlake.twodragonlakemvc.framework.util.RequestUtil;
import twodragonlake.twodragonlakemvc.framework.util.SpringBeanUtils;
import twodragonlake.twodragonlakemvc.framework.util.StringUtil;

/**
 * @author Big Martin
 *
 */
public class RouteHandler
{
	// singleton instance
	private static final RouteHandler INSTANCE = new RouteHandler();

	/**
	 * 路由控制器集合
	 */
	private Map<String, RouteInfoHolder> routeControls = new HashMap<String, RouteInfoHolder>();

	/**
	 * 路由匹配缓存（一次匹配成功后，计入该cache中，避免多次匹配）
	 */
	private Map<String, String> routeMatchCache = new HashMap<String, String>();

	private Log logger = LogFactory.getLog(RouteHandler.class);

	private RouteHandler()
	{

	}

	/**
	 * get instance
	 * 
	 * @return
	 */
	public static RouteHandler getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Regist a handler to Smvc
	 * 
	 * @param clazz
	 */
	public void registRouteByClass(Class<?> clazz)
	{
		// find @controller annotation
		Controller controller = AnnotationUtil.findAnnotation(clazz, Controller.class);

		// if it's a controller
		if (controller != null)
		{
			// 查找RequestMapping注解
			RequestMapping requestMapping = AnnotationUtil.findAnnotation(clazz, RequestMapping.class);

			// 根据各个method的注解注册路由
			registRouteByMethod(StringUtil.standardUrlPattern(requestMapping.value()), clazz);
		}
	}

	/**
	 * 根据方法注册路由
	 * 
	 * @category 根据方法注册路由
	 * @author xiangyong.ding@weimob.com
	 * @since 2016年11月30日 下午11:49:51
	 * @param classRoute
	 * @param clazz
	 */
	public void registRouteByMethod(String classRoute, Class<?> clazz)
	{
		Assert.notNull(clazz, "Class cannot be null.");

		// controller instance
		String beanSpringName = StringUtils.isEmpty(clazz.getAnnotation(Controller.class).value())
				? StringUtil.lowerFirst(clazz.getSimpleName()) : clazz.getAnnotation(Controller.class).value();
		Object instance = SpringBeanUtils.getBean(beanSpringName);

		Assert.notNull(instance, "Faild to get controller instance, class:" + clazz);

		// 先load控制器上的拦截器
		Set<Class<? extends RequestInterceptor>> classInterceptors = loadInterceptor(clazz);

		// 控制器扫描结果
		Map<Method, RouteInfoHolder> results = new HashMap<Method, RouteInfoHolder>();

		// 扫描所有方法，加载控制器和拦截器
		Method[] methods = clazz.getMethods();
		for (Method method : methods)
		{
			// 加载方法上接口配置
			// find @requestmaping
			RequestMapping requestMapping = AnnotationUtil.findAnnotation(method, RequestMapping.class);

			RouteInfoHolder routeInfoHolder;
			if (requestMapping != null)
			{
				// create MappingInfo
				routeInfoHolder = new RouteInfoHolder(requestMapping.value(), requestMapping.requestDataType(),
						requestMapping.returnType(), method, instance);

				// 加载方法上拦截器配置
				Set<Class<? extends RequestInterceptor>> methodInterceptors = loadInterceptor(method);
				methodInterceptors.addAll(classInterceptors);

				// 加载clear注解
				loadClearInterceptor(method, methodInterceptors);

				// 从spring获取拦截器实例
				Set<RequestInterceptor> interceptorInstances = new HashSet<RequestInterceptor>();
				for (Class<? extends RequestInterceptor> itemInterceptorClazz : methodInterceptors)
				{
					Object itemInterceptorIns = SpringBeanUtils
							.getBean(StringUtil.lowerFirst(itemInterceptorClazz.getSimpleName()));

					// 如果获取实例失败，则丢弃
					if (itemInterceptorIns == null)
					{
						continue;
					}

					interceptorInstances.add((RequestInterceptor) itemInterceptorIns);
				}

				// 设定接口拦截器栈
				routeInfoHolder.setInterceptorStack(interceptorInstances);
				results.put(method, routeInfoHolder);

				// 加入控制器路由集合
				routeControls.put(PathUtil.getInstance().combine2(classRoute, requestMapping.value()), routeInfoHolder);
			}
		}
	}

	/**
	 * 加载拦截器配置
	 * 
	 * @param obj
	 * @return
	 */
	private Set<Class<? extends RequestInterceptor>> loadInterceptor(AnnotatedElement obj)
	{
		// 这里根据class、method设定的拦截器来决定拦截栈
		Set<Class<? extends RequestInterceptor>> interceptorStack = new HashSet<Class<? extends RequestInterceptor>>();

		// 加载intercept注解
		Intercept intercept = obj.getAnnotation(Intercept.class);
		loadInterceptor0(interceptorStack, intercept);

		// 加载clear注解
		return interceptorStack;
	}

	/**
	 * 加载Intercept标签
	 * 
	 * @param interceptorStack
	 * @param classIntercept
	 */
	private void loadInterceptor0(Set<Class<? extends RequestInterceptor>> interceptorStack, Intercept classIntercept)
	{
		if (classIntercept != null)
		{
			Class<? extends RequestInterceptor>[] classIntercrptors = classIntercept.value();

			if (classIntercrptors != null)
			{
				Collections.addAll(interceptorStack, classIntercrptors);
			}
		}
	}

	/**
	 * 加载clear标签
	 * 
	 * @param interceptorStack
	 * @param classClear
	 */
	private void loadClearInterceptor(AnnotatedElement obj, Set<Class<? extends RequestInterceptor>> interceptorStack)
	{
		Clear classClear = obj.getAnnotation(Clear.class);
		if (classClear != null)
		{
			Class<? extends RequestInterceptor>[] classClears = classClear.value();

			for (Class<? extends RequestInterceptor> itemClear : classClears)
			{
				interceptorStack.remove(itemClear);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param classMappingInfo
	 * @return
	 */
	public RouteMatchResult getRoute(HttpServletRequest request)
	{
		String requestPath = RequestUtil.getRealRequestURIWithoutPrefix(request);
		requestPath = PathUtil.getInstance().removeLastSlash(requestPath);

		// 路由变量
		Map<String, String> routeParams = new HashMap<String, String>();

		// 优先走匹配结果缓存
		if (routeMatchCache.containsKey(requestPath))
		{
			String route = routeMatchCache.get(requestPath);
			// 路由匹配
			if (PathUtil.getInstance().matchPath(route, requestPath, routeParams))
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("路由匹配走缓存，url:" + requestPath);
				}
				RouteMatchResult routeResult = new RouteMatchResult();
				routeResult.setMappingInfo(routeControls.get(route));
				routeResult.setRouteParams(routeParams);
				return routeResult;
			}
		}

		// 逐个查找
		Set<String> routes = routeControls.keySet();
		for (String route : routes)
		{
			if (StringUtils.isEmpty(route))
			{
				continue;
			}

			routeParams.clear();
			// 路由匹配
			if (PathUtil.getInstance().matchPath(route, requestPath, routeParams))
			{
				// 匹配结果计入缓存
				routeMatchCache.put(requestPath, route);

				RouteMatchResult routeResult = new RouteMatchResult();
				routeResult.setMappingInfo(routeControls.get(route));
				routeResult.setRouteParams(routeParams);
				return routeResult;
			}
		}

		return null;
	}
}
