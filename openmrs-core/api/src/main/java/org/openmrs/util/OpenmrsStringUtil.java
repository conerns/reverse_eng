package org.openmrs.util;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.springframework.context.NoSuchMessageException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenmrsStringUtil {
	public static boolean isStringInArray(String str, String[] arr) {
		if (str != null && arr != null) {
			for (String anArr : arr) {
				if (str.equals(anArr)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tests if the given String starts with any of the specified prefixes
	 * 
	 * @param str the string to test
	 * @param prefixes an array of prefixes to test against
	 * @return true if the String starts with any of the specified prefixes, otherwise false.
	 */
	public static boolean stringStartsWith(String str, String[] prefixes) {
		for (String prefix : prefixes) {
			if (StringUtils.startsWith(str, prefix)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @param test the string to test
	 * @return true if the passed string contains both upper and lower case characters
	 * <strong>Should</strong> return true if string contains upper and lower case
	 * <strong>Should</strong> return false if string does not contain lower case characters
	 * <strong>Should</strong> return false if string does not contain upper case characters
	 */
	public static boolean containsUpperAndLowerCase(String test) {
		if (test != null) {
			Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])[\\w|\\W]*$");
			Matcher matcher = pattern.matcher(test);
			return matcher.matches();
		}
		return false;
	}

	/**
	 * @param test the string to test
	 * @return true if the passed string contains only numeric characters
	 * <strong>Should</strong> return true if string contains only digits
	 * <strong>Should</strong> return false if string contains any non-digits
	 */
	public static boolean containsOnlyDigits(String test) {
		if (test != null) {
			for (char c : test.toCharArray()) {
				if (!Character.isDigit(c)) {
					return false;
				}
			}
		}
		return StringUtils.isNotEmpty(test);
	}

	/**
	 * @param test the string to test
	 * @return true if the passed string contains any numeric characters
	 * <strong>Should</strong> return true if string contains any digits
	 * <strong>Should</strong> return false if string contains no digits
	 */
	public static boolean containsDigit(String test) {
		if (test != null) {
			for (char c : test.toCharArray()) {
				if (Character.isDigit(c)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Performs a case insensitive Comparison of two strings taking care of null values
	 * 
	 * @param s1 the string to compare
	 * @param s2 the string to compare
	 * @return true if strings are equal (ignoring case)
	 * <strong>Should</strong> return false if only one of the strings is null
	 * <strong>Should</strong> be case insensitive
	 * @since 1.8
	 */
	public static boolean nullSafeEqualsIgnoreCase(String s1, String s2) {
		if (s1 == null) {
			return s2 == null;
		} else if (s2 == null) {
			return false;
		}
		
		return s1.equalsIgnoreCase(s2);
	}

	/**
	 * Converts a collection to a String with a specified separator between all elements
	 * 
	 * @param c Collection to be joined
	 * @param separator string to put between all elements
	 * @return a String representing the toString() of all elements in c, separated by separator
	 * @deprecated as of 2.2 use Java's {@link String#join} or Apache Commons StringUtils.join for iterables which do not extend {@link CharSequence}
	 */
	@Deprecated
	public static <E> String join(Collection<E> c, String separator) {
		if (c == null) {
			return "";
		}
		
		StringBuilder ret = new StringBuilder();
		for (Iterator<E> i = c.iterator(); i.hasNext();) {
			ret.append(i.next());
			if (i.hasNext()) {
				ret.append(separator);
			}
		}
		return ret.toString();
	}

	/**
	 * Utility method for getting the translation for the passed code
	 * 
	 * @param code the message key to lookup
	 * @param args the replacement values for the translation string
	 * @return the message, or if not found, the code
	 */
	public static String getMessage(String code, Object... args) {
		Locale l = Context.getLocale();
		try {
			String translation = Context.getMessageSourceService().getMessage(code, args, l);
			if (translation != null) {
				return translation;
			}
		}
		catch (NoSuchMessageException ignored) {
		}
		catch (APIException apiEx) {
			// in case the services aren't set up yet
			return code;
		}
		return code;
	}

	/**
	 * Takes a String like "size=compact|order=date" and returns a Map&lt;String,String&gt; from the
	 * keys to the values.
	 * 
	 * @param paramList <code>String</code> with a list of parameters
	 * @return Map&lt;String, String&gt; of the parameters passed
	 */
	public static Map<String, String> parseParameterList(String paramList) {
		Map<String, String> ret = new HashMap<>();
		if (paramList != null && paramList.length() > 0) {
			String[] args = paramList.split("\\|");
			for (String s : args) {
				int ind = s.indexOf('=');
				if (ind <= 0) {
					throw new IllegalArgumentException(
					        "Misformed argument in dynamic page specification string: '" + s + "' is not 'key=value'.");
				}
				String name = s.substring(0, ind);
				String value = s.substring(ind + 1);
				ret.put(name, value);
			}
		}
		return ret;
	}
}
