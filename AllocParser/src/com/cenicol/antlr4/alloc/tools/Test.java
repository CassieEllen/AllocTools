/**
 * 
 */
package com.cenicol.antlr4.alloc.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

import javax.print.PrintException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.TestRig;

/**
 * @author "Cassie Nicol"
 *
 */
public class Test extends TestRig {

	static String[] myArgs = {"com.cenicol.antlr4.alloc.parser.Alloc", "script", "-tokens", "test.txt"}; 
	
	/**
	 * @param args Command line arguments
	 * @throws Exception Exception For parse failure.
	 */
	public Test(String[] args) throws Exception {
		super(myArgs);
		
		showTokens = true;
		
		showTokens = false;
		printTree = true;
		
//		printTree = false;
//		gui = true;
		
	}

	
	
	/**
	 * @param args Command line arguments
	 * @throws Exception For parse failure.
	 */
	public static void main(String[] args) throws Exception {
		Test rig = new Test(args);
		rig.process();
	}

	protected void process(Lexer lexer, Class<? extends Parser> parserClass, Parser parser, InputStream is, Reader r) 
			throws IOException, IllegalAccessException, InvocationTargetException, PrintException 
	{
		if(! showTokens ) {
			super.process(lexer, parserClass, parser, is, r);
			return;
		}
		
		try {
			ANTLRInputStream input = new ANTLRInputStream(r);
			lexer.setInputStream(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			tokens.fill();

			if ( showTokens ) {
				for (Object tok : tokens.getTokens()) {
					Token t = (Token) tok;
					System.out.println(tok + " " + parser.getVocabulary().getDisplayName(t.getType()) );
				}
			}

		}
		finally {
			if ( r!=null ) r.close();
			if ( is!=null ) is.close();
		}
	}

}
