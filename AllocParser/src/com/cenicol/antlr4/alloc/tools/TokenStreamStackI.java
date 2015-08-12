package com.cenicol.antlr4.alloc.tools;
/**
 * 
 */

/**
 * TokenStreamStack Interface
 * 
 * @author Cassie Nicol
 *
 */
public interface TokenStreamStackI {
	
	/**
	 * Converts a filename to a token stream and pushes it onto the stack.
	 * 
	 * @param filename The name of the token file.
	 */
	public void pushTS(String filename);

}
