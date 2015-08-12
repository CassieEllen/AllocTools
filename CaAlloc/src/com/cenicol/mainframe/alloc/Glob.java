package com.cenicol.mainframe.alloc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * 
 * <p>Patterns are formed by specifying non-blank characters. The patterns are delimited by blanks, 
not quotes. Any ASR constant that is not numeric (that does not begin with digits 0 through 9), or a string (that is not contained within single quotation marks), is considered a pattern, even if it does not contain pattern-matching characters. A pattern without pattern-matching characters works like a string constant, but goes through a slightly longer code path.
Patterns can contain the following special characters:</p>

<dl>
<dt>percent sign (%)</dt>
  <dd>Takes the place of any single character during a comparison.</dd>
<dt>single asterisk (*)</dt>
  <dd>Takes the place of any number of characters (including 0) during a comparison.</dd>
<dt>double asterisk (**)</dt>
  <dd>Takes the place of any number of data set name nodes (including 0) during data set name comparisons.</dd>
<dt>question mark (?)</dt>
  <dd>Takes the place of any single numeric digit (in the range '0' through '9') during a comparison.</dd>
</dl>
 *
 * @author Cassie Nicol
 *
 */
public class Glob {
	/**
	 * Define the pattern matching characters. 
	 */
	static final String globChars = "[%?*]";

	/**
	 * The pattern matching charactes compiled into a regex Pattern.
	 */
	static final Pattern globCharsPat = Pattern.compile(globChars);
	
	/**
	 * Glob string
	 */
	String s = null;
	
	/**
	 * Glob Pattern
	 */
	Pattern p = null;
	
	/**
	 * Checks if <i>text</i> is found in <i>glob</i>
	 * 
	 * @param glob
	 *            The GLOB to check against.
	 * @param text
	 *            The text string to be matched.
	 * @return true if text is matched by glob.
	 */
	static boolean matches(String glob, String text) {
		Pattern p = getGlobPattern(glob);
		Matcher m = p.matcher(text);
		return m.find();
	}
	
	/**
	 * Converts a string containing GLOB characters into a regex Pattern. 
	 * @param text The text string to convert
	 * @return The regular expression Pattern. 
	 */
	static Pattern getGlobPattern(String text) {
		Matcher m = globCharsPat.matcher(text);
		if( ! m.find()) {
			return Glob.getStringPattern(text);
		}
		String value = "^" + text.replaceAll("\\?", "\\p{Digit}");
		value = value.replaceAll("%", ".");
		value = value.replaceAll("\\*{2}", ".*");
		value = value.replaceAll("\\*", ".{0,8}");
		return Pattern.compile(value);
	}
	
	/** Convert a string (without GLOB characters) to a regular expression Pattern.
	 * @param text The text string to convert.
	 * @return The regular expression Pattern.
	 */
	static Pattern getStringPattern(String text) {
		return Pattern.compile(text);
	}
	
	public Glob(String s) {
		this.s = s;
		this.p = Glob.getGlobPattern(s);
	}
	
	/**
	 * Checks if <i>text</i> is found in this GLOB
	 * 
	 * @param text The text string to check.
	 * @return true if text is matched by glob.
	 */
	public boolean matches(String text) {
		Matcher m = p.matcher(text);
		return m.find();
	}
	
}
