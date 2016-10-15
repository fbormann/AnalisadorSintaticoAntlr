package test;

import java.util.List;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ast.ClassDecl;
import ast.ClassDeclExtends;
import ast.ClassDeclList;
import ast.ClassDeclSimple;
import ast.Identifier;
import ast.MainClass;
import ast.MethodDeclList;
import ast.Program;
import ast.Statement;
import ast.Type;
import ast.VarDeclList;
import test.miniJavaParser.ClassDeclarationContext;
import test.miniJavaParser.GoalContext;
import test.miniJavaParser.MainClassContext;
import test.miniJavaParser.MethodDeclarationContext;
import test.miniJavaParser.StatementContext;
import test.miniJavaParser.TypeContext;
import test.miniJavaParser.VarDeclarationContext;

public class BuildAST {

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
		Identifier id1 = this.visiIdentifier(c.Identifier(0));
		Identifier id2 = this.visiIdentifier(c.Identifier(1));
		VarDeclList varList = this.visiVarDeclList(c.varDeclaration());
		MethodDeclList ml = this.visitMethodDeclList(c.methodDeclaration());
		return new ClassDeclExtends(id1, id2, varList, ml);
	}
	private ClassDecl visiClassDecl(ClassDeclarationContext c) {
		
		Identifier id = this.visiIdentifier(c.Identifier(0));
		VarDeclList varList = this.visiVarDeclList(c.varDeclaration());
		MethodDeclList ml = this.visitMethodDeclList(c.methodDeclaration());
		return new ClassDeclSimple(id, varList, ml);
	}
	private MethodDeclList visitMethodDeclList(List<MethodDeclarationContext> methodDeclaration) {
		for(MethodDeclarationContext m : methodDeclaration){
			this.visiMethodDecl(m);
		}
		return null;
	}
	private void visiMethodDecl(MethodDeclarationContext m) {
		Identifier id = this.visiIdentifier(m.Identifier(0));
		Type t = this.visiType(m.type(0));
		
		
	}
	private Type visiType(TypeContext type) {
		
		return null;
	}
	private VarDeclList visiVarDeclList(List<VarDeclarationContext> varDeclaration) {
		
		return null;
	}
	private Identifier visiIdentifier(TerminalNode identifier) {
		
		return null;
	}
	private MainClass visitMainClass(MainClassContext mainContext) {
		Identifier id1  = this.visiIdentifier(mainContext.Identifier(0));
		Identifier id2 = this.visiIdentifier(mainContext.Identifier(1));
		Statement st =  this.visitStatement(mainContext.statement());
		return null;
	}
	private Statement visitStatement(StatementContext statement) {
		
		return null;
	}
	
	
}
