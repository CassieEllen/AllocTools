package com.cenicol.mainframe.alloc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ConfigurationNodeVisitor;

/**
 * Provides a ConfigurationNodeVisitor that populates a variable map.
 * 
 * @author Cassie Nicol
 *
 */
public class TestNodeVisitor implements ConfigurationNodeVisitor {

	/** The variable map */
	Map<String, String> values = new HashMap<String, String>();
	
	/** The last name attribute read */
	private String name;
	
	/** The last value attribute read */
	private String value;
	
	
	@Override
	public void visitBeforeChildren(ConfigurationNode node) {
		if( ! node.isAttribute()) {
			return;
		}
		switch(node.getName()) {
		case "name":
			this.name = (String) node.getValue();
			break;
		case "value":
			this.value = (String) node.getValue();
			break;
		}
		//System.out.println("\tB:" + node.getName() + "=" + node.getValue());
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.configuration.tree.ConfigurationNodeVisitor#visitAfterChildren(org.apache.commons.configuration.tree.ConfigurationNode)
	 */
	@Override
	public void visitAfterChildren(ConfigurationNode node) {
		if(node.isAttribute()) {
			return;
		}
		//System.out.println("\tA:" + name + "=" + value);
		assignValue();
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.configuration.tree.ConfigurationNodeVisitor#terminate()
	 */
	@Override
	public boolean terminate() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Assign a value into the values map if it is available. <br/>
	 * Then resets the name and value members to null.
	 * 
	 */
	private void assignValue() {
		if(null != name && null != value) {
			values.put(name, value);
		}
		name = null;
		value = null;
	}

	/**
	 * Get the values map.
	 * 
	 * @return the values
	 */
	public Map<String, String> getValues() {
		return values;
	}

	/**
	 * Set the initial values map.
	 * 
	 * @param values the values to set
	 */
	public void setValues(Map<String, String> values) {
		this.values = values;
	}

}
