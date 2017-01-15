import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

enum Token {
    LITERAL(1),
    NUMERIC(2),
    OPEN_PARENTHESIS('('),
    CLOSING_PARENTHESIS(')'),
    EOF(-1),
    ERROR(5);

    private int value;
    Token(int value){
        this.value=value;
    }
}

class Atom <T> {
    Token type;
    T value;

    Atom(Token type, T value){
        this.type = type;
        this.value = value;
    }
    Atom(Token type){
        this.type = type;
        this.value = null;
    }

    public Token getType(){
        return this.type;
    }

    public T getValue(){
        return this.value;
    }


}

public class Scanner {



    static final int UPPER_CASE_A = 65;
    static final int UPPER_CASE_Z = 96;

    static boolean isDigit(char token){
        return Character.isDigit(token);
    }
    static boolean isOpenParenthesis(int token){
        return token == '(';
    }
    static boolean isClosingParenthesis(char token){
        return token == ')';
    }
    static boolean isUpperCaseLetter(int token){

        return (token >= UPPER_CASE_A && token <= UPPER_CASE_Z );
    }

    static boolean isWhiteSpace(char token){
        return token == 32 || token == 13 || token == 10;
    }
    static boolean isEndOfFile(char token){
        return token == '\uFFFF';
    }

    public static Atom getNextToken (BufferedReader in) throws IOException {
        char token = (char)in.read();

//        System.out.println("Token: " + (int)token);
//        in.mark(5);
        Atom atom = new Atom<String>(Token.ERROR, Integer.toString((int)token));
        if(isEndOfFile(token)){
            atom = new Atom<String>(Token.EOF);
        }
        if(isWhiteSpace(token)) {
//            token = (char)in.read();
            while(isWhiteSpace(token) ){
                in.mark(5);
                token = (char)in.read();
            }
            if(isEndOfFile(token)){
                atom = new Atom<String>(Token.EOF);
            }
        }
        if(isOpenParenthesis(token)){
            atom = new Atom<String>(Token.OPEN_PARENTHESIS);
        }
        if(isClosingParenthesis(token)){
            atom = new Atom<String>(Token.CLOSING_PARENTHESIS);
        }
        if(isDigit((char)token) || isUpperCaseLetter(token)){
            StringBuilder sb = new StringBuilder();

            while(isDigit(token) || isUpperCaseLetter(token)){
                sb.append(token);
                in.mark(5);
                token = (char)in.read();
            }
            in.reset();
            String newToken = sb.toString();

            if(newToken.matches("[A-Z]+[0-9]*[A-Z]*")){
                atom = new Atom<String>(Token.LITERAL, newToken);
//                System.out.println("literal:" + newToken);
            }else if(newToken.matches("[0-9]+")){
                atom = new Atom<Integer>(Token.NUMERIC, Integer.parseInt(newToken));
//                System.out.println("numeric:" + newToken);
            }else if(newToken.matches("[0-9]+[A-Z]+")){
                atom = new Atom<String>(Token.ERROR, newToken);
//                System.out.println("error digits followed by numebr: " + newToken);
            }
//            else{
//                System.out.println("unknown error" + newToken);
//            }

        }
        return atom;
    }

    public static void main(String[] args) throws IOException{
	// write your code here
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        BufferedReader in = new BufferedReader(new FileReader("inputFile"));
        List <String> literals = new ArrayList<String>();
        List <Integer> numerics = new ArrayList<Integer>();
        int numOpenParenthesis   = 0,
            numClosingParehtisis = 0;


        Atom atom = getNextToken(in);
        while(atom.getType().equals(Token.EOF) == false &&  atom.getType().equals(Token.ERROR) == false){
//            Token currentToken = atom.type;

            switch (atom.type){
                case LITERAL:
                    literals.add((String)atom.value);
                    break;
                case NUMERIC:
                    numerics.add((Integer)atom.value);
                    break;
                case OPEN_PARENTHESIS:
                    numOpenParenthesis +=1;
                    break;
                case CLOSING_PARENTHESIS:
                    numClosingParehtisis +=1;
                    break;
                case ERROR:
                    System.out.println("ERROR: Invalid token " + Integer.getInteger((String)atom.value));
                    return;

            }
            atom = getNextToken(in);

        }


        System.out.println("LITERAL ATOMS: " + literals.size() + ", " +
                literals.toString().substring(1, literals.toString().length()-1));
        System.out.println("NUMERIC ATOMS: " + numerics.size() +", " + numerics.stream().reduce(0, (a,b) -> a + b));
        System.out.println("OPEN PARENTHESES: " + numOpenParenthesis);
        System.out.println("CLOSING PARENTHESES: " + numClosingParehtisis);

    }
}
