package twodragonlake.twodragonlakemvc.framework.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 上传文件，该类封装了方便的文件操作（该类来源于spring web 4.2）
 * 
 * @category @author xiangyong.ding@weimob.com
 * @since 2017年1月18日 上午11:01:00
 */
@SuppressWarnings("serial")
public class CommonsMultipartFile implements MultipartFile, Serializable
{

	protected static final Log logger = LogFactory.getLog(CommonsMultipartFile.class);

	private final FileItem fileItem;

	private final long size;

	/**
	 * Create an instance wrapping the given FileItem.
	 * 
	 * @param fileItem the FileItem to wrap
	 */
	public CommonsMultipartFile(FileItem fileItem)
	{
		this.fileItem = fileItem;
		this.size = this.fileItem.getSize();
	}

	/**
	 * Return the underlying {@code org.apache.commons.fileupload.FileItem}
	 * instance. There is hardly any need to access this.
	 */
	public final FileItem getFileItem()
	{
		return this.fileItem;
	}

	@Override
	public String getName()
	{
		return this.fileItem.getFieldName();
	}

	@Override
	public String getOriginalFileExtend()
	{
		String fileName = getOriginalFilename();
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	@Override
	public String getOriginalFilename()
	{
		String filename = this.fileItem.getName();
		if (filename == null)
		{
			// Should never happen.
			return "";
		}
		// Check for Unix-style path
		int pos = filename.lastIndexOf("/");
		if (pos == -1)
		{
			// Check for Windows-style path
			pos = filename.lastIndexOf("\\");
		}
		if (pos != -1)
		{
			// Any sort of path separator found...
			return filename.substring(pos + 1);
		}
		else
		{
			// A plain name
			return filename;
		}
	}

	@Override
	public String getContentType()
	{
		return this.fileItem.getContentType();
	}

	@Override
	public boolean isEmpty()
	{
		return (this.size == 0);
	}

	@Override
	public long getSize()
	{
		return this.size;
	}

	@Override
	public byte[] getBytes()
	{
		if (!isAvailable())
		{
			throw new IllegalStateException("File has been moved - cannot be read again");
		}
		byte[] bytes = this.fileItem.get();
		return (bytes != null ? bytes : new byte[0]);
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		if (!isAvailable())
		{
			throw new IllegalStateException("File has been moved - cannot be read again");
		}
		InputStream inputStream = this.fileItem.getInputStream();
		return (inputStream != null ? inputStream : new ByteArrayInputStream(new byte[0]));
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException
	{
		if (!isAvailable())
		{
			throw new IllegalStateException("File has already been moved - cannot be transferred again");
		}

		if (dest.exists() && !dest.delete())
		{
			throw new IOException(
					"Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
		}

		try
		{
			this.fileItem.write(dest);
			if (logger.isDebugEnabled())
			{
				String action = "transferred";
				if (!this.fileItem.isInMemory())
				{
					action = isAvailable() ? "copied" : "moved";
				}
				logger.debug("Multipart file '" + getName() + "' with original filename [" + getOriginalFilename()
						+ "], stored " + getStorageDescription() + ": " + action + " to [" + dest.getAbsolutePath()
						+ "]");
			}
		}
		catch (FileUploadException ex)
		{
			throw new IllegalStateException(ex.getMessage());
		}
		catch (IOException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			logger.error("Could not transfer to file", ex);
			throw new IOException("Could not transfer to file: " + ex.getMessage());
		}
	}

	/**
	 * Determine whether the multipart content is still available. If a
	 * temporary file has been moved, the content is no longer available.
	 */
	protected boolean isAvailable()
	{
		// If in memory, it's available.
		if (this.fileItem.isInMemory())
		{
			return true;
		}
		// Check actual existence of temporary file.
		if (this.fileItem instanceof DiskFileItem)
		{
			return ((DiskFileItem) this.fileItem).getStoreLocation().exists();
		}
		// Check whether current file size is different than original one.
		return (this.fileItem.getSize() == this.size);
	}

	/**
	 * Return a description for the storage location of the multipart content.
	 * Tries to be as specific as possible: mentions the file location in case
	 * of a temporary file.
	 */
	public String getStorageDescription()
	{
		if (this.fileItem.isInMemory())
		{
			return "in memory";
		}
		else if (this.fileItem instanceof DiskFileItem)
		{
			return "at [" + ((DiskFileItem) this.fileItem).getStoreLocation().getAbsolutePath() + "]";
		}
		else
		{
			return "on disk";
		}
	}

}
