package org.openmrs.util;

import org.openmrs.ConceptNumeric;

import java.util.ArrayList;
import java.util.List;

public class OpenmrsNumericUtil {
	
	public static Boolean isInNormalNumericRange(Float value, ConceptNumeric concept) {
		if (concept.getHiNormal() == null || concept.getLowNormal() == null) {
			return false;
		}
		return (value <= concept.getHiNormal() && value >= concept.getLowNormal());
	}

	public static Boolean isInCriticalNumericRange(Float value, ConceptNumeric concept) {
		if (concept.getHiCritical() == null || concept.getLowCritical() == null) {
			return false;
		}
		return (value <= concept.getHiCritical() && value >= concept.getLowCritical());
	}

	public static Boolean isInAbsoluteNumericRange(Float value, ConceptNumeric concept) {
		if (concept.getHiAbsolute() == null || concept.getLowAbsolute() == null) {
			return false;
		}
		return (value <= concept.getHiAbsolute() && value >= concept.getLowAbsolute());
	}

	public static Boolean isValidNumericValue(Float value, ConceptNumeric concept) {
		if (concept.getHiAbsolute() == null || concept.getLowAbsolute() == null) {
			return true;
		}
		return (value <= concept.getHiAbsolute() && value >= concept.getLowAbsolute());
	}

	public static List<Integer> delimitedStringToIntegerList(String delimitedString, String delimiter) {
		List<Integer> ret = new ArrayList<>();
		String[] tokens = delimitedString.split(delimiter);
		for (String token : tokens) {
			token = token.trim();
			if (token.length() != 0) {
				ret.add(Integer.valueOf(token));
			}
		}
		return ret;
	}

	/**
	 * This method converts the given Long value to an Integer. If the Long value will not fit in an
	 * Integer an exception is thrown
	 * 
	 * @param longValue the value to convert
	 * @return the long value in integer form.
	 * @throws IllegalArgumentException if the long value does not fit into an integer
	 */
	public static Integer convertToInteger(Long longValue) {
		if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(longValue + " cannot be cast to Integer without changing its value.");
		}
		return longValue.intValue();
	}
}
