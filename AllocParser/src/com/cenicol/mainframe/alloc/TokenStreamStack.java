/**
 * Author Cassie Nicol 
 */

package com.cenicol.mainframe.alloc;
 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

import com.cenicol.antlr4.alloc.parser.AllocLexer;

/**
 * TokenStackStream is a simpleton providing a Stack of TokenStream objects.
 * 
 * @author Cassie Nicol
 *
 */
public class TokenStreamStack implements TokenStream, TokenStreamStackI {
	
	/**
	 * 
	 */
	static TokenStreamStack tss = new TokenStreamStack();
	
	/**
	 * The token stream stack. 
	 */
	private Stack<TokenStream> tokenStack = new Stack<TokenStream>();
	
	/**
	 * Get the instance of TokenStreamStack.
	 * @return the instance of TokenStackStream.
	 */
	public static TokenStreamStack instance() {
		return tss;
	}

	/**
	 * Default constructor.
	 */
	private TokenStreamStack() {
	}

	/**
	 * @return true if the token steam stack is empty.
	 */
	public boolean empty() {
		return tokenStack.empty();
	}
	
	/**
	 * Gets the top token stream.
	 * 
	 * @return The TokenStream on the top of the stack.
	 */
	private TokenStream top() {
		return tokenStack.peek();
	}
	
	/** 
	 * @see TokenStreamStackI#pushTS(java.lang.String)
	 */
	public void pushTS(String filename) {

		ANTLRInputStream input = getInputStream(filename);
		
		AllocLexer lexer = new AllocLexer(input);
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		tokenStack.push(tokens);

	}

	
	/**
	 * Pops the top TokenStream off the stack. 
	 * 
	 * Note: If there is only one element on the stack, then nothing will happen.
	 * 
	 * @return The TokenStream being removed. 
	 */
	public TokenStream popTS() {
		if(tokenStack.size() > 1) {
			TokenStream v = tokenStack.pop();
			return v;
		} else {
			return tokenStack.peek();
		}
	}	
	
	ANTLRInputStream getInputStream(String filename) throws RuntimeException {
		
		InputStream is = System.in;
		if ( filename!=null ) {
			try {
				is = new FileInputStream(filename);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				RuntimeException re = new RuntimeException("Could not open" + filename);
				throw re;
			}
		}
		
		ANTLRInputStream input = null;
		try {
			input = new ANTLRInputStream(is);
			input.name = filename;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return input;
	}
	
	//-----------------------------------------------------------------------------
	// Implementation of TokenStream
	//-----------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.IntStream#LA(int)
	 */
	@Override
	public int LA(int arg0) {
		return top().LA(arg0);
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.IntStream#consume()
	 */
	@Override
	public void consume() {
		top().consume();
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.IntStream#getSourceName()
	 */
	@Override
	public String getSourceName() {
		top().getSourceName();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.IntStream#index()
	 */
	@Override
	public int index() {
		return top().index();
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.IntStream#mark()
	 */
	@Override
	public int mark() {
		return top().mark();
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.IntStream#release(int)
	 */
	@Override
	public void release(int arg0) {
		top().release(arg0);
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.IntStream#seek(int)
	 */
	@Override
	public void seek(int arg0) {
		top().seek(arg0);
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.IntStream#size()
	 */
	@Override
	public int size() {
		return top().size();
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.TokenStream#LT(int)
	 */
	@Override
	public Token LT(int arg0) {
		return top().LT(arg0);
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.TokenStream#get(int)
	 */
	@Override
	public Token get(int arg0) {
		return top().get(arg0);
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.TokenStream#getText()
	 */
	@Override
	public String getText() {
		return top().getText();
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.TokenStream#getText(org.antlr.v4.runtime.misc.Interval)
	 */
	@Override
	public String getText(Interval arg0) {
		return top().getText(arg0);
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.TokenStream#getText(org.antlr.v4.runtime.RuleContext)
	 */
	@Override
	public String getText(RuleContext arg0) {
		return top().getText(arg0);
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.TokenStream#getText(org.antlr.v4.runtime.Token, org.antlr.v4.runtime.Token)
	 */
	@Override
	public String getText(Token arg0, Token arg1) {
		return top().getText(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.TokenStream#getTokenSource()
	 */
	@Override
	public TokenSource getTokenSource() {
		return top().getTokenSource();
	}


}
