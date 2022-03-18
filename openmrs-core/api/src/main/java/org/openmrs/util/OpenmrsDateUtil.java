package org.openmrs.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class OpenmrsDateUtil {

	private static Map<Locale, SimpleDateFormat> dateFormatCache = new HashMap<>();

	private static Map<Locale, SimpleDateFormat> timeFormatCache = new HashMap<>();

	/**
	 * Gets the date having the last millisecond of a given day. Meaning that the hours, seconds,
	 * and milliseconds are the latest possible for that day.
	 * 
	 * @param day the day.
	 * @return the date with the last millisecond of the day.
	 */
	public static Date getLastMomentOfDay(Date day) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(day);
		calender.set(Calendar.HOUR_OF_DAY, 23);
		calender.set(Calendar.MINUTE, 59);
		calender.set(Calendar.SECOND, 59);
		calender.set(Calendar.MILLISECOND, 999);
		
		return calender.getTime();
	}

	/**
	 * Return a date that is the same day as the passed in date, but the hours and seconds are the
	 * earliest possible for that day.
	 * 
	 * @param date date to adjust
	 * @return a date that is the first possible time in the day
	 * @since 1.9
	 */
	public static Date firstSecondOfDay(Date date) {
		if (date == null) {
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();
	}

	public static Date safeDate(Date d1) {
		return new Date(d1.getTime());
	}

	public static Date fromDateHelper(Date comparisonDate, Integer withinLastDays, Integer withinLastMonths,
									  Integer untilDaysAgo, Integer untilMonthsAgo, Date sinceDate, Date untilDate) {
		
		Date ret = null;
		if (withinLastDays != null || withinLastMonths != null) {
			Calendar gc = Calendar.getInstance();
			gc.setTime(comparisonDate != null ? comparisonDate : new Date());
			if (withinLastDays != null) {
				gc.add(Calendar.DAY_OF_MONTH, -withinLastDays);
			}
			if (withinLastMonths != null) {
				gc.add(Calendar.MONTH, -withinLastMonths);
			}
			ret = gc.getTime();
		}
		if (sinceDate != null && (ret == null || sinceDate.after(ret))) {
			ret = sinceDate;
		}
		return ret;
	}

	public static Date toDateHelper(Date comparisonDate, Integer withinLastDays, Integer withinLastMonths,
									Integer untilDaysAgo, Integer untilMonthsAgo, Date sinceDate, Date untilDate) {
		
		Date ret = null;
		if (untilDaysAgo != null || untilMonthsAgo != null) {
			Calendar gc = Calendar.getInstance();
			gc.setTime(comparisonDate != null ? comparisonDate : new Date());
			if (untilDaysAgo != null) {
				gc.add(Calendar.DAY_OF_MONTH, -untilDaysAgo);
			}
			if (untilMonthsAgo != null) {
				gc.add(Calendar.MONTH, -untilMonthsAgo);
			}
			ret = gc.getTime();
		}
		if (untilDate != null && (ret == null || untilDate.before(ret))) {
			ret = untilDate;
		}
		return ret;
	}

	/**
	 * Get the current user's date format Will look similar to "mm-dd-yyyy". Depends on user's
	 * locale.
	 * 
	 * @return a simple date format
	 * <strong>Should</strong> return a pattern with four y characters in it
	 * <strong>Should</strong> not allow the returned SimpleDateFormat to be modified
	 * @since 1.5
	 */
	public static SimpleDateFormat getDateFormat(Locale locale) {
		if (dateFormatCache.containsKey(locale)) {
			return (SimpleDateFormat) dateFormatCache.get(locale).clone();
		}
		
		// note that we are using the custom OpenmrsDateFormat class here which prevents erroneous parsing of 2-digit years
		SimpleDateFormat sdf = new OpenmrsDateFormat((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale),
		        locale);
		String pattern = sdf.toPattern();
		
		if (!pattern.contains("yyyy")) {
			// otherwise, change the pattern to be a four digit year
			String regex = "yy";
			if (!pattern.contains("yy")) {
				//Java 11 has dd/MM/y instead of dd/MM/yy
				regex = "y";
			}
			pattern = pattern.replaceFirst(regex, "yyyy");
			sdf.applyPattern(pattern);
		}
		if (!pattern.contains("MM")) {
			// change the pattern to be a two digit month
			pattern = pattern.replaceFirst("M", "MM");
			sdf.applyPattern(pattern);
		}
		if (!pattern.contains("dd")) {
			// change the pattern to be a two digit day
			pattern = pattern.replaceFirst("d", "dd");
			sdf.applyPattern(pattern);
		}
		
		dateFormatCache.put(locale, sdf);
		
		return (SimpleDateFormat) sdf.clone();
	}

	/**
	 * Get the current user's time format Will look similar to "hh:mm a". Depends on user's locale.
	 * 
	 * @return a simple time format
	 * <strong>Should</strong> return a pattern with two h characters in it
	 * <strong>Should</strong> not allow the returned SimpleDateFormat to be modified
	 * @since 1.9
	 */
	public static SimpleDateFormat getTimeFormat(Locale locale) {
		if (timeFormatCache.containsKey(locale)) {
			return (SimpleDateFormat) timeFormatCache.get(locale).clone();
		}
		
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		String pattern = sdf.toPattern();
		
		if (!(pattern.contains("hh") || pattern.contains("HH"))) {
			// otherwise, change the pattern to be a two digit hour
			pattern = pattern.replaceFirst("h", "hh").replaceFirst("H", "HH");
			sdf.applyPattern(pattern);
		}
		
		timeFormatCache.put(locale, sdf);
		
		return (SimpleDateFormat) sdf.clone();
	}

	/**
	 * Get the current user's datetime format Will look similar to "mm-dd-yyyy hh:mm a". Depends on
	 * user's locale.
	 * 
	 * @return a simple date format
	 * <strong>Should</strong> return a pattern with four y characters and two h characters in it
	 * <strong>Should</strong> not allow the returned SimpleDateFormat to be modified
	 * @since 1.9
	 */
	public static SimpleDateFormat getDateTimeFormat(Locale locale) {
		SimpleDateFormat dateFormat;
		SimpleDateFormat timeFormat;
		
		dateFormat = getDateFormat(locale);
		timeFormat = getTimeFormat(locale);
		
		String pattern = dateFormat.toPattern() + " " + timeFormat.toPattern();
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(pattern);
		return sdf;
	}

	/**
	 * Checks if the passed in date's day of the year is the one that comes immediately before that
	 * of the current date
	 * 
	 * @param date the date to check
	 * @since 1.9
	 * @return true if the date comes immediately before the current date otherwise false
	 */
	public static boolean isYesterday(Date date) {
		if (date == null) {
			return false;
		}
		
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		
		return (c1.get(Calendar.ERA) == c2.get(Calendar.ERA) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
		        && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR));
	}
}
