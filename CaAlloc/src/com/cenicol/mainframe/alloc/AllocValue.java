/**
 * 
 * @author Cassie Nicol
 *
 */

package com.cenicol.mainframe.alloc;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a value container for the parser.
 * 
 * <p>CaAlloc values are {@link AllocValueType}</p>
 * <ul><li>{@link AllocValueType#BOOL BOOL}</li>
 * <li>{@link AllocValueType#INTEGER INTEGER}</li>
 * <li>{@link AllocValueType#STRING STRING}</li>
 * <li>{@link AllocValueType#GLOB GLOB}</li>
 * <li>{@link AllocValueType#LIST LIST}</li>
 * <li>{@link AllocValueType#FILTLIST FILTLIST}</li></ul>
 * 
 * @author Cassie Nicol
 *
 */
public class AllocValue
{
	/** value type {@link AllocValueType} */
	protected AllocValueType type;
	
	/** BOOL value {@link AllocValueType#BOOL} */
	protected boolean b;              
	
	/** INTEGER value */
	protected int i;    
	
	/** STRING value */
	protected String s; 
	
	/** GLOB value */
	protected Glob g;
	
	/** LIST value */
	protected List<AllocValue> l;
	
	/** FILTLIST value */
	protected Filtlist f;          
	
	 /** ID name - set when an ID is replaced by it's value. */
	protected String name;           

	/** Class logger */
	private Log log = LogFactory.getLog(AllocValue.class);

	//--------------------------------------------------------------------------
	// Constructors
	//--------------------------------------------------------------------------

	/**
	 * Default Constructor: create an empty string value. 
	 */
	AllocValue() {
		log.trace("AllocValue()");
		type = AllocValueType.STRING;
		s = "empty";
	}

	/**
	 * Create a boolean variable.
	 * 
	 * @param v Initial boolean value.
	 */
	AllocValue(boolean v)
	{
		log.trace("AllocValue(boolean)");
		type = AllocValueType.BOOL;
		b = v;
	}

	/**
	 * Create an integer value.
	 * 
	 * @param v Initial integer value.
	 */
	AllocValue(int v) {
		log.trace("AllocValue(int)");
		type = AllocValueType.INTEGER;
		i = v;
	}
	
	/** Create a string value.
	 * 
	 * @param v Initial string value. 
	 */
	AllocValue(String v)
	{
		log.trace("AllocValue(String)");
		setString(v);
	}

	/** Create a Glob value
	 * 
	 * @param v
	 */
	AllocValue(Glob v)
	{
		log.trace("AllocValue(Glob)");
		setGlob(v);
	}
	
	/**
	 * @param typeOf
	 * @param value
	 */
	AllocValue(AllocValue value) {
		log.trace("AllocValue(AllocValue)");
		type = AllocValueType.LIST;
		l = new Vector<AllocValue>();
		l.add(value);
	}
	
	/**
	 * Appends a value to the value {@link #l l}.
	 * 
	 * @param value to be appended to the end of the list <i>l</i>.
	 */
	void append(AllocValue value) {
		l.add(value);
	}

	/**
	 * Instantiates AllocValue with a Filtlist.
	 * 
	 * @param f is assigned to {@link AllocValue#f f}
	 */
	AllocValue(Filtlist f) {
		log.trace("AllocValue(Filtlist)");
		type = AllocValueType.FILTLIST;
		this.f = f;
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * @param v
	 */
	void setBool(boolean v)
	{
		type = AllocValueType.BOOL;
		b = v;
	}

	/**
	 * @param v
	 */
	void setInt(int v)
	{
		type = AllocValueType.INTEGER;
		i = v;
	}

	/**
	 * @param v
	 */
	void setString(String v)
	{
		type = AllocValueType.STRING;
		String n = unEscapeString(v);
		s = n;
	}

	/**
	 * @param v
	 */
	void setGlob(String v)
	{
		type = AllocValueType.GLOB;
		s = v;
	}

	/**
	 * @param v
	 */
	void setGlob(Glob v)
	{
		type = AllocValueType.GLOB;
		g = v;
	}

	/**
	 * @param n
	 */
	void setName(String n) {
		name = n;
	}
	
	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * @return
	 */
	boolean getBool() {
		switch(type) {
		case BOOL:
			return b;
		case INTEGER:
			return i != 0;
		default:
			throw new RuntimeException("Not a bool.");
		}
	}

	/**
	 * @return
	 */
	int getInt() {
		if(type != AllocValueType.INTEGER) {
			throw new RuntimeException("Not an int.");
		}
		return i;
	}

	/**
	 * Get the string value.
	 * 
	 * @return The string value.
	 */
	String getString() {
		if(type != AllocValueType.STRING) {
			throw new RuntimeException("Not a string.");
		}
		return s;
	}

	/**
	 * Get the string value of a GLOB
	 * 
	 * @return The GLOB String value;
	 */
	String getGlob() {
		if(type != AllocValueType.GLOB) {
			throw new RuntimeException("Not a glob.");
		}
		return s;
	}


	 /**
	 * @return
	 */
	List<AllocValue> getList() {
		if(null == this.l) {
			throw new RuntimeException("l is null");
		}
		return l;
	}

	 /*
	  * Get the FILTLIST value
	  * 
	  * @return The FILTLIST value;
	  */
	 Filtlist getFiltlist() {
		 if(this.type == AllocValueType.FILTLIST) {
			 return this.f;
		 }
		 throw new RuntimeException("Not a Filtlist");
	 }
	 
	 /**
	 * @return
	 */
	String getName() {
		 return name;
	 }

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue or(AllocValue rhs) {
		AllocValue result = new AllocValue(false);
		switch(type) {
		case BOOL:
			result.setBool(getBool() || rhs.getBool());
			break;
		case INTEGER:
			result.setBool(getBool() || rhs.getBool());
			break;
		case STRING:
			break;
		case GLOB:
			break;
		default:
			throw new RuntimeException("Invalid type: " + type);
		}

		return result;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue and(AllocValue rhs) {
		AllocValue result = new AllocValue(false);
		switch(type) {
		case BOOL:
			result.setBool(getBool() && rhs.getBool());
			break;
		case INTEGER:
			result.setBool(getBool() && rhs.getBool());
			break;
		case STRING:
		case GLOB:
		default:
			throw new RuntimeException("Invalid type: " + type);
		}
		return result;
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue add(AllocValue rhs) {
		AllocValue result = new AllocValue(getInt() + rhs.getInt());
		result.write();
		return result;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue sub(AllocValue rhs) {
		AllocValue result = new AllocValue(getInt() - rhs.getInt());
		return result;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue mul(AllocValue rhs) {
		AllocValue result = new AllocValue(getInt() * rhs.getInt());
		return result;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue div(AllocValue rhs) {
		AllocValue result = new AllocValue(getInt() / rhs.getInt());
		return result;
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue eq(AllocValue rhs) {
		AllocValue result = new AllocValue(false);
		switch(type) {
		case BOOL:
			result.setBool(getBool() == rhs.getBool());
			break;
		case INTEGER:
			result.setBool(getInt() == rhs.getInt());
			break;
		case STRING:
			switch(rhs.type) {
			case STRING:
				result.setBool(getString().equals(rhs.getString()));
				break;
			case GLOB:
				result.setBool(matchesGlob(rhs));
				break;
			case FILTLIST:
				result.setBool( rhs.getFiltlist().match(this) );
				break;
			case INTEGER:
				result.setBool(Integer.parseInt(getString()) == rhs.getInt() );
				break;
			default:
				throw new RuntimeException("Invalid type:" + type);	
			}
			break;
		case GLOB:
			result.setBool(getGlob().equals(rhs.getGlob()));
			break;
		case FILTLIST:
			boolean retval = rhs.getFiltlist().match(rhs);
			result.setBool(retval);
		default:
			throw new RuntimeException("Invalid type: " + type);
		}

		return result;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue ne(AllocValue rhs) {
		AllocValue value = eq(rhs);
		value.setBool(! value.getBool());
		return value;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue lt(AllocValue rhs) {
		AllocValue value = new AllocValue();
		switch(type) {
		case INTEGER:
			value.setBool(i < rhs.i);
			break;
		case STRING:
			value.setBool( s.compareTo(rhs.s) < 0 );
			break;
		case BOOL:
		case GLOB:
		case FILTLIST:
		default:
			throw new RuntimeException("not implemented.");
		}
		return value;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue gt(AllocValue rhs) {
		AllocValue value = new AllocValue();
		switch(type) {
		case INTEGER:
			value.setBool(i > rhs.i);
			break;
		case STRING:
			switch(rhs.type) {
			case INTEGER:
				value.setBool(Integer.parseInt(s) > rhs.getInt());
				break;
			case STRING:
				value.setBool( s.compareTo(rhs.s) > 0 );
				break;
			default:
				throw new RuntimeException("not implemented.");
			}
			break;
		case BOOL:
		case GLOB:
		case FILTLIST:
		default:
			throw new RuntimeException("not implemented.");
		}
		return value;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue le(AllocValue rhs) {
		AllocValue value = new AllocValue();
		switch(type) {
		case INTEGER:
			value.setBool(i <= rhs.i);
			break;
		case STRING:
			value.setBool( s.compareTo(rhs.s) <= 0 );
			break;
		case BOOL:
		case GLOB:
		case FILTLIST:
		default:
			throw new RuntimeException("not implemented.");
		}
		return value;
	}

	/**
	 * @param rhs
	 * @return
	 */
	AllocValue ge(AllocValue rhs) {
		AllocValue value = new AllocValue();
		switch(type) {
		case INTEGER:
			value.setBool(i >= rhs.i);
			break;
		case STRING:
			value.setBool( s.compareTo(rhs.s) >= 0 );
			break;
		case BOOL:
		case GLOB:
		case FILTLIST:
		default:
			throw new RuntimeException("not implemented.");
		}
		return value;
	}

	//--------------------------------------------------------------------------

	/**
	 * Checks 
	 * @param rhs The GLOB to compare against. 
	 * @return true if this string is found within the GLOB.
	 */
	boolean matchesGlob(AllocValue rhs)
	{
		if(this.type == AllocValueType.GLOB) {
			throw new RuntimeException("matchesGlob() not implemented for type " + this.type);
		}
		return Glob.matches(rhs.getGlob(), getString());
	}

	/**
	 * Convert a plain string to a string with escaped character.
	 * 
	 * @param v The string to be conferted.
	 * @return The converted string.
	 */
	static
	String escapeString(String v)
	{
		String a = v.replace("&", "&&");
		return a.replace("'", "''");
	}

	/**
	 * Convert escaped characters to 
	 * @param v The string to be converted.
	 * @return The converted value. 
	 */
	static
	String unEscapeString(String v)
	{
		String a = v.replace("&&", "&");
		a = a.replaceFirst("^''$", "");
		return a.replace("''", "'");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toString(false);
	}
	
	/**
	 * Returns a string representation of the contained value.
	 * 
	 * @param show Set to true to display the assigned variable ID.
	 * @return A text string for this value.
	 */
	public String toString(boolean show) {
		String v = "";
		if(show && null != name) {
		 	v += name + "{";
		}
		
		switch(type) {
		case BOOL:
			v += Boolean.toString(b);
			break;
		case INTEGER:
			v += Integer.toString(i);
			break;
		case STRING:
			v += "'" + s + "'";
			break;
		case GLOB:
			v += s;
			break;
		case LIST:
			v += l.toString();
			break;
		case FILTLIST:
			if(null == f) {
				System.out.println("FILTLIST is null");
			} else {
				v += f;
			}
			break;
		default:
			throw new RuntimeException("Invalid type: must be STRING or GLOB");
		}
		if(show && null != name) {
			v += "}";
		}
		return v;
	}

	/**
	 * Writes the contained value to System.out.println. 
	 */
	public void write() {
		System.out.println(this.toString());
	}


}
