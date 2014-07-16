package fr.licpro.filebox.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Class used manager cache <br/>
 * Only private cache
 * 
 * @author Julien Millau
 * 
 */
public class FileCache {

	/**
	 * External SDCARD folder
	 */
	private static final String SDCARD_FOLDER = Environment.getExternalStorageDirectory() + "/Android/data/%s/files/";

	/**
	 * File cache dir for application
	 */
	private static File cacheDir;

	/**
	 * Constructor of FileCache
	 * 
	 * @param context
	 *            application context
	 */
	public FileCache(Context context) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			cacheDir = new File(getSdCacheLocation(context));
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	/**
	 * Get sdCard location
	 * 
	 * @param context
	 * @return
	 */
	private static String getSdCacheLocation(Context context) {
		return String.format(SDCARD_FOLDER, context.getPackageName());
	}

	/**
	 * 
	 * @param pFileId
	 * @return
	 */
	private File getSimpleFile(String pFileId) {
		return new File(cacheDir, pFileId);
	}

	/**
	 * Method used to get a file with the specified Idin the cache
	 * 
	 * @param pFileId
	 *            id of the file to find
	 * @return the file object or null if the file was not found
	 */
	public File getFile(String pFileId) {
		File f = getSimpleFile(pFileId);
		if (!f.exists()) {
			f = null;
		}
		return f;
	}

	/**
	 * Method used to store a file on disk
	 * 
	 * @param pFileId
	 *            file to store
	 * @param pIs
	 *            inputStream
	 */
	public void putFile(final String pFileId, final InputStream pIs) {
		File file = getSimpleFile(pFileId);
		if (file.exists()) {
			file.delete();
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e(FileCache.class.getName(), e.getMessage(), e);
			}
		}
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			copy(pIs, outputStream);
		} catch (Exception e) {
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				pIs.close();
			} catch (Exception ignore) {

			}
		}

	}

	/**
	 * Method used to clear the cache
	 */
	public void clearCache() {
		File[] files = cacheDir.listFiles();
		for (File f : files) {
			f.delete();
		}
	}

	/**
	 * Method used to copy the content of an inputStream to an outputStream
	 * 
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	private static long copy(InputStream input, OutputStream output) throws IOException {
		long count = 0;
		int n = 0;
		byte[] buffer = new byte[1024 * 4];
		while ((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

}