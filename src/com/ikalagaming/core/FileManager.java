
package com.ikalagaming.core;

import java.io.File;
import java.io.IOException;

/**
 *
 * Contains helper methods for handling files.
 *
 * @author Ches Burks
 *
 */
public class FileManager {

	/**
	 * Returns true if a file exists at the given path.
	 *
	 * @param path the path to the file
	 * @return true if the supplied file exists
	 */
	public static boolean fileExists(String path) {
		File f;
		f = new File(path);
		return f.exists();
	}

	/**
	 * Returns a reference to the requested file. The file may be null, and no
	 * permissions are guaranteed.
	 *
	 * @param path the path of the file
	 * @return the file requested
	 */
	public static File getFile(String path) {
		File f;
		f = new File(path);
		return f;
	}

	/**
	 * Creates a blank file at the supplied path with the supplied name. If the
	 * file already exists, or the file location is not writable by the program,
	 * no file is created and false is returned.
	 *
	 * @param path the path of the parent folder
	 * @param filename the name of the file
	 * @return true if the file was created, otherwise false
	 */
	public static boolean createFile(String path, String filename) {
		File f;
		boolean success = true;
		f = new File(path + filename);
		if (!f.exists()) {
			try {
				f.createNewFile();
			}
			catch (IOException e) {
				success = false;
			}
		}
		else {
			success = false;
		}
		return success;
	}

	/**
	 * Creates an empty folder at the supplied path with the supplied name. If
	 * the folder already exists, or the folder location is not writable by the
	 * program, no folder is created and false is returned. Any necessary but
	 * nonexistent parent directories will be created.
	 *
	 * @param path the path of the folder to create
	 * @return true if the folder was created, otherwise false
	 */
	public static boolean createFolder(String path) {
		File f;
		boolean success = true;
		f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		else {
			success = false;
		}
		return success;
	}

	/**
	 * If the supplied path is a file, it exists, and it can be read and written
	 * to, then the file is deleted.
	 *
	 * @param path the path of the file
	 */
	public static void deleteFile(String path) {
		File f;
		f = new File(path);
		if (!f.exists()) {
			return;
		}
		if (!f.isFile()) {
			return;
		}
		if (!f.canRead()) {
			return;
		}
		if (!f.canWrite()) {
			return;
		}
		f.delete();
	}

	/**
	 * If the supplied path is a folder, it exists, is empty, and it can be read
	 * and written to, then the folder is deleted.
	 *
	 * @param path the path of the folder
	 */
	public static void deleteFolder(String path) {
		File f;
		f = new File(path);
		if (!f.exists()) {
			return;
		}
		if (!f.isDirectory()) {
			return;
		}
		if (!f.canRead()) {
			return;
		}
		if (!f.canWrite()) {
			return;
		}
		if (!(f.list().length == 0)) {
			return;// it contains files
		}
		f.delete();
	}
}
