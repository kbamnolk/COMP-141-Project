/*
 * Karen Bamnolker
 * January 22, 2018
 * Phase 1
 * Written in Java
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

public class Scanner 
{
	public Scanner()
	{
		//hi
	}
	public static String parseToken(String token)
	{
		if(token.equals("false") || token.equals("true"))	
		{
			return "BOOL";
		}
		
		//if | then | else | endif | while | do | endwhile | skip
		if(token.equals("if") || token.equals("then") || token.equals("else") || token.equals("endif")
				|| token.equals("while") || token.equals("do") 
				|| token.equals("endwhile") || token.equals("skip"))	
		{
			return "KEYWORD";
		}
		
		if(token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")
				|| token.equals("&") || token.equals("|") || token.equals("!")
				|| token.equals("(") || token.equals(")") || token.equals("=")
				|| token.equals("==") || token.equals(":=") || token.equals(";"))
		{
			return "PUNCTUATION";
		}
		
		if (token.matches("[0-9]+"))
		{
			return "NUMBER";
		}
				
		if (token.matches("([a-zA-Z])([a-zA-Z0-9])*"))
		{
			return "IDENTIFIER";
		}
		
		return "MORE";
	}
	
	

}
