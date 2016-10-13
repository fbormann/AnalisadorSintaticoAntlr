package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;


public class Main {

	public static void main(String[] args) {
		InputStream stream;
		try {
			stream = new FileInputStream("teste.txt");
			ANTLRInputStream input = new ANTLRInputStream(stream);
			miniJavaLexer lexer = new miniJavaLexer(input);
			CommonTokenStream token = new CommonTokenStream(lexer);
			miniJavaParser parser = new miniJavaParser(token);
			ParseTree tree = parser.goal();
			for(int i = 0; i < tree.getChildCount(); i+=1){
				System.out.println(tree.getChild(i).getText());
			}
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
