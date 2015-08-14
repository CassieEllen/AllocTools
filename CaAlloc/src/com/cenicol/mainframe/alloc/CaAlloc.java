/**
 * @author Cassie Nicol
 */

package com.cenicol.mainframe.alloc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.cenicol.antlr4.alloc.parser.AllocParser;

/**
 * <p><a href="http://logging.apache.org/log4j/1.2/manual.html">log4j manual</a></p>
 * 
 * <p>Loggers may be assigned levels. The set of possible levels, that is:</p>
 * <ul><li>TRACE</li>
 * <li>DEBUG</li>
 * <li>INFO</li>
 * <li>WARN</li>
 * <li>ERROR</li>
 * <li>FATAL</li></ul>
 * 
 * @author Cassie Nicol
 *
 */
public class CaAlloc {
	
	/** The token stream stack must be static because it is used several levels and needs a
	 *  global way to access it.
	 */
	static TokenStreamStack tss = TokenStreamStack.instance();

	/** Class CaAlloc log file */
	static private Log log = LogFactory.getLog(CaAlloc.class.getName());

	/** program properties */
	Properties prop = new Properties();

	/** Script input filename */
	private String inputFilename = null;                        
	
	
	/** Config filename */
	private String configFilename = null;
	
	/** Debugging switch */
	private boolean debugging = false;

	/** Help text from cmd line */
	String helpText;


	/**
	 * @param tree
	 * @param eval
	 */
	private static void execScript(ParseTree tree, EvalVisitor eval) {
		System.out.println("\nRunning Script");
		System.out.println("--------------");
		// Execute the script
		try {
			eval.visit(tree);
		} catch(java.lang.RuntimeException e) {
			e.printStackTrace();
		} catch(Exception e) {
			System.out.println(e.getClass().getName());
			System.out.println(e.getMessage());
		}
		
		// Display variable results
		System.out.println("\nResults");
		System.out.println("-------");
		
		Set<String> keySet = eval.getVars().keySet();
		List<String>  keyList = new ArrayList<String>(keySet);
		//System.out.println("before:" + keyList);
		java.util.Collections.sort(keyList);
		//System.out.println("after:" + keyList);
		for(String key : keyList ) {
			System.out.print(key);
			if(key.length() >= 16) {
				System.out.print("\t");
			} else if(key.length() >= 8) {
				System.out.print("\t\t");				
			} else {
				System.out.print("\t\t\t");
			}
			System.out.println(eval.vars.get(key));
		}
	}

	/**
	 * @param config
	 * @param key
	 */
	static void getProperty(XMLConfiguration config, String key) {
		System.out.println("getProperty - " + key);
		Object prop = config.getProperty(key);
		if(null == prop) {
			System.out.println("\treturned null");
			return;
		}
		System.out.println("\t" + prop.getClass().getName());
		System.out.println("\t" + prop);
	}     

	/**
	 * Returns the TokenStreamStack for this parser.
	 * 
	 * @return The TokenStackStream.
	 */
	public static TokenStreamStack getTokenStreamStack() {
		return tss;
	}
	
	/**
	 * @param top
	 */
	@SuppressWarnings("unused")
	private static void loadValues(ConfigurationNode top) {
		// 
		int attributeCount = top.getAttributeCount();
		List<ConfigurationNode> values = top.getChildren("item");
		for(ConfigurationNode node : values) {
			System.out.print("\t\tattributeCount=" + attributeCount);
			List<ConfigurationNode> attrs = node.getAttributes();
			for(ConfigurationNode attr : attrs) {
				System.out.print(" attr: " + attr.getName() + "=" + attr.getValue());
			}
			System.out.println();
		}
	}   
	
	/**
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		//Log log = LogFactory.getLog(EvalVisitor.class);
		
		// Set up a simple configuration that logs on the console.
		//BasicConfigurator.configure();

		// BasicConfigurator replaced with PropertyConfigurator.
		PropertyConfigurator.configure("log4j.properties");


		log.debug("test");
		
		System.out.println(System.getProperty("user.dir"));

		CaAlloc instance = new CaAlloc();
		
		instance.exec(args);
	
	}
	
	private void exec(String[] args) {
		
		//saveProperties();
		
		//loadProperties();
		
		if( ! getCommandLineOptions(args)) {
			return;
		}

		tss.pushTS(inputFilename);
		AllocParser parser = new AllocParser(tss);
		
		ParseTree tree = parser.script(); // parse

		// Read the XML configuration file
		XMLConfiguration config = null;
		try {
			// TODO figure out why there is a problem opening this file.
			String base = System.getProperty("user.dir");
			config = new XMLConfiguration(base + File.separatorChar + "data" + File.separator + configFilename);
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		{// Load variables from the config file
			HierarchicalConfiguration.Node node = config.getRoot();
			if (debugging) {
				System.out.println("# children=" + node.getChildrenCount());
			}
			int ccount = node.getChildrenCount("test");
			if (debugging) {
				System.out.println("ccount=" + ccount);
			}
			for(ConfigurationNode childNode : node.getChildren("test")) {
				System.out.println("---------------------------------------");
				System.out.print("\nchild node=" + childNode.getName());
				for(ConfigurationNode attrNode : childNode.getAttributes()) {
					System.out.print(" " + attrNode.getName() + "=" + attrNode.getValue());
				}
				System.out.println();

				TestNodeVisitor visitor = new TestNodeVisitor();
				childNode.visit(visitor);

				EvalVisitor eval = new EvalVisitor();
				
				SortedSet<String> valueKeys = new TreeSet<String>(visitor.getValues().keySet());
				if(0 == valueKeys.size()) {
					throw new RuntimeException("map keys = 0");
				}

				// TODO Fix this code
				List<String>  keyList = new ArrayList<String>(valueKeys);
				java.util.Collections.sort(keyList);

				System.out.println("\nSystem Variables");
				System.out.println("----------------");
				for(String key : valueKeys) {
					String value = visitor.getValues().get(key);
					System.out.println(key + "=" + value);
					String name = "&" + key;
					eval.setVar(name, new AllocValue(value));
				}
				CaAlloc.execScript(tree, eval);
			}
		}
		
		if(debugging) {
			System.out.println("debug termination");
			//return;
		}
		
	
	}
	
	/**
	 * @param args
	 */
	private boolean getCommandLineOptions(String[] args) {
		
		// Create command line options
		Options options = new Options();
		Option debugOpt = new Option("d", "debug", false, "Turn on debugging");
		Option helpOpt = new Option( "help", "print this message" );
		Option inputFileOpt = Option.builder("f")
				.longOpt("file")
				.hasArg()
				.desc("Input filename")
				.build();
		Option configFileOpt = Option.builder("c")
				.longOpt("config")
				.hasArg()
				.desc("Config filename")
				.build();
		options.addOption(helpOpt);
		options.addOption(debugOpt);
		options.addOption(inputFileOpt);
		options.addOption(configFileOpt);

		// Process the command line
		
		try {
			CommandLineParser cmdParser = new DefaultParser();
			CommandLine cmd = cmdParser.parse( options, args);

			inputFilename = cmd.getOptionValue("file", "VDSPROG.txt");
			
			configFilename = cmd.getOptionValue("config", "DUMMY.xml");
			
			debugging = cmd.hasOption("debug");

			if(debugging) {
				log.info("inputFilename=" + inputFilename);
				log.info("configFilename=" + configFilename);
				log.info("debugging=" + debugging);
			}

			if(cmd.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				formatter.printHelp( pw, 66, "CaAlloc", "Header", options, 4, 6, "Footer");
				helpText = sw.toString();
				//formatter.printHelp( "CaAlloc", options );
				System.out.println(helpText);
				return false;
			}
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.out.println("Invalid command line");
			System.out.println(helpText);
			return false;
		}
		return true;
	}

	/**
	 * @return the debugging
	 */
	public boolean isDebugging() {
		return debugging;
	}
	
	/**
	 *  Load app properties from a properties file.
	 */
	@SuppressWarnings("unused")
	private void loadProperties() {
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			if(debugging) {
				System.out.println(prop.getProperty("database"));
				System.out.println(prop.getProperty("dbuser"));
				System.out.println(prop.getProperty("dbpassword"));
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * Save app properties to a properties file.
	 */
	@SuppressWarnings("unused")
	private void saveProperties() {
		OutputStream output = null;

		try {
			output = new FileOutputStream("config.properties");

			// set the properties value
			prop.setProperty("database", "localhost");
			prop.setProperty("dbuser", "mkyong");
			prop.setProperty("dbpassword", "password");

			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
			RuntimeException re = new RuntimeException("Unable to open file config.properties");
			re.setStackTrace(io.getStackTrace());
			throw re;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * @param debugging the debugging to set
	 */
	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}
	
}
