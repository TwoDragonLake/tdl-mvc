package twodragonlake.twodragonlakemvc.framework.domain;

import java.io.Serializable;

/**
 * 返回json时默认的返回体包装类
 * 
 * @author dingxiangyong 2016年7月13日 下午8:45:10
 */
public class JsonResponse implements Serializable
{
	// 失败返回码
	public static final String UNKNOWN_ERRORCODE = "000001";
	
	// 未登录返回码
	public static final String UN_LOGIN_ERRORCODE = "000002";

	// 404返回码
	public static final String PAGE_NOT_FOUND_ERRORCODE = "000404";

	// 500返回码
	public static final String PAGE_ERROR_FOUND_ERRORCODE = "000500";

	/**
	 * 
	 */
	protected static final long serialVersionUID = 5325521982124983937L;

	/**
	 * 校验码，0：正常，1：异常
	 */
	private int code;

	/**
	 * 返回数据
	 */
	private Object data;

	/**
	 * 返回码
	 */
	private String returnCode;

	/**
	 * 返回消息
	 */
	private String message;

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getReturnCode()
	{
		return returnCode;
	}

	public void setReturnCode(String returnCode)
	{
		this.returnCode = returnCode;
	}

}
