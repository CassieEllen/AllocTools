/**
 * @author Cassie Nicol
 * 
 */

package com.cenicol.mainframe.alloc;

/**
 * TokenStreamStack Interface
 * 
 * @author Cassie Nicol
 *
 */
public interface TokenStreamStackI {
	
	/**
	 * Get the instance of TokenStreamStack.
	 * @return the instance of TokenStackStream.
	 */
	public static TokenStreamStack instance()
	{
		return TokenStreamStack.tss;
	}


	/**
	 * Converts a filename to a token stream and pushes it onto the stack.
	 * 
	 * @param filename The filename of the token file.
	 */
	public void pushTS(String filename);

}
