package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ast.Program;
import visitor.PrettyPrintVisitor;


public class Main {

	public static void main(String[] args) {
		InputStream stream;
		try {
			stream = new FileInputStream("teste.txt");
			ANTLRInputStream input = new ANTLRInputStream(stream);
			miniJavaLexer lexer = new miniJavaLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			
			miniJavaParser parser = new miniJavaParser(tokens);
			//ParseTree tree = parser.goal();
			
			BuildAST builder = new BuildAST();
			
			
			Program prog = builder.visitGoal(parser.goal());;
			PrettyPrintVisitor ptv = new PrettyPrintVisitor();
			prog.accept(ptv);
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}

}
