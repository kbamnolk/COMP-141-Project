
/* The CFG is defined using EBNF notation. This notation helps you to
define the
the parser straightforwardly.
Basically, for each of the LHS nonterminal you need to define a parsing
function.
The body of the function is defined according to the RHS of the CFG
rule.
Here, sample definitions for nonterminals statement and basestatement
are given.
For next few, i.e., assignment, ifstatement, whilestatement, and skip
they are descibed
in English. Then, the list of functions that remain to be defined are
given.
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Driver {
	
	PrintWriter output;
	
	class Token { // each token has a value and a type
		String value;
		String type;

		Token(String v, String t) {
			this.value = v; // e.g., "if"
			this.type = t; // e.g., "KEYWORD"
		}

		public String getValue() {
			return value;
		}

		public String getType() {
			return type;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	class AST {
		// ASTs at most have 3 children (3 is used for if statements).
		// For every other statement and expression, at least one child will be null.
		Token token;
		AST left;
		AST middle;
		AST right;

		AST(Token t, AST l, AST m, AST r) {
			this.token = t;
			this.left = l;
			this.middle = m;
			this.right = r;
		}
		/*
		 * Also define getter and setter methods
		 */

		public Token getToken() {
			return token;
		}

		public AST getLeft() {
			return left;
		}

		public AST getMiddle() {
			return middle;
		}

		public AST getRight() {
			return right;
		}

		public void setToken(Token token) {
			this.token = token;
		}

		public void setLeft(AST left) {
			this.left = left;
		}

		public void setMiddle(AST middle) {
			this.middle = middle;
		}

		public void setRight(AST right) {
			this.right = right;
		}
	}

	class ParsingException extends Exception {
	} // this is to generate Parsing errors

	ArrayList<Token> scanner(String fName, String outFName) {
		ArrayList<Token> list = new ArrayList<Token>();
		/*
		 * This is where you define your scanner. It is supposed to return a list.
		 */
		System.out.println("Inside scanner; fname is " + fName);

		try {
			Scanner input = new Scanner(fName);

			File file = new File(input.nextLine());
			input = new Scanner(file);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String tokenType;
			output = new PrintWriter(outFName);
			
			output.println("Scanner: Printing input files and tokens");
			output.println();
			
			int lineNum = 1;
			while (input.hasNextLine()) {
				String s = br.readLine();
				output.println("Line " + lineNum  + ": " + s);
				lineNum++;
				Scanner input2 = new Scanner(input.nextLine());

				while (input2.hasNext()) {
					String token = input2.next();
					tokenType = parseToken(token);

					output.println("Token is " + token + " tokentype is " + tokenType);

					Token tok = new Token(token, tokenType);
					list.add(tok);
				}
				output.println();
			}

			input.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println("print array list index i " + i);
			System.out.println(list.get(i).getValue());
			System.out.println(list.get(i).getType());
		}

		return list;
	}

	private String parseToken(String token) {

		if (token.equals("false") || token.equals("true")) {
			return "Bool";
		}

		// if | then | else | endif | while | do | endwhile | skip
		if (token.equals("if") || token.equals("then") || token.equals("else") || token.equals("endif")
				|| token.equals("while") || token.equals("do") || token.equals("endwhile") || token.equals("skip")) {
			return "Keyword";
		}

		if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("&")
				|| token.equals("|") || token.equals("!") || token.equals("(") || token.equals(")") || token.equals("=")
				|| token.equals("==") || token.equals(":=") || token.equals(";")) {
			return "Punctuation";
		}

		if (token.matches("[0-9]+")) {
			return "Number";
		}

		if (token.matches("([a-zA-Z])([a-zA-Z0-9])*")) {
			return "Identifier";
		}

		return "MORE";
	}

	ArrayList<Token> tokenList = new ArrayList<Token>(); // list of tokens accessible to all parsing methods
	int index = 0; // index in the list of tokens

	Token nextToken() { // returns the token in the current index
		if(tokenList.size() > index)
		{
			return tokenList.get(index);
		}
		
		else
		{
			return null;
		}
	}

	void consumeToken() { // shifts the token forward
		index++;
	}

	/* print the resulting AST to output file */
	/* The spacing string is used to add two spaces for extra level we go */
	void printAST(AST tree, String spacing) {
		// System.out.println("Entering printAST");
		if (tree != null) {
			output.println(spacing + "VALUE: " + tree.token.getValue() + " TYPE: " + tree.token.getType());
			spacing = spacing + "  ";
			printAST(tree.left, spacing);
			printAST(tree.middle, spacing);
			printAST(tree.right, spacing);
		}
	}

	AST parseStatement() throws ParsingException {
		System.out.println("Entering parseStatement");
		AST tree = parseBaseStatement();
		//System.out.println("after parse base statement token is " + nextToken().value);
		
		System.out.println("Am I still UP?");

		while (nextToken()!= null && nextToken().value.equals(";")) {
			System.out.println("Entering while loop for ;");
			Token t = new Token(nextToken().value, nextToken().type);
			consumeToken();
			
			// add code here to get a node for the ';'
			// Create a new AST node, for the';' and add tree to its left side
			// This should be the new tree we return
			// original: tree = new AST(t, tree, parseBaseStatement(), null);
			tree = new AST(t, parseBaseStatement(), null, tree);
		}

		return tree;
	}

	AST parseBaseStatement() throws ParsingException {

		AST tree;
		System.out.println("Entering parseBaseStatement");

		if (nextToken().type == "Identifier") {
			tree = parseAssignment();
		} else if (nextToken().value.equals("if")) {
			tree = parseIfStatement();
		} else if (nextToken().value.equals("while")) {
			System.out.println("inside while part");
			tree = parseWhileStatement();
		} else if (nextToken().value.equals("skip")) {
			tree = parseSkip();
		} else {
			throw new ParsingException();
		}
		return tree;
	}

	AST parseAssignment() throws ParsingException {

		System.out.println("Entering parseAssignment");
		AST tree = null;

		/*
		 * check whether nextToken is an identifier if so, make a AST tree1 with token
		 * consisting of the identifier, and null children. next check if the next token
		 * is assignment operator. if so make a token t2. next return a tree with t2 in
		 * the root, left child to be tree1, and right child to be the result of parsing
		 * expression. otherwise generate parsing error
		 */

		if (nextToken().type == "Identifier") {
			System.out.println("next token is an identifier");
			Token t = new Token(nextToken().value, nextToken().type);
			consumeToken();
			AST tree1 = new AST(t, null, null, null);
			System.out.println("next token is " + nextToken().getValue());

			if (nextToken().getValue().equals(":=")) {
				System.out.println("next token is an assignment");
				Token t2 = new Token(nextToken().value, nextToken().type);
				consumeToken();
				tree = new AST(t2, tree1, null, parseExpression());
				// return tree;
			}
		}
		return tree;
	}

	AST parseExpression() throws ParsingException {

		AST tree = null;
		System.out.println("inside parseExpression token is " + (nextToken().value));

		if (nextToken().type == "Number") {
			System.out.println("FOUND A NUMBER");
			Token t = new Token(nextToken().value, nextToken().type);
			consumeToken();
			tree = new AST(t, null, null, null);
		} else if (nextToken().type == "Bool") {
			System.out.println("FOUND A BOOL");
			Token t = new Token(nextToken().value, nextToken().type);
			consumeToken();
			tree = new AST(t, null, null, null);
		}

		return tree;
	}

	AST parseIfStatement() throws ParsingException {
		
		/*
		 * check whether nextToken is "if". if so, make a token t1 for if statement, and
		 * consume the token. next parse boolean expression and assign it to AST tree1.
		 * check if the next token is "then". if so, consume the token, parse the
		 * statement and assign the result to tree2. check if the next token is "else".
		 * if so, consume the token, parse the statement and assign the result to tree3.
		 * check if next token is "endif". if so, consume the token, and return a tree
		 * with token t1 at the root, tree1 as the left child tree2 as the middle child
		 * tree3 as the right child otherwise generate parsing error otherwise generate
		 * parsing error otherwise generate parsing error otherwise generate parsing
		 * error
		 */
	
		System.out.println("entering parseIfStatement");
		
		Token t1 = new Token(nextToken().value, nextToken().type);
		consumeToken();
		
		AST tree1 = new AST(t1, parseBoolExpression(), null, null);
		
		if(nextToken().value.equals("then"))
		{
			System.out.println("inside then");
			consumeToken();
			
			AST tree2 = parseBaseStatement();
			if(nextToken().value.equals("else"))
			{
				System.out.println("inside else");
				consumeToken();
				AST tree3 = parseStatement();
				
				if(nextToken().value.equals("endif"))
				{
					consumeToken();
					tree1.setMiddle(tree2);
					tree1.setRight(tree3);
				}
			}
		}
		return tree1;
		
	}

	AST parseBoolExpression() throws ParsingException {

		AST tree = null;
		System.out.println("inside parseBoolExpression token is " + (nextToken().value));

		if (nextToken().type == "Bool") {
			System.out.println("FOUND A BOOL");
			Token t = new Token(nextToken().value, nextToken().type);
			consumeToken();
			tree = new AST(t, null, null, null);
		}

		return tree;
	}

	AST parseWhileStatement() throws ParsingException {
		
		/*
		 * check whether next token is "while" if so, make a token t1 for while loops,
		 * and consume the token. next parse the boolean expression and assign it to AST
		 * tree1. check if the next token is "do" if so, consume the token, parse the
		 * statement and assign the result to tree2. check whether the next token is
		 * "endwhile" if so, consume the token, and return a tree with token t1 at the
		 * root tree1 as the left child tree2 as the middle child null as the right
		 * child otherwise generate parsing error otherwise generate parsing error
		 * otherwise generate parsing error
		 */
		System.out.println("entering parseWhileStatement");
		
		Token t1 = new Token(nextToken().value, nextToken().type);
		consumeToken();
		
		AST tree1 = new AST(t1, parseBoolExpression(), null, null);
		
		if(nextToken().value.equals("do"))
		{
			System.out.println("inside do");
			consumeToken();
			System.out.println("token after do is " + nextToken().value);
			
			AST tree2 = parseBaseStatement();
			if(nextToken().value.equals("endwhile"))
			{
				System.out.println("inside endwhile");
				consumeToken();
				tree1.setRight(tree2);
			}
		}
		return tree1;
	}

	AST parseSkip() throws ParsingException {
		
		/*
		 * check whether the next token is "skip" if so, make a token t1 containing
		 * "skip" and its type (KEYWORD). return a tree with token t1 as its root null
		 * for left, middle and right children otherwise generate parsing error
		 */
		
		Token t1 = new Token(nextToken().value, nextToken().type);
		consumeToken();
		
		AST tree = new AST(t1, null, null, null);
		return tree;
		
	}

	/*
	 * similarly define a method to parse each of the following: expressions
	 * numexpressions numterms numfactors numpieces numelements numbers identifiers
	 * boolexpressions boolterms boolfactors boolpieces boolelements booleans
	 */
	public static void main(String[] args) {
		/*
		 * read from args the input/output file names
		 */
		Driver d = new Driver();
		/*
		 * read the input file and pass it to scanner
		 */
		String inFileName = args[0];
		String outFileName = args[1] + ".txt";

		d.tokenList = d.scanner(inFileName, outFileName);

		try {
			AST ast = d.parseStatement();
			d.printAST(ast, "  ");
		} catch (ParsingException e) {
			System.out.println("Parsing Error!");
		}
		
        d.output.close();

	}
}
