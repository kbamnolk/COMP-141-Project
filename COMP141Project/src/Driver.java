
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
import java.util.*;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Driver {
	class Token { // each token has a value and a type
		String value;
		String type;

		Token(String v, String t) {
			this.value = v; // e.g., "if"
			this.type = t; // e.g., "KEYWORD"
		}
		/*
		 * Also define getter and setter methods.
		 */
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
	}

	class ParsingException extends Exception {
	} // this is to generate Parsing errors

	ArrayList<Token> scanner(String fName) {
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

			while (input.hasNextLine()) {
				String s = br.readLine();
				Scanner input2 = new Scanner(input.nextLine());

				while (input2.hasNext()) {
					String token = input2.next();
					tokenType = parseToken(token);

					// output.println(tokenType + " " + token);
					Token tok = new Token(token, tokenType);
					list.add(tok);
				}
			}

			input.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	private String parseToken(String token) {

		if (token.equals("false") || token.equals("true")) {
			return "BOOL";
		}

		// if | then | else | endif | while | do | endwhile | skip
		if (token.equals("if") || token.equals("then") || token.equals("else") || token.equals("endif")
				|| token.equals("while") || token.equals("do") || token.equals("endwhile") || token.equals("skip")) {
			return "KEYWORD";
		}

		if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("&")
				|| token.equals("|") || token.equals("!") || token.equals("(") || token.equals(")") || token.equals("=")
				|| token.equals("==") || token.equals(":=") || token.equals(";")) {
			return "PUNCTUATION";
		}

		if (token.matches("[0-9]+")) {
			return "NUMBER";
		}

		if (token.matches("([a-zA-Z])([a-zA-Z0-9])*")) {
			return "IDENTIFIER";
		}

		return "MORE";
	}

	ArrayList<Token> tokenList = new ArrayList<Token>(); // list of tokens accessible to all parsing methods
	int index = 0; // index in the list of tokens

	Token nextToken() { // returns the token in the current index
		return tokenList.get(index);
	}

	void consumeToken() { // shifts the token forward
		index++;
	}

	void printAST(AST tree) {
		/* print the resulting AST to output file */
	}

	AST parseStatement() throws ParsingException {
		AST tree = parseBaseStatement();
		while (nextToken().value == ";") {
			Token t = nextToken();
			consumeToken();
			tree = new AST(t, tree, parseBaseStatement(), null);
		}
		return tree;
	}

	AST parseBaseStatement() throws ParsingException {
		AST tree;
		if (nextToken().type == "Identifier") {
			tree = parseAssignment();
		} else if (nextToken().value == "if") {
			tree = parseIfStatement();
		} else if (nextToken().value == "while") {
			tree = parseWhileStatement();
		} else if (nextToken().value == "skip") {
			tree = parseSkip();
		} else {
			throw new ParsingException();
		}
		return tree;
	}

	AST parseAssignment() throws ParsingException {
		return null;
		/*
		 * check whether nextToken is an identifier if so, make a AST tree1 with token
		 * consisting of the identifier, and null children. next check if the next token
		 * is assignment operator. if so make a token t2. next return a tree with t2 in
		 * the root, left child to be tree1, and right child to be the result of parsing
		 * expression. otherwise generate parsing error otherwise generate parsing Error
		 */
	}

	AST parseIfStatement() throws ParsingException {
		return null;
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
	}

	AST parseWhileStatement() throws ParsingException {
		return null;
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
	}

	AST parseSkip() throws ParsingException {
		return null;
		/*
		 * check whether the next token is "skip" if so, make a token t1 containing
		 * "skip" and its type (KEYWORD). return a tree with token t1 as its root null
		 * for left, middle and right children otherwise generate parsing error
		 */
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

		d.tokenList = d.scanner(inFileName);
		try {
			AST ast = d.parseStatement();
			d.printAST(ast);
		} catch (ParsingException e) {
			System.out.println("Parsing Error!");
		}
	}
}
