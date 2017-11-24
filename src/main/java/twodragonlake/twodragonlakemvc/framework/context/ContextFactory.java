/**
 * @filename ContextFactory.java
 * @createtime 2015年7月22日
 * @author dingxiangyong
 * @comment 
 */
package twodragonlake.twodragonlakemvc.framework.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import twodragonlake.twodragonlakemvc.framework.DispatchServlet;
import twodragonlake.twodragonlakemvc.framework.util.StringUtil;

/**
 * @author Big Martin
 *
 */
public class ContextFactory
{
	/**
	 * application context
	 */
	private static ApplicationContext context;

	/**
	 * logger
	 */
	public static final Logger logger = Logger.getLogger(DispatchServlet.class);

	/**
	 * load configuration
	 * 
	 * @return
	 */
	public static ApplicationContext getApplicationContext(ServletContext servletContext)
	{
		return getApplicationContext(servletContext, null, true);
	}

	/**
	 * load configuration
	 * 
	 * @param contextFilePath
	 * @return
	 */
	public static ApplicationContext getApplicationContext(ServletContext servletContext, ServletConfig sc,
			boolean isNeedReload)
	{
		// check whether cached
		if (context != null && !isNeedReload)
		{
			return context;
		}

		if (StringUtil.isEmpty(sc.getInitParameter("scanPackage")))
		{
			throw new RuntimeException(
					"Failed to load context, please set scanPackage in init-param of DispatchServlet.");
		}
		if (StringUtil.isEmpty(sc.getInitParameter("viewPreffix")))
		{
			throw new RuntimeException(
					"Failed to load context, please set viewPreffix in init-param of DispatchServlet.");
		}
		if (StringUtil.isEmpty(sc.getInitParameter("viewSuffix")))
		{
			throw new RuntimeException(
					"Failed to load context, please set viewSuffix in init-param of DispatchServlet.");
		}

		context = new ApplicationContext();
		context.setScanPackage(sc.getInitParameter("scanPackage"));
		context.setViewPreffix(sc.getInitParameter("viewPreffix"));
		context.setViewSuffix(sc.getInitParameter("viewSuffix"));

		return context;
	}
}
