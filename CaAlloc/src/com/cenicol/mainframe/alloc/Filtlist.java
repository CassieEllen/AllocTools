/**
 * 
 * @author Cassie Nicol
 *
 */

package com.cenicol.mainframe.alloc;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements the CaAlloc FILTLIST type. 
 * 
 * @author Cassie Nicol
 *
 */
public class Filtlist {

	AllocValue include = null;
	AllocValue exclude = null;
	List<Pattern> includePatterns = null;
	List<Pattern> excludePatterns = null;

	private Log log = LogFactory.getLog(AllocValue.class);


	Filtlist(AllocValue include, AllocValue exclude)
	{
		log.trace("Filtlist.ctor");
		//log.info("Filtlist.ctor");
		this.include = include;
		this.exclude = exclude;
	}

	/**
	 * Builds a list of Patterns to match against. 
	 * 
	 * @param values A list of AllocValues to be converted into Patterns. 
	 * @return A list of 
	 */
	List<Pattern> buildMatches(List<AllocValue> values) {
		List<Pattern> matches = new Vector<Pattern>();
		for(AllocValue v : values ) {
			switch(v.type) {
			case STRING:
				matches.add(Pattern.compile(v.getString()));
				break;
			case GLOB:
				matches.add(Glob.getGlobPattern(v.getGlob()));
				break;
			default:
				throw new RuntimeException("Invalid type: " + v.type.name());
			}
		}
		return matches;
	}

	/**
	 * Build include regex match strings.
	 */
	void buildIncludeMatches() {
		this.includePatterns = buildMatches(include.getList());
	}

	/**
	 * Build exclude regex match strings.
	 */
	void buildExcludeMatches() {
		if(null != exclude) {
			this.excludePatterns = buildMatches(exclude.getList());
		}
	}

	/**
	 * Match a string against this Filtlist.
	 * 
	 * @param value The string value to be matched.
	 * @return true if the value matches this Filtlist.
	 */
	boolean match(AllocValue value) {
		if(null == includePatterns) {
			buildIncludeMatches();
		}
		if(null == excludePatterns) {
			buildExcludeMatches();
		}
		List<String> found = new Vector<String>();
		String t = value.getString();

		for(Pattern p : includePatterns) {
			Matcher m = p.matcher(t);
			if(m.find()) {
				//System.out.println("Include: " + t + " is matched by " + p.toString());
				found.add(t);
			}
		}
		
		if(null != excludePatterns) {
			for(Pattern p : excludePatterns) {
				Matcher m = p.matcher(t);
				if(m.find()) {
					//System.out.println("Exclude: " + t + " is matched by " + p.toString());
					if(found.contains(t)) {
						found.remove(t);
					}
				}
			}
		}
		
		return found.size() > 0;
	}

	/**
	 * @return the include
	 */
	public AllocValue getInclude() {
		return include;
	}

	/**
	 * @param include the include to set
	 */
	public void setInclude(AllocValue include) {
		this.include = include;
	}

	/**
	 * @return the exclude
	 */
	public AllocValue getExclude() {
		return exclude;
	}

	/**
	 * @param exclude the exclude to set
	 */
	public void setExclude(AllocValue exclude) {
		this.exclude = exclude;
	}

	/**
	 * Check this filtlist to see if it contains any exclude patterns.
	 * 
	 * @return true if this Filtlist has a list of exclude patterns.
	 */
	public boolean hasExclude() {
		return this.exclude != null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String v = "include(" + this.include + ")";
		if(this.hasExclude() && this.getExclude().getList().size() > 0) {
			v += " exclude(" + this.exclude + ")";
		}
		return v;
	}
}
