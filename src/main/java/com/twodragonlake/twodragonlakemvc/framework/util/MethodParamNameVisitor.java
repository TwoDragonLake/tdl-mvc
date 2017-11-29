package com.twodragonlake.twodragonlakemvc.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * asm的方法名字查看器(主内容参考自网络)
 * 
 * @author dixingxing
 * @date 2016年1月18日11:25:41
 */
public final class MethodParamNameVisitor
{

	/**
	 * 方法名字查看器结果缓存
	 */
	private static Map<String, String[]> methodParamNamesCache = new HashMap<String, String[]>();

	private MethodParamNameVisitor()
	{
	}

	/**
	 * 
	 * 比较参数类型是否一致
	 * 
	 * @param types asm的类型({@link Type})
	 * @param clazzes java 类型({@link Class})
	 * @return
	 */
	private static boolean sameType(Type[] types, Class<?>[] clazzes)
	{
		// 个数不同
		if (types.length != clazzes.length)
		{
			return false;
		}

		for (int i = 0; i < types.length; i++)
		{
			if (!Type.getType(clazzes[i]).equals(types[i]))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取方法参数
	 * 
	 * @category @author xiangyong.ding@weimob.com
	 * @since 2017年1月18日 下午3:56:17
	 * @param paramTypes
	 * @return
	 */
	private static String getMethodParamString(Class<?>[] paramTypes)
	{
		StringBuilder sb = new StringBuilder();

		for (Class<?> paramType : paramTypes)
		{
			sb.append(".");
			sb.append(paramType.getName());
		}
		return sb.toString();
	}

	/**
	 * 获取方法的参数名
	 * 
	 * @param m
	 * @return
	 */
	public static String[] getMethodParamNames(final Method m)
	{
		// 先取缓存中取
		String key = m.getDeclaringClass().getName() + "." + m.getName() + getMethodParamString(m.getParameterTypes());

		if (methodParamNamesCache.containsKey(key))
		{
			return methodParamNamesCache.get(key);
		}

		// 缓存中没有，则通过ASM取
		final String[] paramNames = new String[m.getParameterTypes().length];

		// get clazz name
		final Class<?> clazz = m.getDeclaringClass();

		// load class
		InputStream is = clazz.getResourceAsStream(ClassUtil.getClassFileName(clazz));

		if (is == null)
		{
			return null;
		}

		// get classreader
		ClassReader reader = null;

		try
		{
			reader = new ClassReader(is);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		reader.accept(new ClassVisitor(Opcodes.ASM4)
		{
			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String desc,
					final String signature, final String[] exceptions)
			{
				final Type[] args = Type.getArgumentTypes(desc);
				// 方法名相同并且参数个数相同
				if (!name.equals(m.getName()) || !sameType(args, m.getParameterTypes()))
				{
					return super.visitMethod(access, name, desc, signature, exceptions);
				}
				MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
				return new MethodVisitor(Opcodes.ASM4, v)
				{
					@Override
					public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
							int index)
					{
						int i = index - 1;
						// 如果是静态方法，则第一就是参数
						// 如果不是静态方法，则第一个是"this"，然后才是方法的参数
						if (Modifier.isStatic(m.getModifiers()))
						{
							i = index;
						}
						if (i >= 0 && i < paramNames.length)
						{
							paramNames[i] = name;
						}
						super.visitLocalVariable(name, desc, signature, start, end, index);
					}

				};
			}
		}, 0);
		methodParamNamesCache.put(key, paramNames);
		return paramNames;
	}

}
