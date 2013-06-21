package supercoderz.pushpak.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Pattern;

import supercoderz.pushpak.utils.LogUtils;

public class FileHelper {

	/**
	 * Helper method to serialize a byte array to a file
	 * @param filename the file name to be used
	 * @param data the data to be written
	 * @throws IOException throws IOException in case of errors when writing the file
	 */
	public static void write(String filename, byte[] data) throws IOException {
		long epoch=System.nanoTime();
		File file = new File(filename);
		//created only if the file does not exist - 
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.close();
		LogUtils.debug("FileHelper", "Completed serializing data to file "+filename + " in " +(System.nanoTime()-epoch));
	}

	/**
	 * Helper method to read a byte array of data that was previously serialized to a file
	 * @param filename the file name to read
	 * @return the data as a byte array
	 * @throws IOException throws IOException in case of errors while reading the file
	 */
	public static byte[] read(String filename) throws IOException {
		long epoch=System.nanoTime();
		File file = new File(filename);
		if(!file.exists()){
			return new byte[0];
		}
		FileInputStream fis = new FileInputStream(file);
		byte[] data=new byte[fis.available()];
		fis.read(data);
		fis.close();
		LogUtils.debug("FileHelper", "Completed de-serializing data from file "+filename + " in " +(System.nanoTime()-epoch));
		return data;
	}

	/**
	 * Helper method to get all the files for a given subject pattern
	 * 
	 * @param subjectRegex
	 *            the regex pattern of the subject
	 * @param baseDir
	 *            the base directory where the files are stored
	 * @return the list of filenames that match the pattern
	 */
	public static String[] getFiles(String subjectRegex, String baseDir) {
		final Pattern pattern = Pattern.compile(subjectRegex);
		File dir = new File(baseDir);
		String[] files = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return pattern.matcher(name).matches();
			}
		});
		LogUtils.debug("FileHelper", "Found " + files.length
				+ " files for regex pattern " + subjectRegex);
		return files;
	}

}
