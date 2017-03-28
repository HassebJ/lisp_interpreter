import java.lang.reflect.Method;
import java.util.*;
//import Scanner

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
@SuppressWarnings("unchecked")
public class Interpreter {


    BTreeImpl dlist;

    public Interpreter(){
        dlist = BTreeImpl.getTree("NIL");
    }

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

            //else{
                if(isTokenNIL(rightChildOfCurrentNode.token)){
                    listPrint(currentNode.leftNode);
                    System.out.print(")");
                }else{
                    listPrint(currentNode.leftNode);
                    System.out.print(" ");
                    System.out.print(". ");
                    System.out.print(rightChildOfCurrentNode.token.getValue());
                    System.out.print(")");
                }
            //}

        }
    }
    void prettyPrint(BTreeImpl input){
        listPrint(input.root);
        System.out.println();
    }


    BTreeImpl car(BTreeImpl input) throws UndefinedSExpressionException{
        Node root = input.root;
        if(root.leftNode == null && root.rightNode == null){
            throw new UndefinedSExpressionException("S-Expression contains only one node");
        }
        return new BTreeImpl(root.leftNode);
    }

    BTreeImpl caar(BTreeImpl input) throws UndefinedSExpressionException{
        return car(car(input));
    }

    BTreeImpl cadr(BTreeImpl input) throws UndefinedSExpressionException{
        return car(cdr(input));
    }

    BTreeImpl cddr(BTreeImpl input) throws UndefinedSExpressionException{
        return cdr(cdr(input));
    }


    BTreeImpl cdr(BTreeImpl input) throws UndefinedSExpressionException{
        Node root = input.root;
        if(root.leftNode == null && root.rightNode == null){
            throw new UndefinedSExpressionException("S-Expression contains only one node");
        }
//        BTreeImpl newBtree = new BTreeImpl();
//        newBtree.root = root.rightNode;
//        return newBtree;
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
            Token tokenS1 = s1.root.token;
            Token tokenS2 = s2.root.token;
            if ( tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC) &&
                    tokenS1.getValue().equals(tokenS2.getValue())) {
                return new BTreeImpl(new Node(new Token<String>(TokenType.LITERAL, "T")));
            }else if ( tokenS1.getType().equals(TokenType.LITERAL) && tokenS2.getType().equals(TokenType.LITERAL) &&
                    tokenS1.getValue().equals(tokenS2.getValue())) {
                return new BTreeImpl(new Node(new Token<String>(TokenType.LITERAL, "T")));
            }else{
                return new BTreeImpl(new Node(new Token<String>(TokenType.LITERAL, "NIL")));
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

        Token tokenS1 =  s1.root.token;
        Token tokenS2 = s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            int diff = (int) tokenS1.getValue() - (int) tokenS2.getValue();
            return new BTreeImpl(new Node(new Token(TokenType.NUMERIC, diff)));
        } else {
            throw new UndefinedSExpressionException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
    }

    public BTreeImpl bound(BTreeImpl x, BTreeImpl y) throws UndefinedSExpressionException{
        if(isTokenNIL(y.root.token)){
            return BTreeImpl.getTree("NIL");
        }
        if(isTokenT(eq(x, caar(y)).root.token)){
            return eq(x, caar(y));
        }
        return bound(x, cdr(y));
    }

    public BTreeImpl getVal(BTreeImpl x, BTreeImpl y) throws UndefinedSExpressionException{
        if(isTokenT(eq(x, caar(y)).root.token)){
            return cdr(car(y));
        }
        return getVal(x, cdr(y));
    }

    public BTreeImpl evlist(BTreeImpl x, BTreeImpl a) throws UndefinedSExpressionException{
        if(isTokenT(null_(x).root.token)){
            return new BTreeImpl(new Node(new Token(TokenType.LITERAL, "NIL")));
        }

        BTreeImpl firstParam = eval(car(x), a);
        BTreeImpl secondParam = evlist(cdr(x), a);
        return cons(firstParam, secondParam);
    }

    public BTreeImpl addPairs(BTreeImpl xList, BTreeImpl yList, BTreeImpl z) throws UndefinedSExpressionException{
        if(isTokenT(null_(xList).root.token)){
            return z;
        }
        BTreeImpl updatedZ = addPairs(cdr(xList), cdr(yList),z);
        return cons(cons(car(xList), car(yList)), updatedZ);
//        return addPairs(cdr(xList), cdr(yList), cons(cons(car(xList), car(yList)), z));
    }

    public BTreeImpl times (BTreeImpl s1, BTreeImpl s2) throws UndefinedSExpressionException {
//        Node root = input.root;

        Token tokenS1 =  s1.root.token;
        Token tokenS2 = s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            int prod = (int) tokenS1.getValue() * (int) tokenS2.getValue();
            return new BTreeImpl(new Node(new Token<Integer>(TokenType.NUMERIC, prod)));
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

    BTreeImpl eval(BTreeImpl input, BTreeImpl alist) throws UndefinedSExpressionException{
        if(isTokenT(atom_(input).root.token)){
        Node root = input.root;
            if (isTokenT(root.token) || isTokenNIL(root.token) || isTokenT(int_(input).root.token)) {
                return input;
            }else if (isTokenT(bound(input,alist).root.token)){
                return getVal(input, alist);
            }else{
                throw new UndefinedSExpressionException("Unbound literal.");
            }
        }

        BTreeImpl carOfInput =  car(input);

        if(isTokenFunc(carOfInput, "QUOTE")){

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
                    BTreeImpl resultCurrentCond = eval(car(currentCond), alist);
                    if(isTokenNIL(resultCurrentCond.root.token) == false){
                        BTreeImpl stmtCurrentCond = car(cdr(car(currentCondBranch)));
                        return eval(stmtCurrentCond, alist);
                    }
                }else{
                    throw new UndefinedSExpressionException("Expected S-Expression to be a LIST of 2 tokens");
                }
                currentCondBranch = cdr(currentCondBranch);
            }
            throw new UndefinedSExpressionException("None of the sub-conditions of COND evaluate to T");


        }else if(isTokenFunc(carOfInput, "DEFUN")){
            BTreeImpl funName = cadr(input);
            // check if function name is a reserved literal
            if(!isReservedLiteral(funName) && isTokenNIL(int_(funName).root.token)){
                BTreeImpl formalsList = car(cddr(input));
                // check if formals are in a list
                if(isTokenNIL(atom_(formalsList).root.token)){
                    ArrayList<String> formalsEncountered = new ArrayList<String>();
                    BTreeImpl currentFormalNode = formalsList;
                    // iterate through all formals
                    while(!isTokenNIL(currentFormalNode.root.token)){
//                        String currentFormalLiteral = (String)currentFormalNode.token.value;
                        BTreeImpl currentFormal = car(currentFormalNode);
                        // make sure formal is not reserved or not duplicate
                        if(isReservedLiteral(currentFormal) || formalsEncountered.contains((String)currentFormal.root.token.value)){
                            throw new UndefinedSExpressionException("Duplicate formal or formal is a reserved literal");
                        }
                        formalsEncountered.add((String)currentFormal.root.token.value);
                        currentFormalNode = cdr(currentFormalNode);
                    }
                    dlist = cons(cdr(input), dlist);
                    return BTreeImpl.getTree((String)funName.root.token.value.toString());

                }else{
                    throw new UndefinedSExpressionException("Expected formals to be a list");
                }


            }else{
                throw new UndefinedSExpressionException("Function name is a reserved literal or of numeric type.");
            }
        // handle the case for token corresponding to a function name (potentially)
        }else{
           return apply(input, evlist(cdr(input), alist), alist);
        }

    }

    boolean isReservedLiteral(BTreeImpl literal){
        String reserved = new String("T,NIL,CAR,CDR,CONS,ATOM,EQ,NULL,INT,PLUS,MINUS,TIMES,LESS,GREATER,COND,QUOTE,DEFUN");
        for( String reserve : reserved.split(",")){
            if(reserve.equals(literal.root.token.value)){
                return true;
            }
        }
        return false;
    }

    BTreeImpl apply(BTreeImpl input, BTreeImpl x, BTreeImpl alist) throws UndefinedSExpressionException{

        BTreeImpl carOfInput =  car(input);
        if(isTokenT(atom_(carOfInput).root.token)){
            if(isTokenFunc(carOfInput, "PLUS")
                    || isTokenFunc(car(input), "MINUS")
                    || isTokenFunc(car(input), "TIMES")
                    || isTokenFunc(car(input), "TIMES")
                    || isTokenFunc(car(input), "GREATER")
                    || isTokenFunc(car(input), "LESS")){
                if(input.length() == 3) {

                    BTreeImpl s1 = eval(car(cdr(input)), alist);
                    BTreeImpl s2 = eval(car(cdr(cdr(input))), alist);

                    if(isTokenType(s1.root.token, TokenType.NUMERIC) && isTokenType(s2.root.token, TokenType.NUMERIC)) {
                        String methodName = carOfInput.root.token.getValue().toString().toLowerCase();
                        try {
                            Method method = this.getClass().getMethod(methodName, BTreeImpl.class, BTreeImpl.class);
                            return (BTreeImpl)method.invoke(this, s1, s2);
//                        return (BTreeImpl)method.invoke(this, new Object[]{s1, s2});
                        } catch (Exception e) {
                            throw new UndefinedSExpressionException(e.getCause().toString().substring(e.getCause().toString().indexOf(":")+1));
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
                    BTreeImpl s1 = eval(car(cdr(input)), alist);
                    BTreeImpl s2 = eval(car(cdr(cdr(input))), alist);

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
                    BTreeImpl s1 = eval(car(cdr(input)), alist);
//                BTreeImpl s2 = eval(car(cdr(cdr(input))));
                    String methodName = carOfInput.root.token.getValue().toString().toLowerCase() + "_";
                    try {
                        Method method = this.getClass().getMethod(methodName, BTreeImpl.class);
                        return (BTreeImpl)method.invoke(this, s1);
//                        return (BTreeImpl)method.invoke(this, new Object[]{s1, s2});
                    } catch (Exception e) {
                        throw new UndefinedSExpressionException(e.getCause().toString().substring(e.getCause().toString().indexOf(":")+1));
                    }
                }else{
                    throw new UndefinedSExpressionException("Expected S-Expression to contain 2 tokens");
                }
            }
            else if(isTokenFunc(carOfInput, "CAR")
                    || isTokenFunc(car(input), "CDR")){
                if(input.length() == 2){
                    BTreeImpl s1 = eval(car(cdr(input)), alist);
                    if(isTokenType(s1.root.token, TokenType.NUMERIC) || isTokenType(s1.root.token, TokenType.LITERAL)){
                        String methodName = carOfInput.root.token.getValue().toString().toLowerCase();
                        try {
                            Method method = this.getClass().getDeclaredMethod(methodName, BTreeImpl.class);
                            return (BTreeImpl)method.invoke(this, s1);
                        } catch (Exception e) {
                            throw new UndefinedSExpressionException(e.getCause().toString().substring(e.getCause().toString().indexOf(":")+1));
//
                        }
                    }else{
                        throw new UndefinedSExpressionException("Expected S-Expression to be not an atom");
                    }

                }
            }
            else if (isTokenFunc(carOfInput, "CONS")) {
                if(input.length() == 3){
                    BTreeImpl s1 = eval(car(cdr(input)), alist);
                    BTreeImpl s2 = eval(car(cdr(cdr(input))), alist);
                    return cons(s1,s2);
                }else{
                    throw new UndefinedSExpressionException("Expected S-Expression to contain 3 tokens");
                }
            }else if(isTokenT(bound(carOfInput, dlist).root.token)){

                BTreeImpl getValOutput = getVal(carOfInput, dlist);
                if(car(getValOutput).length() == x.length()){

                    BTreeImpl functionBody = cadr(getValOutput);
                    BTreeImpl functionArguments = addPairs(car(getValOutput), x, alist);
                    return eval ( functionBody , functionArguments);
                }else{
                    throw new UndefinedSExpressionException("Function " +  carOfInput.toString()+ " called with different number of actuals compared to formals");
                }

            }else{
                throw new UndefinedSExpressionException("Function " +  carOfInput.toString()+ " is not defined");
            }

        }

        throw new UndefinedSExpressionException("Expected function name to be an atomic literal, found non-atomic");

    }

    public static void main (String [] args){
        Scanner.init();
        Interpreter intrprtr = new Interpreter();
        try{
            List <BTreeImpl> bTreeList = Parser.parseStart();
            BTreeImpl aList = new BTreeImpl(new Node(new Token(TokenType.LITERAL, "NIL")));
            for(BTreeImpl btr : bTreeList){
                BTreeImpl expToPrint = intrprtr.eval(btr, aList);
//                Parser.prettyPrint(expToPrint);
                intrprtr.prettyPrint(expToPrint);
            }
        }catch (Exception e){
            System.out.println(e);
        }


    }






}
