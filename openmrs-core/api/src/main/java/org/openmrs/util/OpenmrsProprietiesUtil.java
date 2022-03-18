package org.openmrs.util;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class OpenmrsProprietiesUtil {

	private static org.slf4j.Logger log = LoggerFactory.getLogger(OpenmrsProprietiesUtil.class);

	/**
	 * Convenience method to replace Properties.store(), which isn't UTF-8 compliant <br>
	 * NOTE: In Java 6, you will be able to pass the load() and store() methods a UTF-8
	 * Reader/Writer object as an argument, making this method unnecessary.
	 * 
	 * @param properties
	 * @param file
	 * @param comment
	 */
	public static void storeProperties(Properties properties, File file, String comment) {
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(file, true);
			storeProperties(properties, outStream, comment);
		}
		catch (IOException ex) {
			log.error("Unable to create file " + file.getAbsolutePath() + " in storeProperties routine.");
		}
		finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
			}
			catch (IOException ioe) {
				// pass
			}
		}
	}

	/**
	 * Convenience method to replace Properties.store(), which isn't UTF-8 compliant NOTE: In Java
	 * 6, you will be able to pass the load() and store() methods a UTF-8 Reader/Writer object as an
	 * argument.
	 * 
	 * @param properties
	 * @param outStream
	 * @param comment (which appears in comments in properties file)
	 */
	public static void storeProperties(Properties properties, OutputStream outStream, String comment) {
		try {
			Charset utf8 = StandardCharsets.UTF_8;
			properties.store(new OutputStreamWriter(outStream, utf8), comment);
		}
		catch (FileNotFoundException fnfe) {
			log.error("target file not found" + fnfe);
		}
		catch (UnsupportedEncodingException ex) { // pass
			log.error("unsupported encoding error hit" + ex);
		}
		catch (IOException ioex) {
			log.error("IO exception encountered trying to append to properties file" + ioex);
		}
		
	}

	/**
	 * This method is a replacement for Properties.load(InputStream) so that we can load in utf-8
	 * characters. Currently the load method expects the inputStream to point to a latin1 encoded
	 * file. <br>
	 * NOTE: In Java 6, you will be able to pass the load() and store() methods a UTF-8
	 * Reader/Writer object as an argument, making this method unnecessary.
	 * 
	 * @param props the properties object to write into
	 * @param inputStream the input stream to read from
	 */
	public static void loadProperties(Properties props, InputStream inputStream) {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			props.load(reader);
		}
		catch (FileNotFoundException fnfe) {
			log.error("Unable to find properties file" + fnfe);
		}
		catch (UnsupportedEncodingException uee) {
			log.error("Unsupported encoding used in properties file" + uee);
		}
		catch (IOException ioe) {
			log.error("Unable to read properties from properties file" + ioe);
		}
		finally {
			try {
				if (reader != null) {
					reader.close();
				}
			}
			catch (IOException ioe) {
				log.error("Unable to close properties file " + ioe);
			}
		}
	}

	/**
	 * Convenience method used to load properties from the given file.
	 * 
	 * @param props the properties object to be loaded into
	 * @param propertyFile the properties file to read
	 */
	public static void loadProperties(Properties props, File propertyFile) {
		try {
			loadProperties(props, new FileInputStream(propertyFile));
		}
		catch (FileNotFoundException fnfe) {
			log.error("Unable to find properties file" + fnfe);
		}
	}

	/**
	 * <pre>
	 * Finds and loads the runtime properties file for a specific OpenMRS application.
	 * Searches for the file in this order:
	 * 1) {current directory}/{applicationname}_runtime.properties
	 * 2) an environment variable called "{APPLICATIONNAME}_RUNTIME_PROPERTIES_FILE"
	 * 3) {openmrs_app_dir}/{applicationName}_runtime.properties   // openmrs_app_dir is typically {user_home}/.OpenMRS
	 * </pre>
	 * 
	 * @see #getApplicationDataDirectory()
	 * @param applicationName (defaults to "openmrs") the name of the running OpenMRS application,
	 *            e.g. if you have deployed OpenMRS as a web application you would give the deployed
	 *            context path here
	 * @return runtime properties, or null if none can be found
	 * @since 1.8
	 */
	public static Properties getRuntimeProperties(String applicationName) {
		if (applicationName == null) {
			applicationName = "openmrs";
		}
		String pathName;
		pathName = getRuntimePropertiesFilePathName(applicationName);
		FileInputStream propertyStream = null;
		try {
			if (pathName != null) {
				propertyStream = new FileInputStream(pathName);
			}
		}
		catch (FileNotFoundException e) {
			log.warn("Unable to find a runtime properties file at " + new File(pathName).getAbsolutePath());
		}
		
		try {
			if (propertyStream == null) {
				throw new IOException("Could not find a runtime properties file named " + pathName
				        + " in the OpenMRS application data directory, or the current directory");
			}
			
			Properties props = new Properties();
			loadProperties(props, propertyStream);
			propertyStream.close();
			log.info("Using runtime properties file: " + pathName);
			return props;
		}
		catch (Exception ex) {
			log.info("Got an error while attempting to load the runtime properties", ex);
			log.warn(
			    "Unable to find a runtime properties file. Initial setup is needed. View the webapp to run the setup wizard.");
			return null;
		}
	}

	/**
	 * Gets the full path and name of the runtime properties file.
	 * 
	 * @param applicationName (defaults to "openmrs") the name of the running OpenMRS application,
	 *            e.g. if you have deployed OpenMRS as a web application you would give the deployed
	 *            context path here
	 * @return runtime properties file path and name, or null if none can be found
	 * @since 1.9
	 */
	public static String getRuntimePropertiesFilePathName(String applicationName) {
		if (applicationName == null) {
			applicationName = "openmrs";
		}
		
		String defaultFileName = applicationName + "-runtime.properties";
		String fileNameInTestMode = getRuntimePropertiesFileNameInTestMode();
		
		// first look in the current directory (that java was started from)
		String pathName = fileNameInTestMode != null ? fileNameInTestMode : defaultFileName;
		log.debug("Attempting to look for properties file in current directory: " + pathName);
		if (new File(pathName).exists()) {
			return pathName;
		} else {
			log.warn("Unable to find a runtime properties file at " + new File(pathName).getAbsolutePath());
		}
		
		// next look from environment variable
		String envVarName = applicationName.toUpperCase() + "_RUNTIME_PROPERTIES_FILE";
		String envFileName = System.getenv(envVarName);
		if (envFileName != null) {
			log.debug("Atempting to look for runtime properties from: " + pathName);
			if (new File(envFileName).exists()) {
				return envFileName;
			} else {
				log.warn("Unable to find properties file with path: " + pathName + ". (derived from environment variable "
				        + envVarName + ")");
			}
		} else {
			log.info("Couldn't find an environment variable named " + envVarName);
			if (log.isDebugEnabled()) {
				log.debug("Available environment variables are named: " + System.getenv().keySet());
			}
		}
		
		// next look in the OpenMRS application data directory
		File file = new File(OpenmrsExtUtil.getApplicationDataDirectory(), pathName);
		pathName = file.getAbsolutePath();
		log.debug("Attempting to look for property file from: " + pathName);
		if (file.exists()) {
			return pathName;
		} else {
			log.warn("Unable to find properties file: " + pathName);
		}
		
		return null;
	}

	public static String getRuntimePropertiesFileNameInTestMode() {
		String filename = null;
		if (isTestMode()) {
			log.info("In functional testing mode. Ignoring the existing runtime properties file");
			filename = getOpenMRSVersionInTestMode() + "-test-runtime.properties";
		}
		return filename;
	}

	/**
	 * Checks whether the system is running in test mode
	 *
	 * @return boolean
	 */

	public static boolean isTestMode() {
		return "true".equalsIgnoreCase(System.getProperty("FUNCTIONAL_TEST_MODE"));
	}

	/**
	 * Gets OpenMRS version name under test mode.
	 *
	 * @return String openmrs version number
	 */
	public static String getOpenMRSVersionInTestMode() {
		return System.getProperty("OPENMRS_VERSION", "openmrs");
	}

	
}
