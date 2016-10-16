package test;

import java.util.List;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import ast.*;
import test.miniJavaParser.*;

public class BuildAST  {

	MainClass mainClass;
	ClassDeclList classDeclList;
	
	public MainClass getMainClass() {
		return mainClass;
	}
	public void setMainClass(MainClass mainClass) {
		this.mainClass = mainClass;
	}
	public ClassDeclList getClassDeclList() {
		return classDeclList;
	}
	public void setClassDeclList(ClassDeclList classDeclList) {
		this.classDeclList = classDeclList;
	}
	public BuildAST(){
		
		
	}
	
	
	
	public MainClass buildMainClass(){
		
		return new MainClass(null, null, null);
	}
	
	
	
	public Program visitGoal(GoalContext goal) {
		
		this.mainClass = visitMainClass(goal.mainClass());
		this.classDeclList = visitClassDeclList(goal.classDeclaration());
		return new Program(this.mainClass, this.classDeclList);
	}
	
	
	private ClassDeclList visitClassDeclList(List<ClassDeclarationContext> classDeclaration) {
		ClassDeclList classDecList = new ClassDeclList();
		for (ClassDeclarationContext c : classDeclaration){
			if(c.Identifier().size() > 1) classDecList.addElement(this.visiClassDeclExtends(c));
			else {
				classDecList.addElement(this.visiClassDecl(c));
			}
		}
		
		return classDecList;
	}
	private ClassDecl visiClassDeclExtends(ClassDeclarationContext c) {
		Identifier id1 = this.visitIdentifier(c.Identifier(0));
		Identifier id2 = this.visitIdentifier(c.Identifier(1));
		VarDeclList varList = this.visitVarDeclList(c.varDeclaration());
		MethodDeclList ml = this.visitMethodDeclList(c.methodDeclaration());
		return new ClassDeclExtends(id1, id2, varList, ml);
	}
	private ClassDecl visiClassDecl(ClassDeclarationContext c) {
		
		Identifier id = this.visitIdentifier(c.Identifier(0));
		VarDeclList varList = this.visitVarDeclList(c.varDeclaration());
		MethodDeclList ml = this.visitMethodDeclList(c.methodDeclaration());
		return new ClassDeclSimple(id, varList, ml);
	}
	private MethodDeclList visitMethodDeclList(List<MethodDeclarationContext> methodDeclaration) {
		MethodDeclList mlist = new MethodDeclList();
		for(MethodDeclarationContext m : methodDeclaration){
			mlist.addElement(this.visiMethodDecl(m));
		}
		return mlist;
	}
	private MethodDecl visiMethodDecl(MethodDeclarationContext m) {
		Identifier id = this.visitIdentifier(m.Identifier(0));
		Type t = this.visitType(m.type(0));
		//FormalList = this.visitFormalList()
		VarDeclList val = this.visitVarDeclList(m.varDeclaration());
		StatementList sts = this.visiStatementList(m.statement());
		Exp expr = this.visitExpression(m.expression());
		return new MethodDecl(t,id,null,val,sts,expr);
	}
	private Exp visitExpression(ExpressionContext expression) {
		if(expression == null){
			return null;
		}
		if(expression.isEmpty()){
			return null;
		}else if(!expression.expression().isEmpty()){
			if(expression.getChild(1).getText().equals(".")){ 
				if(expression.getChild(2).getText().equals("length")){
					return new ArrayLength(this.visitExpression(expression.expression(0)));
				}
				//If it's not a arraylength it will be a call
				return new Call(this.visitExpression(expression.expression(0)),
						this.visitIdentifier(expression.Identifier()),
						this.visitExpressionList(expression.expression().subList(1, 
								expression.expression().size())));
			}
			switch(expression.getChild(1).getText()){
				case "&&":
					return new And(this.visitExpression(expression.expression(0)),
							this.visitExpression(expression.expression(1)));
					
				case "*":
					return new Times(this.visitExpression(expression.expression(0)),
							this.visitExpression(expression.expression(1)));
					
				case "<":
					return new LessThan(this.visitExpression(expression.expression(0)),
							this.visitExpression(expression.expression(1)));
					
				case "-":
					return new Minus(this.visitExpression(expression.expression(0)),
							this.visitExpression(expression.expression(1)));
				case "[":
					return new ArrayLookup(this.visitExpression(expression.expression(0)),
							this.visitExpression(expression.expression(1)));
				
			}
			
			this.visitExpressionList(expression.expression());
		}else if(expression.getChildCount() == 1){
			if(expression.getChild(0).getText().equals("new")){
				if(expression.Identifier() != null) return new NewObject(this.visitIdentifier(expression.Identifier()));
				return new NewArray (this.visitExpression(expression.expression(0)));
			}
			if(expression.getChild(0).getText().equals("this")) return new This();
			if(expression.getChild(0).getText().equals("true")) return new True();
			if(expression.getChild(0).getText().equals("false")) return new False();
			
			if(expression.getChild(0).getText().equals("!")){
				return new Not(this.visitExpression(expression.expression(0)));
			}
			try{
				int x = Integer.parseInt(expression.getChild(0).getText());
				return new IntegerLiteral(x);
			}catch(Exception e){
				return new IdentifierExp(expression.getChild(0).getText());
			}
			
				
			
		}
		
		if(expression.expression() != null){
		return this.visitExpression(expression.expression(0));
		}
		return null;
	}
	private Exp visitIdentifierExp(TerminalNode identifier) {
		
		return new IdentifierExp(identifier.getText());
	}
	private StatementList visiStatementList(List<StatementContext> statements) {
		StatementList st = new StatementList();
		for(StatementContext s : statements){
			st.addElement(this.visitStatement(s));
		}
		return st;
	}
	private Type visitIdentifierType(TypeContext type) {
		
		return new IdentifierType(type.getText());
	}
	private Type visitType(TypeContext type) {
		if (type.getText().equals("int")){
			return new IntegerType();
		}else if(type.getText().equals("boolean")){
			return new BooleanType();
		}else if(type.getText().equals("int[]")){
			return new IntArrayType();
		}
		return null;
	}
	private VarDeclList visitVarDeclList(List<VarDeclarationContext> varDeclaration) {
		VarDeclList vlist = new VarDeclList();
		for(VarDeclarationContext ctx : varDeclaration){
			vlist.addElement(this.visitVarDecl(ctx));
		}
		return vlist;
	}
	private VarDecl visitVarDecl(VarDeclarationContext ctx) {
		
		return new VarDecl(this.visitType(ctx.type()),this.visitIdentifier(ctx.Identifier()));
	}
	private Identifier visitIdentifier(TerminalNode identifier) {
		
		return new Identifier(identifier.toString());
	}
	private MainClass visitMainClass(MainClassContext mainContext) {
		Identifier id1  = this.visitIdentifier(mainContext.Identifier(0));
		Identifier id2 = this.visitIdentifier(mainContext.Identifier(1));
		Statement st =  this.visitStatement(mainContext.statement());
		
		return new MainClass(id1, id2, st);
	}
	private Statement visitStatement(StatementContext statement) {
		if(statement.isEmpty()){
			return null;
		}
		
		if(statement.getChild(0).getText().equals("System.out.println")){
			return new Print(this.visitExpression(statement.expression(0)));
		}else if(statement.getChild(0).getText().equals("while")){
			return new While(this.visitExpression(statement.expression(0)), 
					this.visitStatement(statement.statement(0)));
		}else if(statement.getChild(0).getText().equals("if")){
			return new If(this.visitExpression(statement.expression(0)),
					this.visitStatement(statement.statement(0)), this.visitStatement(statement.statement(1)));
		}else if(statement.getChild(0).getText().equals("{")){
			return this.visitBlock(statement.statement());
		}else if(statement.getChild(1).getText().equals("=")){
			return this.visitAssign(statement.Identifier(),
					statement.expression(0));
		}else{
			return this.visitArrayAssign(statement.Identifier(),statement.expression(0),
					statement.expression(1));
		}
	}
	
	
	private Statement visitArrayAssign(TerminalNode identifier, ExpressionContext expression,
			ExpressionContext expression2) {
		
		return new ArrayAssign(this.visitIdentifier(identifier), this.visitExpression(expression),
				this.visitExpression(expression2));
	}
	private Statement visitAssign(TerminalNode terminalNode, ExpressionContext expressionContext) {
		
		return new Assign(this.visitIdentifier(terminalNode), this.visitExpression(expressionContext));
	}
	private Block visitBlock(List<StatementContext> statement) {
		
		return new Block(this.visitStatementList(statement));
	}
	private StatementList visitStatementList(List<StatementContext> statement) {
		StatementList stL = new StatementList();
		for( StatementContext stctx : statement){
			stL.addElement(this.visitStatement(stctx));
		}
		return stL;
	}
	private ExpList visitExpressionList(List<ExpressionContext> expressions) {
		//TODO: return statement from here
		ExpList expList = new ExpList();
		for(ExpressionContext exp : expressions){
			expList.addElement(this.visitExpression(exp));
		}
		return expList;
	}
	
	
	
}
