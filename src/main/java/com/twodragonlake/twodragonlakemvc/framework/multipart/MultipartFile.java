package com.twodragonlake.twodragonlakemvc.framework.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 上传文件，该类封装了方便的文件操作（该类来源于spring web 4.2）
 * 
 * @category @author xiangyong.ding@weimob.com
 * @since 2017年1月18日 上午11:02:08
 */
public interface MultipartFile
{

	/**
	 * Return the name of the parameter in the multipart form.
	 * 
	 * @return the name of the parameter (never {@code null} or empty)
	 */
	String getName();

	/**
	 * Return the original filename in the client's filesystem.
	 * <p>
	 * This may contain path information depending on the browser used, but it
	 * typically will not with any other than Opera.
	 * 
	 * @return the original filename, or the empty String if no file has been
	 *         chosen in the multipart form, or {@code null} if not defined or
	 *         not available
	 */
	String getOriginalFilename();

	/**
	 * get file extend
	 * 
	 * @category @author xiangyong.ding@weimob.com
	 * @since 2017-1-19 02:13:26
	 * @return
	 */
	String getOriginalFileExtend();

	/**
	 * Return the content type of the file.
	 * 
	 * @return the content type, or {@code null} if not defined (or no file has
	 *         been chosen in the multipart form)
	 */
	String getContentType();

	/**
	 * Return whether the uploaded file is empty, that is, either no file has
	 * been chosen in the multipart form or the chosen file has no content.
	 */
	boolean isEmpty();

	/**
	 * Return the size of the file in bytes.
	 * 
	 * @return the size of the file, or 0 if empty
	 */
	long getSize();

	/**
	 * Return the contents of the file as an array of bytes.
	 * 
	 * @return the contents of the file as bytes, or an empty byte array if
	 *         empty
	 * @throws IOException in case of access errors (if the temporary store
	 *             fails)
	 */
	byte[] getBytes() throws IOException;

	/**
	 * Return an InputStream to read the contents of the file from. The user is
	 * responsible for closing the stream.
	 * 
	 * @return the contents of the file as stream, or an empty stream if empty
	 * @throws IOException in case of access errors (if the temporary store
	 *             fails)
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Transfer the received file to the given destination file.
	 * <p>
	 * This may either move the file in the filesystem, copy the file in the
	 * filesystem, or save memory-held contents to the destination file. If the
	 * destination file already exists, it will be deleted first.
	 * <p>
	 * If the file has been moved in the filesystem, this operation cannot be
	 * invoked again. Therefore, call this method just once to be able to work
	 * with any storage mechanism.
	 * <p>
	 * <strong>Note:</strong> when using Servlet 3.0 multipart support you need
	 * to configure the location relative to which files will be copied as
	 * explained in {@link javax.servlet.http.Part#write}.
	 * 
	 * @param dest the destination file
	 * @throws IOException in case of reading or writing errors
	 * @throws IllegalStateException if the file has already been moved in the
	 *             filesystem and is not available anymore for another transfer
	 */
	void transferTo(File dest) throws IOException, IllegalStateException;

}
