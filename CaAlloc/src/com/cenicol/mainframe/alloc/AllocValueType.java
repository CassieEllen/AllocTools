/**
 * 
 * @author Cassie Nicol
 *
 */

package com.cenicol.mainframe.alloc;

/**
 * Enumerates the possible types contained by class {@link AllocValue}.
 * @author Cassie Nicol
 *
 */
public enum AllocValueType {
	/** Integer type */
    INTEGER,
    /** String type */
    STRING, 
    /** GLOB type: {@link com.cenicol.mainframe.alloc.Glob GLOB} */
    GLOB, 
    /** List type */
    LIST, 
    /** Filtlist type: {@link com.cenicol.mainframe.alloc.Filtlist Filtlist} */
    FILTLIST, 
    /** Boolean type */
    BOOL
}

