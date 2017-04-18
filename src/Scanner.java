import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

enum TokenType {
    LITERAL(1),
    NUMERIC(2),
    OPEN_PARENTHESIS('('),
    CLOSING_PARENTHESIS(')'),
    EOF(-1),
    ERROR(5);

    int value;
    TokenType(int value){
        this.value=value;
    }
}

class Token<T> {
    TokenType type;
    T value;
    Types abstractType = Types.LISTOFNATS;

    Token(TokenType type, T value){
        this.type = type;
        this.value = value;
        if (type.equals(TokenType.NUMERIC)) {
            this.abstractType = Types.NAT;
        }else if(value.equals("F") || value.equals("T")) {
            this.abstractType = Types.BOOL;
        }
//        else{
//
//            String reserved = new String("T,NIL,CAR,CDR,CONS,ATOM,EQ,NULL,INT,PLUS,MINUS,TIMES,LESS,GREATER,COND,QUOTE,DEFUN");
//            boolean isReserved = false;
//            for( String reserve : reserved.split(",")){
//                if(reserve.equals(value)){
//                    isReserved = true;
//                }
//            }
//            if(isReserved == false){
//
//            }
//
//        }

    }

    Token(Types type, T value){
        this.type = null;
        this.value = value;
        this.abstractType = type;
    }

    Token(TokenType type){
        this.type = type;
        this.value = null;
    }
    public boolean equalsVal(T val){
        return this.value.equals(val);
    }
    public boolean equalsType(TokenType val){
        if(this.type.equals(val)){
            return true;
        }else{
            return false;
        }
    }

    public boolean equalsAbstractType(Types val){
        return this.abstractType.equals(val);
    }

//
//    public  Object toString(){
//        return token;
//    }

    public TokenType getType(){
        return this.type;
    }

    public Types getAbstractType(){
        return this.abstractType;
    }

    public T getValue(){
        return this.value;
    }


}

public final class Scanner {

    static Token current;
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

//    //hardcoded input file for debugging purposes
//    static BufferedReader in ;
//    static {
//        try{
//            in =  new BufferedReader(new FileReader("inputFile"));
//        }catch (Exception e){
//            System.out.println(e);
//        }
//
//    }


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

    public static Token getCurrent (){
        return current;
    }

    static boolean isWhiteSpace(char token){
        return token == 32 || token == 13 || token == 10;
    }
    static boolean isEndOfFile(char token){
        return token == '\uFFFF';
    }

    static void moveToNext() {
        if(!current.getType().equals(TokenType.ERROR)){
            current = getNextToken();
        }

    }

    public static Token getNextToken (){

        try {
            char currentChar = (char)in.read();
            Token token = new Token<String>(TokenType.ERROR, ""+currentChar);
            if(isEndOfFile(currentChar)){
                token = new Token<String>(TokenType.EOF);
            }
            if(isWhiteSpace(currentChar)) {
                while(isWhiteSpace(currentChar) ){
                    in.mark(5);
                    currentChar = (char)in.read();
                }
                if(isEndOfFile(currentChar)){
                    token = new Token<String>(TokenType.EOF);
                }
            }
            if(isOpenParenthesis(currentChar)){
                token = new Token<String>(TokenType.OPEN_PARENTHESIS);
            }
            if(isClosingParenthesis(currentChar)){
                token = new Token<String>(TokenType.CLOSING_PARENTHESIS);
            }
            if(isDigit((char)currentChar) || isUpperCaseLetter(currentChar)){
                StringBuilder sb = new StringBuilder();

                while(isDigit(currentChar) || isUpperCaseLetter(currentChar)){
                    sb.append(currentChar);
                    in.mark(5);
                    currentChar = (char)in.read();
                }
                in.reset();
                String newToken = sb.toString();

                if(newToken.matches("[A-Z]+[0-9]*[A-Z]*")){
                    token = new Token<String>(TokenType.LITERAL, newToken);
                }else if(newToken.matches("[0-9]+")){
                    token = new Token<Integer>(TokenType.NUMERIC, Integer.parseInt(newToken));
                }else //(newToken.matches("[0-9]+[A-Z]+")){
                {
                    token = new Token<String>(TokenType.ERROR, newToken);
                }


            }
            return token;
        }catch(IOException e){
            System.out.println(e);

        }
        return null;
    }

    public static void init(){
        current = getNextToken();
    }

//    public static void main(String[] args){
//
////        BufferedReader in = new BufferedReader(new FileReader("inputFile"));
//        List <String> literals = new ArrayList<String>();
//        List <Integer> numerics = new ArrayList<Integer>();
//        int numOpenParenthesis   = 0,
//            numClosingParehtisis = 0;
//
//
//        Token token = Scanner.getNextToken();
//        while(token.getType().equals(TokenType.EOF) == false &&  token.getType().equals(TokenType.ERROR) == false){
//
//            switch (token.type){
//                case LITERAL:
//                    literals.add((String) token.token);
//                    break;
//                case NUMERIC:
//                    numerics.add((Integer) token.token);
//                    break;
//                case OPEN_PARENTHESIS:
//                    numOpenParenthesis +=1;
//                    break;
//                case CLOSING_PARENTHESIS:
//                    numClosingParehtisis +=1;
//                    break;
//                case ERROR:
//                    System.out.println("ERROR: Invalid token " + Integer.getInteger((String) token.token));
//                    return;
//
//            }
//            token = getNextToken();
//
//        }
//
//
//        System.out.println("LITERAL ATOMS: " + literals.size() + ", " +
//                literals.toString().substring(1, literals.toString().length() - 1));
//        System.out.println("NUMERIC ATOMS: " + numerics.size() +", " + numerics.stream().reduce(0, (a, b) -> a + b));
//        System.out.println("OPEN PARENTHESES: " + numOpenParenthesis);
//        System.out.println("CLOSING PARENTHESES: " + numClosingParehtisis);
//
//    }
}
