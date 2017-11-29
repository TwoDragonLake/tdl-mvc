package com.twodragonlake.twodragonlakemvc.framework.domain;

import java.util.Map;

/**
 * 路由匹配结果
 * 
 * @category 路由匹配结果
 * @author xiangyong.ding@weimob.com
 * @since 2016年11月25日 下午10:56:19
 */
public class RouteMatchResult
{
	/**
	 * 路由匹配到的映射信息
	 */
	private RouteInfoHolder mappingInfo;

	/**
	 * 路由变量，如：路由, a/{b}/c，b--->'D'
	 */
	private Map<String, String> routeParams;

	public RouteInfoHolder getMappingInfo()
	{
		return mappingInfo;
	}

	public void setMappingInfo(RouteInfoHolder mappingInfo)
	{
		this.mappingInfo = mappingInfo;
	}

	public Map<String, String> getRouteParams()
	{
		return routeParams;
	}

	public void setRouteParams(Map<String, String> routeParams)
	{
		this.routeParams = routeParams;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("RouteResult [mappingInfo=");
		builder.append(mappingInfo);
		builder.append(", routeParams=");
		builder.append(routeParams);
		builder.append("]");
		return builder.toString();
	}

}
