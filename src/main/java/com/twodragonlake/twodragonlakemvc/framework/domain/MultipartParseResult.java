package com.twodragonlake.twodragonlakemvc.framework.domain;

import java.util.List;
import java.util.Map;

import com.twodragonlake.twodragonlakemvc.framework.multipart.CommonsMultipartFile;

/**
 * 多媒体文件解析结果
 * 
 * @category 多媒体文件解析结果
 * @author xiangyong.ding@weimob.com
 * @since 2016年11月25日 下午10:56:19
 */
public class MultipartParseResult
{
	/**
	 * 多个文件（这里用list的原因是支持多个文件同时上传）
	 */
	private Map<String, List<CommonsMultipartFile>> files;

	/**
	 * 参数
	 */
	private Map<String, String> fields;

	public Map<String, List<CommonsMultipartFile>> getFiles()
	{
		return files;
	}

	public void setFiles(Map<String, List<CommonsMultipartFile>> files)
	{
		this.files = files;
	}

	public Map<String, String> getFields()
	{
		return fields;
	}

	public void setFields(Map<String, String> fields)
	{
		this.fields = fields;
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer("MultipartParseResult{");
		sb.append("files=").append(files);
		sb.append(", fields=").append(fields);
		sb.append('}');
		return sb.toString();
	}
}
