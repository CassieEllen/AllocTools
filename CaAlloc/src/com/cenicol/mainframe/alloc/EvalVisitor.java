/**
 *  Provides an Alloc parser for script language CaAlloc
 *  
 */

package com.cenicol.mainframe.alloc;

import com.cenicol.antlr4.alloc.parser.*;

import java.util.HashMap;
//import java.util.List;
//import java.util.Vector;
import java.util.Map;








//import org.antlr.v4.runtime.ParserRuleContext;
//import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements Visitor for AllocParser
 * 
 *  @see <a href="https://students.cs.byu.edu/~cs240ta/fall2012/tutorials/javadoctutorial.html">Reference</a>
 * 
 * @author Cassie Nicol
 *
 */
public class EvalVisitor extends AllocBaseVisitor<AllocValue> {
	/** "memory" for our calculator; variable/value pairs go here */
	Map<String, AllocValue> vars = new HashMap<String, AllocValue>();
	
	/** Set to false to stop running the interpreter. */
	boolean isRunning = true;

	/** Sets the indention level for printing out the code trace. */
	private int indentLevel = 0;

	/** Get the log instance for this class. */
	private Log log = LogFactory.getLog(EvalVisitor.class.getName());
	
	/**
	 * Default ctor
	 */
	EvalVisitor() {
		log.trace("EvalVisitor.ctor");
	}

	/**
	 * Get the varible map. 
	 * 
	 * @return The variable map. 
	 */
	public Map<String, AllocValue> getVars() {
		return vars;
	}


	/**
	 * Set the variable map. 
	 * 
	 * @param vars Assigned to the variable map. 
	 */
	public void setVars(Map<String, AllocValue> vars) {
		this.vars = vars;
	}
	
	/**
	 * Sets a variable vars[key] = value
	 * 
	 * @param key name of the variable
	 * @param value of the variable
	 * @return true if the value will replace an existing value.
	 */
	public boolean setVar(String key, AllocValue value) {
		boolean result = vars.containsKey(key);
		vars.put(key, value);
		return result;
	}
	

	/**
	 * Print the line indention. 
	 * 
	 * @param c The indention character to use.
	 */
	void printIndent(char c) {
		char data[] = {c, c};
		String s = new String(data);
		
		for(int i=indentLevel; i>0; --i) {
			System.out.print(s);
		}
	}
	
	/*
	 * Print the line indention with a space. 
	 */
	void printIndent() {
		printIndent(' ');
	}

	/**
	 * Increase the line indention level. 
	 */
	void increaseIndent() {
		++ indentLevel;
		//System.out.print("+");
	}
	
	/**
	 * Decrease the line indention level. 
	 */
	void decreaseIndent() {
		-- indentLevel;
		//System.out.print("-");
	}

	/**
	 * Returns true if more statements should be evaluated. 
 	 * 
	 * @see org.antlr.v4.runtime.tree.AbstractParseTreeVisitor#shouldVisitNextChild(org.antlr.v4.runtime.tree.RuleNode, java.lang.Object)
	 */
	protected boolean shouldVisitNextChild(RuleNode node, AllocValue currentResult) {
		return isRunning;
	}

	/* (non-Javadoc)
	 * @see AbstractParseTreeVisitor#aggregateResult(AllocValue, AllocValue)
	 */
	@Override
	protected AllocValue aggregateResult(AllocValue aggregate, AllocValue nextResult) {
		if(null == aggregate) {
			return nextResult;
		}
		if(null == nextResult) {
			if(aggregate.type != AllocValueType.LIST) {
				AllocValue value = new AllocValue(AllocValueType.LIST, aggregate);
				aggregate = value;
			}
			return aggregate;
		}

		AllocValue value;
		if(aggregate.type == AllocValueType.LIST) {
			value = aggregate;
		} else {
			value = new AllocValue(AllocValueType.LIST, aggregate);
		}
		value.append(nextResult);
		return value;
	}
	
	/* --------------------------------------------------------------------- */
	/* Language Elements                                                     */
	/* --------------------------------------------------------------------- */

/**
 * stat
 */
@Override public AllocValue visitStat(AllocParser.StatContext ctx)
{
	if(indentLevel > 0) {
		printIndent();
	}
	AllocValue value = visitChildren(ctx);
	System.out.println();
	return value;
}

/**
  * 'SET' ID '=' expr ;
 */
@Override public AllocValue visitSet(AllocParser.SetContext ctx)
{
	String id = ctx.ID().getText();         // id is left-hand side of '='
	AllocValue value = visit(ctx.expr());   // compute value of expression on right
	System.out.print("SET " + id + "=" + value);
	vars.put(id, value);                  // store it in our memory
	return null;
}

/* Implement the WRITE statement.
 * 
 * write : 'WRITE' expr ;
 */
@Override public AllocValue visitWrite(AllocParser.WriteContext ctx)
{
	AllocValue value = visit(ctx.expr());
	System.out.print("WRITE ");
	System.out.print(value);
	return null;
}

/**
 * Implement EXIT statement.
 * 
 * EXIT [CODE (INT)]
 * 
 * @see AllocBaseVisitor#visitExit(AllocParser.ExitContext)
 */
@Override public AllocValue visitExit(AllocParser.ExitContext ctx)
{
	System.out.print("EXIT ");

	AllocValue value = new AllocValue(0);
	if(ctx.CODE() != null) {
		int i = Integer.valueOf(ctx.INT().getText());
		value.setInt(i);
		System.out.print("CODE(" + value + ") ");
	}

	// Changing isRunning to false stops the walker from continuing. 
	// TODO uncomment the line below
	isRunning = false;
	return value;
}

/* Implements the CALL statement.
 * 
 * call : 'CALL' (VDSEXIT | QUOTACHK) ;
 * 
 * 
 *
 *  (non-Javadoc)
 * @see AllocBaseVisitor#visitCall(AllocParser.CallContext)
 */
@Override public AllocValue visitCall(AllocParser.CallContext ctx)
{
	System.out.print("CALL ");
	AllocValue value = new AllocValue(0);
	if(ctx.VDSEXIT() != null) {
		//int i = Integer.valueOf(ctx.INT().getText());
		//value.setInt(i);
		//System.out.print("VDSEXIT" + i);
		System.out.print(ctx.VDSEXIT().toString() );
	} else {
		System.out.print("QUOTACHK");
	}
	//throw new org.antlr.v4.runtime.misc.ParseCancellationException("CALL - not implemented!");
	return value;
}


/** Implement block statements.
 * 
 * DO ... END blocks
 * 
 * @see AllocBaseVisitor#visitBlock(AllocParser.BlockContext)
 */
@Override public AllocValue visitBlock(AllocParser.BlockContext ctx)
{
	//printIndent('#'); done in stmt
	System.out.println("DO");
	
	increaseIndent();
	//printIndent('*'); done in stmt
	AllocValue value = visitChildren(ctx);
	decreaseIndent();

	printIndent();
	System.out.print("END");
	return value;
}

// FILTLIST name INCLUDE(list) [EXCLUDE(list)]
/*
 *
 */
@Override public AllocValue visitFiltlist(AllocParser.FiltlistContext ctx)
{
	System.out.print("FILTLIST ");
	String id = ctx.ID().toString();
	System.out.print(id + " ");

	AllocValue include = visit(ctx.list(0));
	AllocValue exclude = null;
	if(ctx.EXCLUDE() != null) {
		exclude = visit(ctx.list(1));
	}
	
	Filtlist f = new Filtlist(include, exclude);
	AllocValue value = new AllocValue(f);
	System.out.print(value);
	vars.put(id, value);                  // store it in our memory

	return null;
}

/*
 *
 */
@Override public AllocValue visitList(AllocParser.ListContext ctx)
{
	AllocValue value = visitChildren(ctx);
	return value;
}

/*
 *
 */
@Override public AllocValue visitPathSTRING(AllocParser.PathSTRINGContext ctx) {
	AllocValue value = new AllocValue(AllocValueType.STRING,ctx.STRING().getText());
	//System.out.println("visitPathString:"+ value);
	return value;
}

/**
 * {@inheritDoc}
 *
 * <p>The default implementation returns the result of calling
 * {@link #visitChildren} on {@code ctx}.</p>
 */
@Override public AllocValue visitPathGLOB(AllocParser.PathGLOBContext ctx) {
	AllocValue value = new AllocValue(AllocValueType.GLOB, ctx.GLOB().getText());
	//System.out.println("visitPathGLOB:" + value);
	return value;
}

/**
 * {@inheritDoc}
 *
 * <p>The default implementation returns the result of calling
 * {@link #visitChildren} on {@code ctx}.</p>
 */
@Override public AllocValue visitCopybook(AllocParser.CopybookContext ctx) {
	String filename = ctx.STRING().getText();
	System.out.print("COPYBOOK " + filename);
	return new AllocValue(AllocValueType.STRING, filename);
}

/* ----------------------------------------------------------------------------- */
/* ----------------------------------------------------------------------------- */

@Override public AllocValue visitIfstat(AllocParser.IfstatContext ctx)
{
	System.out.print("if ");
	AllocValue value = new AllocValue(false);
	AllocValue bexpr =  visit(ctx.bexpr());
	if(bexpr.getBool()) {
		System.out.println(" THEN ");
		increaseIndent();
		value = visit( ctx.stat(0) );
		decreaseIndent();
	} else {
		if(ctx.ELSE() != null) {
			System.out.println(" ELSE ");
			increaseIndent();
			value = visit(ctx.stat(1));
			decreaseIndent();
		}
	}
	System.out.print("ENDIF");
	return value;
}


/* ----------------------------------------------------------------------------- */
/* expr                                                                          */
/* ----------------------------------------------------------------------------- */

/** expr op=('*'|'/') expr */
@Override public AllocValue visitMulDiv(AllocParser.MulDivContext ctx)
{
	AllocValue left = visit(ctx.expr(0));  // get value of left subexpression
	AllocValue right = visit(ctx.expr(1)); // get value of right subexpression
	if ( ctx.op.getType() == AllocParser.MUL ) {
		return left.mul(right);
	} else {
		return left.div(right); // must be DIV
	}
}

/** expr op=('+'|'-') expr */
@Override public AllocValue visitAddSub(AllocParser.AddSubContext ctx)
{
	AllocValue left = visit(ctx.expr(0));  // get value of left subexpression
	AllocValue right = visit(ctx.expr(1)); // get value of right subexpression
	if ( ctx.op.getType() == AllocParser.ADD ) return left.add(right);
	return left.sub(right);
	//return left - right; // must be SUB
}

/** '(' expr ')' */
@Override public AllocValue visitParens(AllocParser.ParensContext ctx)
{
	return visit(ctx.expr()); // return child expr's value
}

/** ID */
@Override public AllocValue visitId(AllocParser.IdContext ctx)
{
	String id = ctx.ID().getText();
	if ( vars.containsKey(id) ) {
		AllocValue value = vars.get(id);
		value.setName(id);
		return value;
	}
	throw new RuntimeException("Variable " + id + " does not exist.");
}

/** INT */
@Override public AllocValue visitInt(AllocParser.IntContext ctx)
{
	AllocValue value = new AllocValue(Integer.valueOf(ctx.INT().getText()));
	return value;
}

/** STRING */
@Override public AllocValue visitString(AllocParser.StringContext ctx)
{
	String text = ctx.STRING().getText();
	int endIndex = text.length() - 1;
	text = text.substring(1, endIndex);
	AllocValue value = new AllocValue(AllocValueType.STRING, text);
	
	// TODO uncomment
	//value.write();
	return value;
}

@Override public AllocValue visitGlob(AllocParser.GlobContext ctx)
{
	AllocValue value = new AllocValue(AllocValueType.GLOB, ctx.GLOB().getText());
	// TODO uncomment
	//value.write();
	return value;
}

/* ----------------------------------------------------------------------------- */
/* ----------------------------------------------------------------------------- */

// bexpr op=('AND' | '&&') bexpr
@Override public AllocValue visitBand(AllocParser.BandContext ctx)
{
	{
		//Interval sourceInterval = ctx.getSourceInterval();
		//Token t = ctx.getStart();
		//int line = t.getLine();
		//System.out.println("(" + line + ")");
	}
	AllocValue left = visit(ctx.bexpr(0));  // get value of left subexpression
	System.out.print(" AND ");
	AllocValue right = visit(ctx.bexpr(1)); // get value of right subexpression
	return left.and(right);
}

// bexpr op=('OR'  | '|') bexpr
@Override public AllocValue visitBor(AllocParser.BorContext ctx)
{
	{
		//Interval sourceInterval = ctx.getSourceInterval();
		//Token t = ctx.getStart();
		//int line = t.getLine();
		//System.out.println("(" + line + ")");
	}
	AllocValue left = visit(ctx.bexpr(0));  // get value of left subexpression
	System.out.print(" OR ");
	AllocValue right = visit(ctx.bexpr(1)); // get value of right subexpression
	return left.or(right);
}

/* ----------------------------------------------------------------------------- */
/* ----------------------------------------------------------------------------- */

// 
@Override public AllocValue visitCexpr(AllocParser.CexprContext ctx)
{
	AllocValue lhs = visit(ctx.expr(0));

	// Print the operator token.
	int id = ctx.op.getType();
	//String name = AllocParser.tokenNames[id];
	String name = AllocParser.VOCABULARY.getDisplayName(id);
	
	AllocValue rhs = visit(ctx.expr(1));

	System.out.print(lhs.toString(true) + " " + name + " " + rhs.toString(true) + " ");

	try {
		switch(ctx.op.getType()) {
		case AllocParser.EQ:         // '==' || 'EQ' -> lhs.eq(rhs)
		case AllocParser.SEQ:
			return lhs.eq(rhs);
		case AllocParser.NE:
			return lhs.ne(rhs);
		case AllocParser.LT:
			return lhs.lt(rhs);
		case AllocParser.GT:
			return lhs.gt(rhs);
		case AllocParser.LE:
			return lhs.le(rhs);
		case AllocParser.GE:
			return lhs.ge(rhs);
		}
	} catch(RuntimeException e) 
	{
		//Interval sourceInterval = ctx.getSourceInterval();
		Token t = ctx.getStart();
		int line = t.getLine();
		System.out.println("runtimeException");
		System.out.println("(" + line + ")");
		throw e;
	}
	throw new java.lang.RuntimeException("Invalid boolean operator: " + ctx.op.getType());
}


}
