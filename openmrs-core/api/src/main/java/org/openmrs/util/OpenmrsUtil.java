/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.annotation.AddOnStartup;
import org.openmrs.annotation.HasAddOnStartupPrivileges;
import org.openmrs.annotation.Logging;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.InvalidCharactersPasswordException;
import org.openmrs.api.PasswordException;
import org.openmrs.api.ShortPasswordException;
import org.openmrs.api.WeakPasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.logging.OpenmrsLoggingUtil;
import org.openmrs.module.ModuleFactory;
import org.openmrs.propertyeditor.CohortEditor;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.openmrs.propertyeditor.EncounterTypeEditor;
import org.openmrs.propertyeditor.FormEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonAttributeTypeEditor;
import org.openmrs.propertyeditor.ProgramEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;

/**
 * Utility methods used in openmrs
 */
public class OpenmrsUtil {
	private OpenmrsUtil() {
	}
	
	
	private static org.slf4j.Logger log = LoggerFactory.getLogger(OpenmrsUtil.class);

	/**
	 * These are the privileges that are required by OpenMRS. This looks for privileges marked as
	 * {@link AddOnStartup} to know which privs, upon startup or loading of a module, to insert into
	 * the database if they do not exist already. These privileges are not allowed to be deleted.
	 * They are marked as 'locked' in the administration screens.
	 * 
	 * @return privileges core to the system
	 * @see PrivilegeConstants
	 * @see Context#checkCoreDataset()
	 */
	public static Map<String, String> getCorePrivileges() {
		Map<String, String> corePrivileges = new HashMap<>();
		
		// TODO getCorePrivileges() is called so so many times that getClassesWithAnnotation() better do some catching.
		Set<Class<?>> classes = OpenmrsClassScanner.getInstance().getClassesWithAnnotation(HasAddOnStartupPrivileges.class);
		
		for (Class cls : classes) {
			Field[] flds = cls.getDeclaredFields();
			for (Field fld : flds) {
				String fieldValue = null;
				
				AddOnStartup privilegeAnnotation = fld.getAnnotation(AddOnStartup.class);
				if (null == privilegeAnnotation) {
					continue;
				}
				if (!privilegeAnnotation.core()) {
					continue;
				}
				
				try {
					fieldValue = (String) fld.get(null);
				}
				catch (IllegalAccessException e) {
					log.error("Field is inaccessible.", e);
				}
				corePrivileges.put(fieldValue, privilegeAnnotation.description());
			}
		}
		
		// always add the module core privileges back on
		for (org.openmrs.Privilege privilege : ModuleFactory.getPrivileges()) {
			corePrivileges.put(privilege.getPrivilege(), privilege.getDescription());
		}
		
		return corePrivileges;
	}
	
	/**
	 * All roles returned by this method are inserted into the database if they do not exist
	 * already. These roles are also forbidden to be deleted from the administration screens.
	 * 
	 * @return roles that are core to the system
	 */
	public static Map<String, String> getCoreRoles() {
		Map<String, String> roles = new HashMap<>();
		
		Field[] flds = RoleConstants.class.getDeclaredFields();
		for (Field fld : flds) {
			String fieldValue = null;
			
			AddOnStartup roleAnnotation = fld.getAnnotation(AddOnStartup.class);
			if (null == roleAnnotation) {
				continue;
			}
			if (!roleAnnotation.core()) {
				continue;
			}
			
			try {
				fieldValue = (String) fld.get(null);
			}
			catch (IllegalAccessException e) {
				log.error("Field is inaccessible.", e);
			}
			roles.put(fieldValue, roleAnnotation.description());
		}
		
		return roles;
	}
	
	/**
	 * Initialize global settings Find and load modules
	 * 
	 * @param p properties from runtime configuration
	 */
	public static void startup(Properties p) {
		
		// Override global OpenMRS constants if specified by the user
		
		// Allow for "demo" mode where patient data is obscured
		String val = p.getProperty("obscure_patients", null);
		if (val != null && "true".equalsIgnoreCase(val)) {
			OpenmrsConstants.OBSCURE_PATIENTS = true;
		}
		
		val = p.getProperty("obscure_patients.family_name", null);
		if (val != null) {
			OpenmrsConstants.OBSCURE_PATIENTS_FAMILY_NAME = val;
		}
		
		val = p.getProperty("obscure_patients.given_name", null);
		if (val != null) {
			OpenmrsConstants.OBSCURE_PATIENTS_GIVEN_NAME = val;
		}
		
		val = p.getProperty("obscure_patients.middle_name", null);
		if (val != null) {
			OpenmrsConstants.OBSCURE_PATIENTS_MIDDLE_NAME = val;
		}
		
		// Override the default "openmrs" database name
		val = p.getProperty("connection.database_name", null);
		if (val == null) {
			// the database name wasn't supplied explicitly, guess it
			// from the connection string
			val = p.getProperty("connection.url", null);
			
			if (val != null) {
				try {
					int endIndex = val.lastIndexOf("?");
					if (endIndex == -1) {
						endIndex = val.length();
					}
					int startIndex = val.lastIndexOf("/", endIndex);
					val = val.substring(startIndex + 1, endIndex);
					OpenmrsConstants.DATABASE_NAME = val;
				}
				catch (Exception e) {
					log.error(MarkerFactory.getMarker("FATAL"), "Database name cannot be configured from 'connection.url' ."
					        + "Either supply 'connection.database_name' or correct the url",
					    e);
				}
			}
		}
		
		// set the business database name
		val = p.getProperty("connection.database_business_name", null);
		if (val == null) {
			val = OpenmrsConstants.DATABASE_NAME;
		}
		OpenmrsConstants.DATABASE_BUSINESS_NAME = val;
	}
	
	/**
	 * Gets the in-memory log appender. This method needed to be added as it is much more difficult to
	 * get a specific appender in the Log4J2 architecture. This method is called in places where we need
	 * to display logging message.
	 *
	 * @since 2.4.0
	 * @deprecated As of 2.4.4, 2.5.1, and 2.6.0; replaced by {@link OpenmrsLoggingUtil#getMemoryAppender()} instead
	 */
	@Deprecated
	public static MemoryAppender getMemoryAppender() {
		return new MemoryAppender(OpenmrsLoggingUtil.getMemoryAppender());
	}
	
	/**
	 * Set the org.openmrs log4j logger's level if global property log.level.openmrs (
	 * OpenmrsConstants.GLOBAL_PROPERTY_LOG_LEVEL ) exists. Valid values for global property are
	 * trace, debug, info, warn, error or fatal.
	 * 
	 * @deprecated As of 2.4.4, 2.5.1, and 2.6.0; replaced by {@link OpenmrsLoggingUtil#applyLogLevels()}
	 */
	@Logging(ignore = true)
	@Deprecated
	public static void applyLogLevels() {
		OpenmrsLoggingUtil.applyLogLevels();
	}
	
	/**
	 * Setup root level log appenders.
	 *
	 * @since 1.9.2
	 * @deprecated As of 2.4.4, 2.5.1, and 2.6.0; replaced by {@link OpenmrsLoggingUtil#reloadLoggingConfiguration()}
	 */
	@Deprecated
	public static void setupLogAppenders() {
		OpenmrsLoggingUtil.reloadLoggingConfiguration();
	}
	
	/**
	 * Set the log4j log level for class <code>logClass</code> to <code>logLevel</code>.
	 * 
	 * @param logClass optional string giving the class level to change. Defaults to
	 *            OpenmrsConstants.LOG_CLASS_DEFAULT . Should be something like org.openmrs.___
	 * @param logLevel one of OpenmrsConstants.LOG_LEVEL_*
	 *                 
	 * @deprecated As of 2.4.4, 2.5.1, and 2.6.0; replaced by {@link OpenmrsLoggingUtil#applyLogLevel(String, String)}
	 */
	@Deprecated
	public static void applyLogLevel(String logClass, String logLevel) {
		OpenmrsLoggingUtil.applyLogLevel(logClass, logLevel);
	}

	public static Set<Concept> conceptSetHelper(String descriptor) {
		Set<Concept> ret = new HashSet<>();
		if (descriptor == null || descriptor.length() == 0) {
			return ret;
		}
		ConceptService cs = Context.getConceptService();
		
		for (StringTokenizer st = new StringTokenizer(descriptor, "|"); st.hasMoreTokens();) {
			String s = st.nextToken().trim();
			boolean isSet = s.startsWith("set:");
			if (isSet) {
				s = s.substring(4).trim();
			}
			Concept c = null;
			if (s.startsWith("name:")) {
				String name = s.substring(5).trim();
				c = cs.getConceptByName(name);
			} else {
				try {
					c = cs.getConcept(Integer.valueOf(s.trim()));
				}
				catch (Exception ex) {}
			}
			if (c != null) {
				if (isSet) {
					List<Concept> inSet = cs.getConceptsByConceptSet(c);
					ret.addAll(inSet);
				} else {
					ret.add(c);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Parses and loads a delimited list of concept ids or names
	 * 
	 * @param delimitedString the delimited list of concept ids or names
	 * @param delimiter the delimiter, e.g. ","
	 * @return the list of concepts
	 * @since 1.10, 1.9.2, 1.8.5
	 */
	public static List<Concept> delimitedStringToConceptList(String delimitedString, String delimiter) {
		List<Concept> ret = null;
		
		if (delimitedString != null) {
			String[] tokens = delimitedString.split(delimiter);
			for (String token : tokens) {
				Integer conceptId;
				
				try {
					conceptId = Integer.valueOf(token);
				}
				catch (NumberFormatException nfe) {
					conceptId = null;
				}
				
				Concept c;
				
				if (conceptId != null) {
					c = Context.getConceptService().getConcept(conceptId);
				} else {
					c = Context.getConceptService().getConceptByName(token);
				}
				
				if (c != null) {
					if (ret == null) {
						ret = new ArrayList<>();
					}
					ret.add(c);
				}
			}
		}
		
		return ret;
	}
	
	public static Map<String, Concept> delimitedStringToConceptMap(String delimitedString, String delimiter) {
		Map<String, Concept> ret = null;
		
		if (delimitedString != null) {
			String[] tokens = delimitedString.split(delimiter);
			for (String token : tokens) {
				Concept c = Context.getConceptService().getConcept(token);
				
				if (c != null) {
					if (ret == null) {
						ret = new HashMap<>();
					}
					ret.put(token, c);
				}
			}
		}
		
		return ret;
	}
	
	// TODO: properly handle duplicates
	public static List<Concept> conceptListHelper(String descriptor) {
		List<Concept> ret = new ArrayList<>();
		if (descriptor == null || descriptor.length() == 0) {
			return ret;
		}
		ConceptService cs = Context.getConceptService();
		
		for (StringTokenizer st = new StringTokenizer(descriptor, "|"); st.hasMoreTokens();) {
			String s = st.nextToken().trim();
			boolean isSet = s.startsWith("set:");
			if (isSet) {
				s = s.substring(4).trim();
			}
			Concept c = null;
			if (s.startsWith("name:")) {
				String name = s.substring(5).trim();
				c = cs.getConceptByName(name);
			} else {
				try {
					c = cs.getConcept(Integer.valueOf(s.trim()));
				}
				catch (Exception ex) {}
			}
			if (c != null) {
				if (isSet) {
					List<Concept> inSet = cs.getConceptsByConceptSet(c);
					ret.addAll(inSet);
				} else {
					ret.add(c);
				}
			}
		}
		return ret;
	}

	/**
	 * Returns the location of the OpenMRS log file.
	 * <p/>
	 * <strong>Warning:</strong> as of 2.4.4, 2.5.1, and 2.6.0 which allows configuration via a configuration file, the
	 * result of this call can return null if either the file appender uses a name other than
	 * {@link OpenmrsConstants#LOG_OPENMRS_FILE_APPENDER} or if the appender with that name is not one of the default file
	 * appending types.
	 * 
	 * @return the path to the OpenMRS log file
	 * @since 1.9.2
	 * @deprecated As of 2.4.4, 2.5.1, and 2.6.0; replaced by {@link OpenmrsLoggingUtil#getOpenmrsLogLocation()}
	 */
	@Deprecated
	public static String getOpenmrsLogLocation() {
		return OpenmrsLoggingUtil.getOpenmrsLogLocation();
	}
	
	/**
	 * Checks whether the current JVM version is at least Java 8.
	 * 
	 * @throws APIException if the current JVM version is earlier than Java 8
	 */
	public static void validateJavaVersion() {
		// check whether the current JVM version is at least Java 8
		if (System.getProperty("java.version").matches("1\\.[0-7]\\.(.*)")) {
			throw new APIException(
				"OpenMRS " + OpenmrsConstants.OPENMRS_VERSION_SHORT + " requires Java 8 and above, but is running under " + 
					System.getProperty("java.version"));
		}
	}


	public static boolean isConceptInList(Concept concept, List<Concept> list) {
		boolean ret = false;
		if (concept != null && list != null) {
			for (Concept c : list) {
				if (c.equals(concept)) {
					ret = true;
					break;
				}
			}
		}
		
		return ret;
	}

	/**
	 * Takes a String (e.g. a user-entered one) and parses it into an object of the specified class
	 * 
	 * @param string
	 * @param clazz
	 * @return Object of type <code>clazz</code> with the data from <code>string</code>
	 */
	@SuppressWarnings("unchecked")
	public static Object parse(String string, Class clazz) {
		try {
			// If there's a valueOf(String) method, just use that (will cover at
			// least String, Integer, Double, Boolean)
			Method valueOfMethod = null;
			try {
				valueOfMethod = clazz.getMethod("valueOf", String.class);
			}
			catch (NoSuchMethodException ex) {}
			if (valueOfMethod != null) {
				return valueOfMethod.invoke(null, string);
			} else if (clazz.isEnum()) {
				// Special-case for enum types
				List<Enum> constants = Arrays.asList((Enum[]) clazz.getEnumConstants());
				for (Enum e : constants) {
					if (e.toString().equals(string)) {
						return e;
					}
				}
				throw new IllegalArgumentException(string + " is not a legal value of enum class " + clazz);
			} else if (String.class.equals(clazz)) {
				return string;
			} else if (Location.class.equals(clazz)) {
				try {
					Integer.parseInt(string);
					LocationEditor ed = new LocationEditor();
					ed.setAsText(string);
					return ed.getValue();
				}
				catch (NumberFormatException ex) {
					return Context.getLocationService().getLocation(string);
				}
			} else if (Concept.class.equals(clazz)) {
				ConceptEditor ed = new ConceptEditor();
				ed.setAsText(string);
				return ed.getValue();
			} else if (Program.class.equals(clazz)) {
				ProgramEditor ed = new ProgramEditor();
				ed.setAsText(string);
				return ed.getValue();
			} else if (ProgramWorkflowState.class.equals(clazz)) {
				ProgramWorkflowStateEditor ed = new ProgramWorkflowStateEditor();
				ed.setAsText(string);
				return ed.getValue();
			} else if (EncounterType.class.equals(clazz)) {
				EncounterTypeEditor ed = new EncounterTypeEditor();
				ed.setAsText(string);
				return ed.getValue();
			} else if (Form.class.equals(clazz)) {
				FormEditor ed = new FormEditor();
				ed.setAsText(string);
				return ed.getValue();
			} else if (Drug.class.equals(clazz)) {
				DrugEditor ed = new DrugEditor();
				ed.setAsText(string);
				return ed.getValue();
			} else if (PersonAttributeType.class.equals(clazz)) {
				PersonAttributeTypeEditor ed = new PersonAttributeTypeEditor();
				ed.setAsText(string);
				return ed.getValue();
			} else if (Cohort.class.equals(clazz)) {
				CohortEditor ed = new CohortEditor();
				ed.setAsText(string);
				return ed.getValue();
			} else if (Date.class.equals(clazz)) {
				// TODO: this uses the date format from the current session,
				// which could cause problems if the user changes it after
				// searching.
				CustomDateEditor ed = new CustomDateEditor(Context.getDateFormat(), true, 10);
				ed.setAsText(string);
				return ed.getValue();
			} else if (Object.class.equals(clazz)) {
				// TODO: Decide whether this is a hack. Currently setting Object
				// arguments with a String
				return string;
			} else {
				throw new IllegalArgumentException("Don't know how to handle class: " + clazz);
			}
		}
		catch (Exception ex) {
			log.error("error converting \"" + string + "\" to " + clazz, ex);
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * Utility to check the validity of a password for a certain {@link User}. Passwords must be
	 * non-null. Their required strength is configured via global properties:
	 * <table summary="Configuration props">
	 * <tr>
	 * <th>Description</th>
	 * <th>Property</th>
	 * <th>Default Value</th>
	 * </tr>
	 * <tr>
	 * <th>Require that it not match the {@link User}'s username or system id
	 * <th>{@link OpenmrsConstants#GP_PASSWORD_CANNOT_MATCH_USERNAME_OR_SYSTEMID}</th>
	 * <th>true</th>
	 * </tr>
	 * <tr>
	 * <th>Require a minimum length
	 * <th>{@link OpenmrsConstants#GP_PASSWORD_MINIMUM_LENGTH}</th>
	 * <th>8</th>
	 * </tr>
	 * <tr>
	 * <th>Require both an upper and lower case character
	 * <th>{@link OpenmrsConstants#GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE}</th>
	 * <th>true</th>
	 * </tr>
	 * <tr>
	 * <th>Require at least one numeric character
	 * <th>{@link OpenmrsConstants#GP_PASSWORD_REQUIRES_DIGIT}</th>
	 * <th>true</th>
	 * </tr>
	 * <tr>
	 * <th>Require at least one non-numeric character
	 * <th>{@link OpenmrsConstants#GP_PASSWORD_REQUIRES_NON_DIGIT}</th>
	 * <th>true</th>
	 * </tr>
	 * <tr>
	 * <th>Require a match on the specified regular expression
	 * <th>{@link OpenmrsConstants#GP_PASSWORD_CUSTOM_REGEX}</th>
	 * <th>null</th>
	 * </tr>
	 * </table>
	 * 
	 * @param username user name of the user with password to validated
	 * @param password string that will be validated
	 * @param systemId system id of the user with password to be validated
	 * @throws PasswordException
	 * @since 1.5
	 * <strong>Should</strong> fail with short password by default
	 * <strong>Should</strong> fail with short password if not allowed
	 * <strong>Should</strong> pass with short password if allowed
	 * <strong>Should</strong> fail with digit only password by default
	 * <strong>Should</strong> fail with digit only password if not allowed
	 * <strong>Should</strong> pass with digit only password if allowed
	 * <strong>Should</strong> fail with char only password by default
	 * <strong>Should</strong> fail with char only password if not allowed
	 * <strong>Should</strong> pass with char only password if allowed
	 * <strong>Should</strong> fail without both upper and lower case password by default
	 * <strong>Should</strong> fail without both upper and lower case password if not allowed
	 * <strong>Should</strong> pass without both upper and lower case password if allowed
	 * <strong>Should</strong> fail with password equals to user name by default
	 * <strong>Should</strong> fail with password equals to user name if not allowed
	 * <strong>Should</strong> pass with password equals to user name if allowed
	 * <strong>Should</strong> fail with password equals to system id by default
	 * <strong>Should</strong> fail with password equals to system id if not allowed
	 * <strong>Should</strong> pass with password equals to system id if allowed
	 * <strong>Should</strong> fail with password not matching configured regex
	 * <strong>Should</strong> pass with password matching configured regex
	 * <strong>Should</strong> allow password to contain non alphanumeric characters
	 * <strong>Should</strong> allow password to contain white spaces
	 * <strong>Should</strong> still work without an open session
	 */
	public static void validatePassword(String username, String password, String systemId) throws PasswordException {
		
		// default values for all of the global properties
		String userGp = "true";
		String lengthGp = "8";
		String caseGp = "true";
		String digitGp = "true";
		String nonDigitGp = "true";
		String regexGp = null;
		AdministrationService svc = null;
		
		try {
			svc = Context.getAdministrationService();
		}
		catch (APIException apiEx) {
			// if a service isn't available, fail quietly and just do the
			// defaults
			log.debug("Unable to get global properties", apiEx);
		}
		
		if (svc != null && Context.isSessionOpen()) {
			// (the session won't be open here to allow for the unit test to
			// fake not having the admin service available)
			userGp = svc.getGlobalProperty(OpenmrsConstants.GP_PASSWORD_CANNOT_MATCH_USERNAME_OR_SYSTEMID, userGp);
			lengthGp = svc.getGlobalProperty(OpenmrsConstants.GP_PASSWORD_MINIMUM_LENGTH, lengthGp);
			caseGp = svc.getGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE, caseGp);
			digitGp = svc.getGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_DIGIT, digitGp);
			nonDigitGp = svc.getGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_NON_DIGIT, nonDigitGp);
			regexGp = svc.getGlobalProperty(OpenmrsConstants.GP_PASSWORD_CUSTOM_REGEX, regexGp);
		}
		
		if (password == null) {
			throw new WeakPasswordException();
		}
		
		if ("true".equals(userGp) && (password.equals(username) || password.equals(systemId))) {
			throw new WeakPasswordException();
		}
		
		if (StringUtils.isNotEmpty(lengthGp)) {
			try {
				int minLength = Integer.parseInt(lengthGp);
				if (password.length() < minLength) {
					throw new ShortPasswordException(OpenmrsStringUtil.getMessage("error.password.length", lengthGp));
				}
			}
			catch (NumberFormatException nfe) {
				log.warn(
				    "Error in global property <" + OpenmrsConstants.GP_PASSWORD_MINIMUM_LENGTH + "> must be an Integer");
			}
		}
		
		if ("true".equals(caseGp) && !OpenmrsStringUtil.containsUpperAndLowerCase(password)) {
			throw new InvalidCharactersPasswordException(OpenmrsStringUtil.getMessage("error.password.requireMixedCase"));
		}
		
		if ("true".equals(digitGp) && !OpenmrsStringUtil.containsDigit(password)) {
			throw new InvalidCharactersPasswordException(OpenmrsStringUtil.getMessage("error.password.requireNumber"));
		}
		
		if ("true".equals(nonDigitGp) && OpenmrsStringUtil.containsOnlyDigits(password)) {
			throw new InvalidCharactersPasswordException(OpenmrsStringUtil.getMessage("error.password.requireLetter"));
		}
		
		if (StringUtils.isNotEmpty(regexGp)) {
			try {
				Pattern pattern = Pattern.compile(regexGp);
				Matcher matcher = pattern.matcher(password);
				if (!matcher.matches()) {
					throw new InvalidCharactersPasswordException(OpenmrsStringUtil.getMessage("error.password.different"));
				}
			}
			catch (PatternSyntaxException pse) {
				log.warn("Invalid regex of " + regexGp + " defined in global property <"
				        + OpenmrsConstants.GP_PASSWORD_CUSTOM_REGEX + ">.");
			}
		}
	}

	/**
	 * Convert a stack trace into a shortened version for easier viewing and data storage, excluding
	 * those lines we are least concerned with; should average about 60% reduction in stack trace
	 * length
	 * 
	 * @param stackTrace original stack trace from an error
	 * @return shortened stack trace
	 * <strong>Should</strong> return null if stackTrace is null
	 * <strong>Should</strong> remove springframework and reflection related lines
	 * @since 1.7
	 */
	public static String shortenedStackTrace(String stackTrace) {
		if (stackTrace == null) {
			return null;
		}
		
		List<String> results = new ArrayList<>();
		final Pattern exclude = Pattern.compile("(org.springframework.|java.lang.reflect.Method.invoke|sun.reflect.)");
		boolean found = false;
		
		for (String line : stackTrace.split("\n")) {
			Matcher m = exclude.matcher(line);
			if (m.find()) {
				found = true;
			} else {
				if (found) {
					found = false;
					results.add("\tat [ignored] ...");
				}
				results.add(line);
			}
		}
		
		return StringUtils.join(results, "\n");
	}


	/**
	 * Get declared field names of a class
	 * 
	 * @param clazz
	 * @return
	 */
	public static Set<String> getDeclaredFields(Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
	}
	
}
