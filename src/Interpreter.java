import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by mac on 2/13/17.
 */
class UndefinedSExpressionException extends Exception{
    String message;
    UndefinedSExpressionException(String str2) {
        message = str2;
    }
    public String toString(){
        return ("UndefinedSExpressionException : " + message ) ;
    }
}

public class Interpreter {

    void listPrint(Node currentNode){
        if(currentNode.leftNode == null && currentNode.rightNode == null){
            System.out.print(currentNode.token.getValue());
        }else{
            System.out.print("(");
            Node rightChildOfCurrentNode = currentNode.rightNode;
            while(rightChildOfCurrentNode.leftNode != null && rightChildOfCurrentNode.rightNode != null){
                listPrint(currentNode.leftNode);
                System.out.print(" ");
                currentNode = rightChildOfCurrentNode;
                rightChildOfCurrentNode = rightChildOfCurrentNode.rightNode;


            }
            listPrint(currentNode.leftNode);
            System.out.print(" ");
            //else{
                if(isTokenNIL(rightChildOfCurrentNode.token)){
                    System.out.print(")");
                }else{
                    System.out.print(". ");
                    System.out.print(rightChildOfCurrentNode.token.getValue());
                    System.out.print(")");
                }
            //}

        }
    }
    void prettyPrint(BTreeImpl input){
        listPrint(input.root);
    }


    BTreeImpl car(BTreeImpl input) throws UndefinedSExpressionException{
        Node root = input.root;
        if(root.leftNode == null && root.rightNode == null){
            throw new UndefinedSExpressionException("S-Expression contains only one node");
        }
        return new BTreeImpl(root.leftNode);
    }
    BTreeImpl cdr(BTreeImpl input) throws UndefinedSExpressionException{
        Node root = input.root;
        if(root.leftNode == null && root.rightNode == null){
            throw new UndefinedSExpressionException("S-Expression contains only one node");
        }
        return new BTreeImpl(root.rightNode);
    }

    BTreeImpl cons(BTreeImpl s1, BTreeImpl s2){
        BTreeImpl s = new BTreeImpl();
        s.root.leftNode = s1.root;
        s.root.rightNode = s2.root;
        return s;
    }

    public BTreeImpl atom_(BTreeImpl input){
        Node root = input.root;
        String resultAtom = "NIL";
        if(root.leftNode == null && root.rightNode == null){
             resultAtom = "T";
        }
        return new BTreeImpl(new Node(new Token(TokenType.LITERAL,resultAtom)));
    }

    public BTreeImpl int_ (BTreeImpl input){
        Node root = input.root;
        if(root.leftNode == null && root.rightNode == null) {
            Token token = (Token) root.token;
            if (token.getType().equals(TokenType.NUMERIC)) {
                return new BTreeImpl(new Node(new Token(TokenType.LITERAL, "T")));
            }
        }
        return new BTreeImpl(new Node(new Token(TokenType.LITERAL,"NIL")));

    }
    public BTreeImpl null_ (BTreeImpl input){
        Node root = input.root;
        if(root.leftNode == null && root.rightNode == null) {
            Token token = (Token) root.token;
            if (token.getType().equals(TokenType.LITERAL) && token.getValue().equals("NIL")) {
                return new BTreeImpl(new Node(new Token(TokenType.LITERAL, "T")));
            }
        }
        return new BTreeImpl(new Node(new Token(TokenType.LITERAL,"NIL")));

    }

    public BTreeImpl eq (BTreeImpl s1, BTreeImpl s2) throws UndefinedSExpressionException{
//        Node root = input.root;
        if(s1.root.leftNode == null && s1.root.rightNode == null && s2.root.leftNode == null && s2.root.rightNode == null ) {
            Token tokenS1 = (Token)s1.root.token;
            Token tokenS2 = (Token)s2.root.token;
            if ( tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC) &&
                    tokenS1.getValue().equals(tokenS2.getValue())) {
                return new BTreeImpl(new Node(new Token(TokenType.LITERAL, "T")));
            }else if ( tokenS1.getType().equals(TokenType.LITERAL) && tokenS2.getType().equals(TokenType.LITERAL) &&
                    tokenS1.getValue().equals(tokenS2.getValue())) {
                return new BTreeImpl(new Node(new Token(TokenType.LITERAL, "T")));
            }else{
                return new BTreeImpl(new Node(new Token(TokenType.LITERAL, "NIL")));
            }

        }else{
            throw new UndefinedSExpressionException("Atleast one of the input S-Expressions has more than one node OR S-Expression not an atom.");
        }


    }

    public BTreeImpl plus (BTreeImpl s1, BTreeImpl s2) throws UndefinedSExpressionException {
//    BTreeImpl plus () throws UndefinedSExpressionException {
//        Node root = input.root;

        Token tokenS1 = (Token) s1.root.token;
        Token tokenS2 = (Token) s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            int sum = (int) tokenS1.getValue() + (int) tokenS2.getValue();
            return new BTreeImpl(new Node(new Token(TokenType.NUMERIC, sum)));
        } else {
            throw new UndefinedSExpressionException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
//        throw new UndefinedSExpressionException("Atleast one of the input S-Expressions is not NUMERIC.");
    }

    public BTreeImpl minus (BTreeImpl s1, BTreeImpl s2) throws UndefinedSExpressionException {
//        Node root = input.root;

        Token tokenS1 = (Token) s1.root.token;
        Token tokenS2 = (Token) s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            int diff = (int) tokenS1.getValue() - (int) tokenS2.getValue();
            return new BTreeImpl(new Node(new Token(TokenType.NUMERIC, diff)));
        } else {
            throw new UndefinedSExpressionException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
    }

    public BTreeImpl times (BTreeImpl s1, BTreeImpl s2) throws UndefinedSExpressionException {
//        Node root = input.root;

        Token tokenS1 = (Token) s1.root.token;
        Token tokenS2 = (Token) s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            int prod = (int) tokenS1.getValue() * (int) tokenS2.getValue();
            return new BTreeImpl(new Node(new Token(TokenType.NUMERIC, prod)));
        } else {
            throw new UndefinedSExpressionException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
    }

    public BTreeImpl less (BTreeImpl s1, BTreeImpl s2) throws UndefinedSExpressionException {
//        Node root = input.root;

        Token tokenS1 = (Token) s1.root.token;
        Token tokenS2 = (Token) s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            String t = ((int) tokenS1.getValue() < (int) tokenS2.getValue() ) ? "T" :  "NIL";
            return new BTreeImpl(new Node(new Token(TokenType.LITERAL, t)));
        } else {
            throw new UndefinedSExpressionException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
    }

    public BTreeImpl greater (BTreeImpl s1, BTreeImpl s2) throws UndefinedSExpressionException {
//        Node root = input.root;

        Token tokenS1 = (Token) s1.root.token;
        Token tokenS2 = (Token) s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            String t = ((int) tokenS1.getValue() > (int) tokenS2.getValue() ) ? "T" :  "NIL";
            return new BTreeImpl(new Node(new Token(TokenType.LITERAL, t)));
        } else {
            throw new UndefinedSExpressionException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
    }
    boolean isTokenT(Token s1){
        if(s1.equalsType(TokenType.LITERAL) && s1.equalsVal("T")){
            return true;
        }else{
            return false;
        }
    }
    boolean isTokenNIL(Token token){
        if(token.equalsType(TokenType.LITERAL) && token.equalsVal("NIL")){
            return true;
        }else{
            return false;
        }
    }
    boolean isTokenType(Token token, TokenType tokenType){
        return token.equalsType(tokenType);
    }
    boolean isTokenFunc(BTreeImpl input, String funcName){

        return input.root.token.equalsVal(funcName);
    }

    BTreeImpl eval(BTreeImpl input) throws UndefinedSExpressionException{
        Node root = input.root;
        if (isTokenT(root.token) || isTokenNIL(root.token) || isTokenT(int_(input).root.token)) {
            return input;
        }
        BTreeImpl carOfInput =  car(input);
        if(isTokenFunc(carOfInput, "PLUS")
                || isTokenFunc(car(input), "MINUS")
                || isTokenFunc(car(input), "TIMES")
                || isTokenFunc(car(input), "TIMES")
                || isTokenFunc(car(input), "GREATER")
                || isTokenFunc(car(input), "LESS")){
            if(input.length() == 3) {

                BTreeImpl s1 = eval(car(cdr(input)));
                BTreeImpl s2 = eval(car(cdr(cdr(input))));

                if(isTokenType(s1.root.token, TokenType.NUMERIC) && isTokenType(s2.root.token, TokenType.NUMERIC)) {
                    String methodName = carOfInput.root.token.getValue().toString().toLowerCase();
                    try {
                        Method method = this.getClass().getMethod(methodName, BTreeImpl.class, BTreeImpl.class);
                        return (BTreeImpl)method.invoke(this, s1, s2);
//                        return (BTreeImpl)method.invoke(this, new Object[]{s1, s2});
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }else{
                    throw new UndefinedSExpressionException("Expected ATOM to be of type Numeric");
                }

            }else{
                throw new UndefinedSExpressionException("Expected S-Expression to contain 3 tokens");
            }

        }
        else if (isTokenFunc(carOfInput, "EQ")) {

            if(input.length() == 3){
                BTreeImpl s1 = eval(car(cdr(input)));
                BTreeImpl s2 = eval(car(cdr(cdr(input))));

                if(isTokenType(s1.root.token, TokenType.NUMERIC) || isTokenType(s1.root.token, TokenType.LITERAL)
                        && isTokenType(s2.root.token, TokenType.NUMERIC) || isTokenType(s2.root.token, TokenType.LITERAL)) {
                    return eq(s1, s2);
                }else{
                    throw new UndefinedSExpressionException("At least one S-Expression is not an atom");
                }
            }else{
                throw new UndefinedSExpressionException("Expected S-Expression to contain 2 tokens");
            }
        }
        else if(isTokenFunc(carOfInput, "ATOM")
                || isTokenFunc(car(input), "INT")
                || isTokenFunc(car(input), "NULL")){
            if(input.length() == 2){
                BTreeImpl s1 = eval(car(cdr(input)));
//                BTreeImpl s2 = eval(car(cdr(cdr(input))));
                String methodName = carOfInput.root.token.getValue().toString().toLowerCase() + "_";
                try {
                    Method method = this.getClass().getMethod(methodName, BTreeImpl.class);
                    return (BTreeImpl)method.invoke(this, s1);
//                        return (BTreeImpl)method.invoke(this, new Object[]{s1, s2});
                } catch (Exception e) {
                    System.out.println(e);
                }
            }else{
                throw new UndefinedSExpressionException("Expected S-Expression to contain 2 tokens");
            }
        }
        else if(isTokenFunc(carOfInput, "CAR")
                || isTokenFunc(car(input), "CDR")){
            if(input.length() == 2){
                BTreeImpl s1 = eval(car(cdr(input)));
                if(isTokenType(s1.root.token, TokenType.NUMERIC) || isTokenType(s1.root.token, TokenType.LITERAL)){
                    String methodName = carOfInput.root.token.getValue().toString().toLowerCase();
                    try {
                        Method method = this.getClass().getDeclaredMethod(methodName, BTreeImpl.class);
                        return (BTreeImpl)method.invoke(this, s1);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }else{
                }
                throw new UndefinedSExpressionException("Expected S-Expression to be not an atom");
            }
        }
        else if (isTokenFunc(carOfInput, "CONS")) {
            if(input.length() == 3){
                BTreeImpl s1 = eval(car(cdr(input)));
                BTreeImpl s2 = eval(car(cdr(cdr(input))));
                return cons(s1,s2);
            }else{
                throw new UndefinedSExpressionException("Expected S-Expression to contain 3 tokens");
            }
        }else if(isTokenFunc(carOfInput, "QUOTE")){

            if(input.length() == 2){
                return car(cdr(input));
            }else{
                throw new UndefinedSExpressionException("Expected S-Expression to contain 2 tokens");
            }
        }else if(isTokenFunc(carOfInput, "COND")){

            BTreeImpl currentCondBranch = cdr(input);

            while(isTokenNIL(currentCondBranch.root.token) == false){
                BTreeImpl currentCond = car(currentCondBranch);
                if(currentCond.length() == 2){
                    BTreeImpl resultCurrentCond = eval(car(currentCond));
                    if(isTokenT(resultCurrentCond.root.token)){
                        BTreeImpl stmtCurrentCond = car(cdr(car(currentCondBranch)));
                        return eval(stmtCurrentCond);
                    }
                }else{
                    throw new UndefinedSExpressionException("Expected S-Expression to be a LIST of 2 tokens");
                }
                currentCondBranch = cdr(currentCondBranch);
            }
            throw new UndefinedSExpressionException("Expected S-Expression to be a LIST of 2 tokens");


        }
        throw new UndefinedSExpressionException("Unexpected S-Expression.");
    }

    public static void main (String [] args){
        Scanner.init();
        Interpreter intrprtr = new Interpreter();
        try{
            List <BTreeImpl> bTreeList = Parser.parseStart();
            for(BTreeImpl btr : bTreeList){
                BTreeImpl expToPrint = intrprtr.eval(btr);
//                Parser.prettyPrint(expToPrint);
                intrprtr.prettyPrint(expToPrint);
            }
        }catch (Exception e){
            System.out.println(e);
        }


    }






}
