/**
 * @filename ApplicationContext.java
 * @createtime 2015.7.21
 * @author Big Martin
 * @comment 
 */
package com.twodragonlake.twodragonlakemvc.framework.context;

/**
 * Store framework configuration and other basic information.
 * 
 * @author Big Martin
 *
 */
public class ApplicationContext
{
	/**
	 * user's controller package that need to scan.
	 */
	private String scanPackage;

	/**
	 * view preffix
	 */
	private String viewPreffix;

	/**
	 * view suffix
	 */
	private String viewSuffix;

	public String getScanPackage()
	{
		return scanPackage;
	}

	public void setScanPackage(String scanPackage)
	{
		this.scanPackage = scanPackage;
	}

	public String getViewPreffix()
	{
		return viewPreffix;
	}

	public void setViewPreffix(String viewPreffix)
	{
		this.viewPreffix = viewPreffix;
	}

	public String getViewSuffix()
	{
		return viewSuffix;
	}

	public void setViewSuffix(String viewSuffix)
	{
		this.viewSuffix = viewSuffix;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationContext [scanPackage=");
		builder.append(scanPackage);
		builder.append(", viewPreffix=");
		builder.append(viewPreffix);
		builder.append(", viewSuffix=");
		builder.append(viewSuffix);
		builder.append("]");
		return builder.toString();
	}

}
