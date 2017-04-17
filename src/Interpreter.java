import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.*;
//import Scanner

/**
 * Created by mac on 2/13/17.
 */
enum Types {NAT, BOOL, LISTOFNATS}
class TypeException extends Exception{
    String message;
    TypeException(String str2) {
        message = str2;
    }
    public String toString(){
        return ("TYPE ERROR: " + message ) ;
    }
}

class ListException extends Exception{
    String message;
    ListException(String str2) {
        message = str2;
    }
    public String toString(){
        return ("EMPTY LIST ERROR: " + message ) ;
    }
}
@SuppressWarnings("unchecked")
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

    boolean equalsAbstractType(BTreeImpl input, Types type){
        return input.root.token.equalsAbstractType(type);
    }

    boolean equalsAbstractTypeAndValue(BTreeImpl input, Types type, Object val){
        return input.root.token.equalsAbstractType(type) && input.root.token.equalsVal(val);
    }

    BTreeImpl getTreeOfAbstractType(Token token){
        return new BTreeImpl(new Node(token));
    }

    boolean areTreesSameAbstractType(BTreeImpl s1, BTreeImpl s2){
        return s1.root.token.equalsAbstractType(s2.root.token.getAbstractType());
    }

    BTreeImpl addAbstractTypeToTree(Node root, Types type){
        BTreeImpl tree = new BTreeImpl(root);
        tree.root.token.abstractType = type;
        return tree;
    }


    BTreeImpl car(BTreeImpl input) throws TypeException {
        Node root = input.root;
        if((root.leftNode == null && root.rightNode == null) && equalsAbstractType(input, Types.LISTOFNATS)){
            return input;
        }
        if((root.leftNode == null && root.rightNode == null) || !equalsAbstractType(input, Types.LISTOFNATS)){
            throw new TypeException("S-Expression is not of type List(Nat)");
        }

        return new BTreeImpl(root.leftNode);
//        return addAbstractTypeToTree(root.leftNode, Types.NAT);
    }
    BTreeImpl CAR(BTreeImpl input) throws TypeException {
        Node root = input.root;
        if(equalsAbstractType(input, Types.LISTOFNATS)){
            return getTreeOfAbstractType(new Token(Types.NAT, "AnyInt"));
        }else{
            throw new TypeException("CAR called on type NOT List(Nat).");
        }

    }
    BTreeImpl carDash(BTreeImpl input) throws ListException {
        Node root = input.root;
        if((int)root.token.value > 0 && equalsAbstractType(input, Types.LISTOFNATS)){
            return getTreeOfAbstractType(new Token(Types.NAT, "AnyInt"));
        }else{
            throw new ListException("CAR on empty list.");
        }

    }
    BTreeImpl cdr(BTreeImpl input) throws TypeException {
        Node root = input.root;
        if((root.leftNode == null && root.rightNode == null) && equalsAbstractType(input, Types.LISTOFNATS)){
            return input;
        }
        if((root.leftNode == null && root.rightNode == null) || !equalsAbstractType(input, Types.LISTOFNATS)){
            throw new TypeException("S-Expression is not of type List(Nat)");
        }
        return new BTreeImpl(root.rightNode);
//        return addAbstractTypeToTree(root.rightNode, Types.LISTOFNATS);
    }

    BTreeImpl CDR(BTreeImpl input) throws TypeException {
        Node root = input.root;
        if(equalsAbstractType(input, Types.LISTOFNATS)){
            return getTreeOfAbstractType(new Token(Types.LISTOFNATS, "AnyNat"));
        }else{
            throw new TypeException("CDR on operand not of type List(Nat).");
        }
    }

    BTreeImpl cdrDash(BTreeImpl input) throws ListException {
        Node root = input.root;
        if((int)root.token.value > 0 && equalsAbstractType(input, Types.LISTOFNATS)){
            return getTreeOfAbstractType(new Token(Types.LISTOFNATS, (int)root.token.getValue() - 1));
        }else{
            throw new ListException("CDR on empty list.");
        }
    }

    public BTreeImpl cons(BTreeImpl s1, BTreeImpl s2)  throws TypeException {
        if( equalsAbstractType(s1, Types.NAT) &&  equalsAbstractType(s2, Types.LISTOFNATS)){
            Token newToken;
            if(s2.root.token.getValue() instanceof String){
                newToken = new Token(Types.LISTOFNATS, 1);
//                return getTreeOfAbstractType(new Token(Types.LISTOFNATS, 1));
            }else{
                newToken = new Token(Types.LISTOFNATS, (int)s2.root.token.getValue() + 1);
//                return getTreeOfAbstractType(new Token(Types.LISTOFNATS, (int)s2.root.token.getValue() + 1));
            }
            BTreeImpl s = new BTreeImpl();
            s.root.leftNode = s1.root;
            s.root.rightNode = s2.root;
            s.root.token = newToken;
            return s;

        }else{
            throw new TypeException("S-Expressions are not of expected types");
        }

    }
    public BTreeImpl consDash(BTreeImpl s1, BTreeImpl s2)  throws TypeException {
        if( equalsAbstractTypeAndValue(s1, Types.NAT, "AnyInt") &&  equalsAbstractType(s2, Types.LISTOFNATS)) {
            if (s2.root.token.getValue() instanceof String) {
                return getTreeOfAbstractType(new Token(Types.LISTOFNATS, 1));
            } else {
                return getTreeOfAbstractType(new Token(Types.LISTOFNATS, (int) s2.root.token.getValue() + 1));
            }
        }else{
            throw new TypeException("S-Expressions are not of expected types");
        }


    }

    public BTreeImpl atom_ (BTreeImpl input) throws TypeException {
        Node root = input.root;
        String resultAtom = "NIL";
        if(!(equalsAbstractType(input, Types.BOOL)
                || equalsAbstractType(input, Types.LISTOFNATS)
                || equalsAbstractType(input, Types.NAT) ) ){
            throw new TypeException("S-Expressions are not of expected types");
        }else{
            if(root.token.equalsVal("NIL")){
                return getTreeOfAbstractType(new Token(Types.BOOL, "T"));
            }else if(equalsAbstractType(input, Types.LISTOFNATS) ){
                return getTreeOfAbstractType(new Token(Types.BOOL, "F"));
            }else{
                return getTreeOfAbstractType(new Token(Types.BOOL, "T"));
            }
        }

    }

    public BTreeImpl atomDash (BTreeImpl input) throws TypeException {
        Node root = input.root;
        String resultAtom = "NIL";
        if(equalsAbstractType(input, Types.LISTOFNATS)){
            return getTreeOfAbstractType(new Token(Types.BOOL, "False"));
        }else{
            return getTreeOfAbstractType(new Token(Types.BOOL, "True"));
        }

    }



    public BTreeImpl null_ (BTreeImpl input) throws TypeException {
        Node root = input.root;
        String resultAtom = "NIL";
        if(!equalsAbstractType(input, Types.LISTOFNATS)){
            throw new TypeException("S-Expression is not of expected types");
        }else{
            if(root.token.getValue() instanceof String){
                return getTreeOfAbstractType(new Token(Types.BOOL, "T"));
            }else if(equalsAbstractType(input, Types.LISTOFNATS) && (int)input.root.token.getValue() == 0){
                return getTreeOfAbstractType(new Token(Types.BOOL, "T"));
            }else{
                return getTreeOfAbstractType(new Token(Types.BOOL, "F"));
            }
        }

    }
    public BTreeImpl nullDash (BTreeImpl input) throws TypeException {
        Node root = input.root;
        String resultAtom = "NIL";
        if(equalsAbstractTypeAndValue(input, Types.LISTOFNATS, 0)){
            return getTreeOfAbstractType(new Token(Types.BOOL, "AnyBool"));
        }else{
            return getTreeOfAbstractType(new Token(Types.BOOL, "False"));
        }

    }
    public BTreeImpl int_ (BTreeImpl input) throws TypeException {
        Node root = input.root;
        String resultAtom = "NIL";
        if(!(equalsAbstractType(input, Types.BOOL)
                || equalsAbstractType(input, Types.LISTOFNATS)
                || equalsAbstractType(input, Types.NAT) ) ){
            throw new TypeException("S-Expressions are not of expected types");
        }else{
            if(equalsAbstractType(input, Types.NAT)){
                return getTreeOfAbstractType(new Token(Types.BOOL, "T"));
            }else{
                return getTreeOfAbstractType(new Token(Types.BOOL, "F"));
            }
        }

    }

    public BTreeImpl intDash (BTreeImpl input) throws TypeException {
        Node root = input.root;
        String resultAtom = "NIL";
        if(equalsAbstractTypeAndValue(input, Types.NAT, "AnyNat")){
            return getTreeOfAbstractType(new Token(Types.BOOL, "True"));
        }else{
            return getTreeOfAbstractType(new Token(Types.BOOL, "False"));
        }

    }

    public BTreeImpl eq (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;
        if(equalsAbstractType(s1, Types.NAT ) && equalsAbstractType(s2, Types.NAT) ) {
            Token tokenS1 = s1.root.token;
            Token tokenS2 = s2.root.token;
            if ( tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC) &&
                    tokenS1.getValue().equals(tokenS2.getValue())) {
                return new BTreeImpl(new Node(new Token<String>(TokenType.LITERAL, "T")));
            }else{
                return new BTreeImpl(new Node(new Token<String>(TokenType.LITERAL, "F")));
            }
        }else{
            throw new TypeException("At least one of the input S-Expressions is not of type Nat");
        }


    }

    public BTreeImpl eqDash (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;
        String resultAtom = "NIL";
        if(equalsAbstractType(s1, Types.NAT) && equalsAbstractType(s2, Types.NAT)){
            return getTreeOfAbstractType(new Token(Types.BOOL, "AnyBool"));
        }else{
            throw new TypeException("Wrong types for operands of EQ");
        }

    }

    public BTreeImpl plus (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;
        if(equalsAbstractType(s1, Types.NAT) && equalsAbstractType(s2, Types.NAT) ) {
            Token tokenS1 = s1.root.token;
            Token tokenS2 = s2.root.token;
//            if ( tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC) &&
//                    tokenS1.getValue().equals(tokenS2.getValue())) {
                return new BTreeImpl(new Node(new Token<String>(Types.NAT, null)));
//            }else{
//                return new BTreeImpl(new Node(new Token<String>(TokenType.LITERAL, "F")));
//            }
        }else{
            throw new TypeException("At least one of the input S-Expressions is not of type Nat");
        }


    }

    public BTreeImpl plusDash (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;
        String resultAtom = "NIL";
        if(equalsAbstractType(s1, Types.NAT) && equalsAbstractType(s2, Types.NAT)){
            return getTreeOfAbstractType(new Token(Types.NAT, "AnyNat"));
        }else{
            throw new TypeException("Wrong types for operands of PLUS");
        }

    }

    public BTreeImpl minus (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;

        Token tokenS1 =  s1.root.token;
        Token tokenS2 = s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            int diff = (int) tokenS1.getValue() - (int) tokenS2.getValue();
            return new BTreeImpl(new Node(new Token(TokenType.NUMERIC, diff)));
        } else {
            throw new TypeException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
    }

    public BTreeImpl times (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;

        Token tokenS1 =  s1.root.token;
        Token tokenS2 = s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            int prod = (int) tokenS1.getValue() * (int) tokenS2.getValue();
            return new BTreeImpl(new Node(new Token<Integer>(TokenType.NUMERIC, prod)));
        } else {
            throw new TypeException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
    }

    public BTreeImpl less (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;
        if (equalsAbstractType(s1, Types.NAT) && equalsAbstractType(s2, Types.NAT) ) {
            Token tokenS1 = s1.root.token;
            Token tokenS2 = s2.root.token;
//            if ( tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC) &&
//                    tokenS1.getValue().equals(tokenS2.getValue())) {
            return new BTreeImpl(new Node(new Token<String>(Types.NAT, null)));
//            }else{
//                return new BTreeImpl(new Node(new Token<String>(TokenType.LITERAL, "F")));
//            }
        }else{
            throw new TypeException("At least one of the input S-Expressions is not of type Nat");
        }


    }
    public BTreeImpl lessDash (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;
        String resultAtom = "NIL";
        if(equalsAbstractType(s1, Types.NAT) && equalsAbstractType(s2, Types.NAT)){
            return getTreeOfAbstractType(new Token(Types.BOOL, "AnyBool"));
        }else{
            throw new TypeException("Wrong types for operands of LESS");
        }

    }

    public BTreeImpl greater (BTreeImpl s1, BTreeImpl s2) throws TypeException {
//        Node root = input.root;

        Token tokenS1 = (Token) s1.root.token;
        Token tokenS2 = (Token) s2.root.token;
        if (tokenS1.getType().equals(TokenType.NUMERIC) && tokenS2.getType().equals(TokenType.NUMERIC)) {
            String t = ((int) tokenS1.getValue() > (int) tokenS2.getValue() ) ? "T" :  "NIL";
            return new BTreeImpl(new Node(new Token(TokenType.LITERAL, t)));
        } else {
            throw new TypeException("Atleast one of the input S-Expressions is not NUMERIC.");
        }
    }
    boolean isTokenT(Token s1){
        if(s1.equalsVal("T")){
            return true;
        }else{
            return false;
        }
    }
    boolean isTokenNIL(Token token) {
        if (token.equalsType(TokenType.LITERAL) && token.equalsVal("NIL")){
            return true;
        }else{
            return false;
        }
    }

    boolean isTokenF(Token token){
        if(token.equalsVal("F")){
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

    BTreeImpl eval(BTreeImpl input) throws TypeException {

        if(isTokenT(atom_(input).root.token)) {

            Node root = input.root;
            if (isTokenT(root.token) || isTokenF(root.token) || isTokenNIL(root.token) || isTokenT(int_(input).root.token)) {
                return input;
            }else{
                throw new TypeException("Unacceptable use of an atom.");
            }
        }
        if(input.length() >= 2){
            BTreeImpl carOfInput = car(input);
            if (isTokenFunc(carOfInput, "PLUS")
                    || isTokenFunc(car(input), "LESS")
                    || isTokenFunc(carOfInput, "EQ")
                    || isTokenFunc(carOfInput, "CONS")) {
                if (input.length() == 3) {
                try {
                        BTreeImpl s1 = eval(car(cdr(input)));
                        BTreeImpl s2 = eval(car(cdr(cdr(input))));

                        String methodName = carOfInput.root.token.getValue().toString().toLowerCase();
                        Method method = this.getClass().getDeclaredMethod(methodName, BTreeImpl.class, BTreeImpl.class);
                        return (BTreeImpl) method.invoke(this, s1, s2);
                    } catch (TypeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new TypeException(e.getCause().toString().substring(e.getCause().toString().indexOf(":") + 1));
                    }
                } else {
                    throw new TypeException("Expected S-Expression to contain exactly 3 tokens");
                }

            } else if (isTokenFunc(carOfInput, "ATOM")
                    || isTokenFunc(car(input), "INT")
                    || isTokenFunc(car(input), "NULL")
                    || isTokenFunc(carOfInput, "CAR")
                    || isTokenFunc(car(input), "CDR")) {
                if (input.length() == 2) {

                    try {
                        BTreeImpl s1 = eval(car(cdr(input)));
                        String methodName = carOfInput.root.token.getValue().toString().toLowerCase();
                        if(!methodName.equals("car") && !methodName.equals("cdr")){
                            methodName = methodName + "_";
                        }else{
                            methodName = methodName.toUpperCase();
                        }
                        Method method = this.getClass().getDeclaredMethod(methodName, BTreeImpl.class);
                        return (BTreeImpl) method.invoke(this, s1);
                    }catch (TypeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new TypeException(e.getCause().toString().substring(e.getCause().toString().indexOf(":") + 1));
                    }
                } else {
                    throw new TypeException("Expected S-Expression to contain 2 tokens");
                }
            } else if (isTokenFunc(carOfInput, "COND")) {

                BTreeImpl currentCondBranch = cdr(input);
                BTreeImpl stmtCurrentCond = eval(car(cdr(car(currentCondBranch))));
                BTreeImpl ans = null;

                while (isTokenNIL(currentCondBranch.root.token) == false) {
                    BTreeImpl currentCond = car(currentCondBranch);
                    if (currentCond.length() == 2 && isTokenF(atom_(currentCond).root.token)) {
                        BTreeImpl resultCurrentCond = eval(car(currentCond));
                        if (isTokenT(resultCurrentCond.root.token) || isTokenF(resultCurrentCond.root.token )) {

                            try {
                                BTreeImpl temp = eval(car(cdr(car(currentCondBranch))));
                                if(!areTreesSameAbstractType(temp, stmtCurrentCond)){
                                    throw new TypeException("Type mismatch between expressions of COND.");
                                }
                                if(ans == null){
                                    ans = temp;
                                }

                                currentCondBranch = cdr(currentCondBranch);
//                                return eval(stmtCurrentCond);j
                            }catch (TypeException e) {
                                throw e;
                            }
                        }else{
                            throw new TypeException("Sub-conditions of COND evaluate to non-boolean value");
                        }
                    } else {
                        throw new TypeException("Expected S-Expression to be a LIST of 2 tokens");
                    }

                }
                return ans;
//                throw new TypeException("None of the sub-conditions of COND evaluate to T");


            }
        }
        throw new TypeException("List of length >= 2 expected at: " + prettyPrinToString(input));

    }

    BTreeImpl evalDash(BTreeImpl input) throws Exception {
        if(isTokenT(atom_(input).root.token)) {

            Node root = input.root;
            if (isTokenT(root.token) ){
                return getTreeOfAbstractType(new Token(Types.BOOL, "True"));
            }else if( isTokenF(root.token)){
                return getTreeOfAbstractType(new Token(Types.BOOL, "False"));
            }else if( isTokenNIL(root.token)){
                return getTreeOfAbstractType(new Token(Types.LISTOFNATS, 0));
            }else if(isTokenT(int_(input).root.token)) {
                return getTreeOfAbstractType(new Token(Types.NAT, "AnyInt"));
            }else{
                throw new TypeException("Unacceptable use of an atom.");
            }
        }
        BTreeImpl carOfInput = car(input);
        if (isTokenFunc(carOfInput, "PLUS")
                || isTokenFunc(car(input), "LESS")
                || isTokenFunc(carOfInput, "EQ")
                || isTokenFunc(carOfInput, "CONS")) {
            if (input.length() == 3) {
                try {
                    BTreeImpl s1 = evalDash(car(cdr(input)));
                    BTreeImpl s2 = evalDash(car(cdr(cdr(input))));

                    String methodName = carOfInput.root.token.getValue().toString().toLowerCase() + "Dash";
                    Method method = this.getClass().getMethod(methodName, BTreeImpl.class, BTreeImpl.class);
                    return (BTreeImpl) method.invoke(this, s1, s2);
                } catch (TypeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new TypeException(e.getCause().toString().substring(e.getCause().toString().indexOf(":") + 1));
                }
            } else {
                throw new TypeException("Expected S-Expression to contain exactly 3 tokens");
            }

        } else if (isTokenFunc(carOfInput, "ATOM")
                || isTokenFunc(car(input), "INT")
                || isTokenFunc(car(input), "NULL")
                || isTokenFunc(carOfInput, "CAR")
                || isTokenFunc(car(input), "CDR")) {
            if (input.length() == 2) {

                try {
                    BTreeImpl s1 = evalDash(car(cdr(input)));
                    String methodName = carOfInput.root.token.getValue().toString().toLowerCase() + "Dash";
                    Method method = this.getClass().getDeclaredMethod(methodName, BTreeImpl.class);
                    return (BTreeImpl) method.invoke(this, s1);
                }catch (TypeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new TypeException(e.getCause().toString().substring(e.getCause().toString().indexOf(":") + 1));
                }
            } else {
                throw new TypeException("Expected S-Expression to contain 2 tokens");
            }
        }else if(isTokenFunc(carOfInput, "COND")){

            BTreeImpl currentCondBranch = cdr(input);
            BTreeImpl stmtCurrentCond = car(cdr(car(currentCondBranch)));
            if(equalsAbstractType(stmtCurrentCond, Types.NAT)){
                return getTreeOfAbstractType(new Token(Types.NAT, "AnyNat"));
            }else if (equalsAbstractType(stmtCurrentCond, Types.BOOL)){
                return getTreeOfAbstractType(new Token(Types.BOOL, "AnyBool"));
            }else if (equalsAbstractType(stmtCurrentCond, Types.LISTOFNATS)){
                ArrayList <Integer> list = new ArrayList <Integer>();
                while(isTokenNIL(currentCondBranch.root.token) == false){
                    BTreeImpl evalStmtCurrentCond = evalDash(car(cdr(car(currentCondBranch))));
                    list.add((int)evalStmtCurrentCond.root.token.getValue());
                    currentCondBranch = cdr(currentCondBranch);
                }
                return getTreeOfAbstractType(new Token(Types.LISTOFNATS, Collections.min(list)));
            }
//            throw new TypeException("None of the sub-conditions of COND evaluate to T");


        }
        throw new TypeException("List of length >= 2 expected at: " + prettyPrinToString(input));

    }

    String prettyPrinToString(BTreeImpl input){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        prettyPrint(input);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        return baos.toString();
    }

    public static void main (String [] args){
        Scanner.init();
        Interpreter intrprtr = new Interpreter();
        try{
            List <BTreeImpl> bTreeList = Parser.parseStart();
            for(BTreeImpl btr : bTreeList){
                BTreeImpl res = intrprtr.eval(btr);
                intrprtr.evalDash(btr);
//                Parser.prettyPrint(expToPrint);
                intrprtr.prettyPrint(btr);
//                intrprtr.prettyPrint(res);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }






}
