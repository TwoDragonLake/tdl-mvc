package twodragonlake.twodragonlakemvc.framework.util;

import org.springframework.util.StringUtils;

import twodragonlake.twodragonlakemvc.framework.exception.BusinessException;

/**
 * Assertion utility class that assists in validating arguments.
 * 
 * @author Big Martin
 *
 */
public class Assert
{
	/**
	 * 邮箱验证正则表达式
	 */
	private static final String EMAIL_REGEX = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	/**
	 * 手机号码正则表达式
	 */
	private static final String MOBILE_REGEX = "^1\\d{10}$";

	/**
	 * Assert that an object is not <code>null</code> .
	 * 
	 * <pre class="code">
	 * Assert.notNull(clazz, "The class must not be null");
	 * </pre>
	 * 
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is <code>null</code>
	 */
	public static void notNull(Object object, String message)
	{
		if (object == null)
		{
			throw new BusinessException(message);
		}

		if (object instanceof String && "".equals(object))
		{
			throw new BusinessException(message);
		}
	}

	/**
	 * Assert that an object is not <code>null</code> .
	 * 
	 * <pre class="code">
	 * Assert.notNull(clazz);
	 * </pre>
	 * 
	 * @param object the object to check
	 * @throws IllegalArgumentException if the object is <code>null</code>
	 */
	public static void notNull(Object object)
	{
		notNull(object, "[Assertion failed] - this argument is required; it must not be null");
	}

	/**
	 * Assert that strings are not email.
	 * 
	 * @param str
	 */
	public static void email(String str, String message)
	{
		if (StringUtils.isEmpty(str))
		{
			return;
		}

		if (!str.matches(EMAIL_REGEX))
		{
			throw new BusinessException(message);
		}
	}

	/**
	 * Assert that strings are not mobile.
	 * 
	 * @param str
	 */
	public static void mobile(String str, String message)
	{
		if (StringUtils.isEmpty(str))
		{
			return;
		}

		if (!str.matches(MOBILE_REGEX))
		{
			throw new BusinessException(message);
		}
	}
}
