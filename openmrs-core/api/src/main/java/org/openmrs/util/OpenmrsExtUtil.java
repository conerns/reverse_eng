package org.openmrs.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleException;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class OpenmrsExtUtil {

	private static org.slf4j.Logger log = LoggerFactory.getLogger(OpenmrsExtUtil.class);
	private static volatile MimetypesFileTypeMap mimetypesFileTypeMap = null;

	/**
	 * Save the given xml document to the given outfile
	 *
	 * @param doc Document to be saved
	 * @param outFile file pointer to the location the xml file is to be saved to
	 */
	public static void saveDocument(Document doc, File outFile) {
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(outFile);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DocumentType doctype = doc.getDoctype();
			if (doctype != null) {
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
			}

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outStream);
			transformer.transform(source, result);
		}
		catch (TransformerException e) {
			throw new ModuleException("Error while saving dwrmodulexml back to dwr-modules.xml", e);
		}
		catch (FileNotFoundException e) {
			throw new ModuleException(outFile.getAbsolutePath() + " file doesn't exist.", e);
		}
		finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
			}
			catch (Exception e) {
				log.warn("Unable to close outstream", e);
			}
		}
	}
	
	/**
	 * Look for a file named <code>filename</code> in folder
	 * 
	 * @param folder
	 * @param filename
	 * @return true/false whether filename exists in folder
	 */
	public static boolean folderContains(File folder, String filename) {
		if (folder == null) {
			return false;
		}
		if (!folder.isDirectory()) {
			return false;
		}
		File[] files = folder.listFiles();
		if (files == null) {
			return false;
		}
		
		for (File f : files) {
			if (f.getName().equals(filename)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Recursively deletes files in the given <code>dir</code> folder
	 * 
	 * @param dir File directory to delete
	 * @return true/false whether the delete was completed successfully
	 * @throws IOException if <code>dir</code> is not a directory
	 */
	public static boolean deleteDirectory(File dir) throws IOException {
		if (!dir.exists() || !dir.isDirectory()) {
			throw new IOException("Could not delete directory '" + dir.getAbsolutePath() + "' (not a directory)");
		}
		
		log.debug("Deleting directory {}", dir.getAbsolutePath());
		
		File[] fileList = dir.listFiles();
		if (fileList == null) {
			return false;
		}
		for (File f : fileList) {
			if (f.isDirectory()) {
				deleteDirectory(f);
			}
			boolean success = f.delete();
			
			if (log.isDebugEnabled()) {
				log.debug("   deleting " + f.getName() + " : " + (success ? "ok" : "failed"));
			}
			
			if (!success) {
				f.deleteOnExit();
			}
		}
		
		boolean success = dir.delete();
		
		if (!success) {
			log.warn("   ...could not remove directory: " + dir.getAbsolutePath());
			dir.deleteOnExit();
		}
		
		if (success && log.isDebugEnabled()) {
			log.debug("   ...and directory itself");
		}
		
		return success;
	}

	/**
	 * Utility method to convert local URL to a File object.
	 * 
	 * @param url an URL
	 * @return file object for given URL or <code>null</code> if URL is not local
	 * <strong>Should</strong> return null given null parameter
	 */
	public static File url2file(final URL url) {
		if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
			return null;
		}
		return new File(url.getFile().replaceAll("%20", " "));
	}

	public static File getApplicationDataDirectoryAsFile() {
		String filepath = null;
		final String openmrsDir = "OpenMRS";
		
		String systemProperty = System.getProperty(OpenmrsConstants.KEY_OPENMRS_APPLICATION_DATA_DIRECTORY);
		//System and runtime property take precedence
		if (StringUtils.isNotBlank(systemProperty)) {
			filepath = systemProperty;
		} else {
			String runtimeProperty = Context.getRuntimeProperties()
				.getProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, null);
			if (StringUtils.isNotBlank(runtimeProperty)) {
				filepath = runtimeProperty;
			}
		}
		
		if (filepath == null) {
			if (OpenmrsConstants.UNIX_BASED_OPERATING_SYSTEM) {
				filepath = Paths.get(System.getProperty("user.home"), "." + openmrsDir).toString();
				if (!canWrite(new File(filepath))) {
					log.warn("Unable to write to users home dir, fallback to: "
						+ OpenmrsConstants.APPLICATION_DATA_DIRECTORY_FALLBACK_UNIX);
					filepath = Paths.get(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_FALLBACK_UNIX, openmrsDir).toString();
				}
			} else {
				filepath = Paths.get(System.getProperty("user.home"), "Application Data", "OpenMRS").toString();
				if (!new File(filepath).exists()) {
					filepath = Paths.get(System.getenv("appdata"), "OpenMRS").toString();
				}
				if (!canWrite(new File(filepath))) {
					log.warn("Unable to write to users home dir, fallback to: "
						+ OpenmrsConstants.APPLICATION_DATA_DIRECTORY_FALLBACK_WIN);
					filepath = OpenmrsConstants.APPLICATION_DATA_DIRECTORY_FALLBACK_WIN + File.separator + openmrsDir;
				}
			}
			
			filepath = filepath + File.separator;
		}
		
		File folder = new File(filepath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		return folder;
	}

	/**
	 * Checks if we can write to a given folder.
	 * 
	 * @param folder the directory to check.
	 * @return true if we can write to it, else false.
	 */
	private static boolean canWrite(File folder) {
		try {
			//We need to first create the folder if it does not exist, 
			//else File.canWrite() will return false even when we
			//have the necessary permissions.
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			return folder.canWrite();
		}
		catch (SecurityException ex) {
			//all we wanted to know is whether we have permissions
		}
		
		return false;
	}

	/**
	 * Find the given folderName in the application data directory. Or, treat folderName like an
	 * absolute url to a directory
	 * 
	 * @param folderName
	 * @return folder capable of storing information
	 */
	public static File getDirectoryInApplicationDataDirectory(String folderName) throws APIException {
		// try to load the repository folder straight away.
		File folder = new File(folderName);
		
		// if the property wasn't a full path already, assume it was intended to
		// be a folder in the
		// application directory
		if (!folder.isAbsolute()) {
			folder = new File(getApplicationDataDirectoryAsFile(), folderName);
		}
		
		// now create the directory folder if it doesn't exist
		if (!folder.exists()) {
			log.warn("'" + folder.getAbsolutePath() + "' doesn't exist.  Creating directories now.");
			folder.mkdirs();
		}
		
		if (!folder.isDirectory()) {
			throw new APIException("should.be.directory", new Object[] { folder.getAbsolutePath() });
		}
		
		return folder;
	}

	/**
	 * Gets an out File object. If date is not provided, the current timestamp is used. If user is
	 * not provided, the user id is not put into the filename. Assumes dir is already created
	 * 
	 * @param dir directory to make the random filename in
	 * @param date optional Date object used for the name
	 * @param user optional User creating this file object
	 * @return file new file that is able to be written to
	 */
	public static File getOutFile(File dir, Date date, User user) {
		Random gen = new Random();
		File outFile;
		do {
			// format to print date in filename
			DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HHmm-ssSSS");
			
			// use current date if none provided
			if (date == null) {
				date = new Date();
			}
			
			StringBuilder filename = new StringBuilder();
			
			// the start of the filename is the time so we can do some sorting
			filename.append(dateFormat.format(date));
			
			// insert the user id if they provided it
			if (user != null) {
				filename.append("-");
				filename.append(user.getUserId());
				filename.append("-");
			}
			
			// the end of the filename is a randome number between 0 and 10000
			filename.append(gen.nextInt() * 10000);
			filename.append(".xml");
			
			outFile = new File(dir, filename.toString());
			
			// set to null to avoid very minimal possiblity of an infinite loop
			date = null;
			
		} while (outFile.exists());
		
		return outFile;
	}

	/**
	 * Return a string representation of the given file
	 * 
	 * @param file
	 * @return String file contents
	 * @throws IOException
	 */
	public static String getFileAsString(File file) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		char[] buf = new char[1024];
		int numRead;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	/**
	 * Return a byte array representation of the given file
	 * 
	 * @param file
	 * @return byte[] file contents
	 * @throws IOException
	 */
	public static byte[] getFileAsBytes(File file) throws IOException {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte[] b = new byte[fileInputStream.available()];
			fileInputStream.read(b);
			return b;
		}
		catch (Exception e) {
			log.error("Unable to get file as byte array", e);
		}
		finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				}
				catch (IOException io) {
					log.warn("Couldn't close fileInputStream: " + io);
				}
			}
		}
		
		return null;
	}

	/**
	 * Copy file from inputStream onto the outputStream inputStream is not closed in this method
	 * outputStream /is/ closed at completion of this method
	 * 
	 * @param inputStream Stream to copy from
	 * @param outputStream Stream/location to copy to
	 * @throws IOException thrown if an error occurs during read/write
	 * <strong>Should</strong> not copy the outputstream if outputstream is null
	 * <strong>Should</strong> not copy the outputstream if inputstream is null
	 * <strong>Should</strong> copy inputstream to outputstream and close the outputstream
	 */
	public static void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
		
		if (inputStream == null || outputStream == null) {
			if (outputStream != null) {
				IOUtils.closeQuietly(outputStream);
			}
			return;
		}
		
		try {
			IOUtils.copy(inputStream, outputStream);
		}
		finally {
			IOUtils.closeQuietly(outputStream);
		}
		
	}

	/**
	 * Get mime type of the given file
	 *
	 * @param file
	 * @return mime type
	 */
	public static String getFileMimeType(File file) {
		if (mimetypesFileTypeMap == null) {
			synchronized (OpenmrsUtil.class) {
				mimetypesFileTypeMap = new MimetypesFileTypeMap();
			}
		}
		return mimetypesFileTypeMap.getContentType(file);
	}

	/**
	 * Opens input stream for given resource. This method behaves differently for different URL
	 * types:
	 * <ul>
	 * <li>for <b>local files</b> it returns buffered file input stream;</li>
	 * <li>for <b>local JAR files</b> it reads resource content into memory buffer and returns byte
	 * array input stream that wraps those buffer (this prevents locking JAR file);</li>
	 * <li>for <b>common URL's</b> this method simply opens stream to that URL using standard URL
	 * API.</li>
	 * </ul>
	 * It is not recommended to use this method for big resources within JAR files.
	 * 
	 * @param url resource URL
	 * @return input stream for given resource
	 * @throws IOException if any I/O error has occurred
	 */
	public static InputStream getResourceInputStream(final URL url) throws IOException {
		File file = url2file(url);
		if (file != null) {
			return new BufferedInputStream(new FileInputStream(file));
		}
		if (!"jar".equalsIgnoreCase(url.getProtocol())) {
			return url.openStream();
		}
		String urlStr = url.toExternalForm();
		if (urlStr.endsWith("!/")) {
			// JAR URL points to a root entry
			throw new FileNotFoundException(url.toExternalForm());
		}
		int p = urlStr.indexOf("!/");
		if (p == -1) {
			throw new MalformedURLException(url.toExternalForm());
		}
		String path = urlStr.substring(p + 2);
		file = url2file(new URL(urlStr.substring(4, p)));
		if (file == null) {// non-local JAR file URL
			return url.openStream();
		}
		try (JarFile jarFile = new JarFile(file)) {
			ZipEntry entry = jarFile.getEntry(path);
			if (entry == null) {
				throw new FileNotFoundException(url.toExternalForm());
			}
			try (InputStream in = jarFile.getInputStream(entry)) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				copyFile(in, out);
				return new ByteArrayInputStream(out.toByteArray());
			}
		}
	}

	/**
	 * Can be used to override default application data directory.
	 * <p>
	 * Note that it will not override application data directory provided as a system property.
	 * 
	 * @param path
	 * @since 1.11
	 */
	public static void setApplicationDataDirectory(String path) {
		if (StringUtils.isBlank(path)) {
			System.clearProperty(OpenmrsConstants.KEY_OPENMRS_APPLICATION_DATA_DIRECTORY);
		} else {
			System.setProperty(OpenmrsConstants.KEY_OPENMRS_APPLICATION_DATA_DIRECTORY, path);
		}
	}

	/**
	 * <pre>
	 * Returns the application data directory. Searches for the value first 
	 * in the "OPENMRS_APPLICATION_DATA_DIRECTORY" system property and "application_data_directory" runtime property, then in the servlet
	 * init parameter "application.data.directory." If not found, returns:
	 * a) "{user.home}/.OpenMRS" on UNIX-based systems
	 * b) "{user.home}\Application Data\OpenMRS" on Windows
	 *
	 * </pre>
	 *
	 * @return The path to the directory on the file system that will hold miscellaneous data about
	 *         the application (runtime properties, modules, etc)
	 */
	public static String getApplicationDataDirectory() {
		return OpenmrsExtUtil.getApplicationDataDirectoryAsFile().toString();
	}

	/**
	 * A null-safe and exception safe way to close an inputstream or an outputstream
	 * 
	 * @param closableStream an InputStream or OutputStream to close
	 */
	public static void closeStream(Closeable closableStream) {
		if (closableStream != null) {
			try {
				closableStream.close();
			}
			catch (IOException io) {
				log.trace("Error occurred while closing stream", io);
			}
		}
	}
}
