import java.util.*;

/**
 * Created by mac on 1/20/17.
 */
class ParserException extends Exception{
    String message;
    ParserException(String str2) {
        message = str2;
    }
    public String toString(){
        return ("ParserException : " + message ) ;
    }
}

public class Parser {

    static BTreeImpl bTree = new BTreeImpl();

    public static void dotPrint(Node node){
        if(node.leftNode == null || node.rightNode == null){
            Object printValue;
            if(node.token instanceof Token){
                Token token = (Token) node.token;
                printValue = token.getValue();
            }else{
                printValue = node.token;
            }

            System.out.print(printValue);
        }else{
            System.out.print("(");
//            if (node.leftNode != null) {
                dotPrint(node.leftNode);
//            }
            System.out.print(" . ");
//            if(node.rightNode != null){
                dotPrint(node.rightNode);
//            }
            System.out.print(")");
        }
    }

    public static void prettyPrint(BTreeImpl btree){
        dotPrint(btree.root);
        System.out.println();

    }

    public static void reset (){
        bTree = new BTreeImpl();
    }

    public static List parseStart () throws ParserException{
        List <BTreeImpl> bTreeList = new ArrayList<>();

        do{
            parseExpr();
            if(Scanner.getCurrent().getType().equals(TokenType.ERROR)){
                throw new ParserException("ERROR: Unrecognized token " + Scanner.getCurrent().getValue());
            }
//            prettyPrint(bTree);
            bTreeList.add(bTree);
            reset();

        }while(!Scanner.getCurrent().getType().equals(TokenType.EOF));
        return bTreeList;
    }
    public static void parseExpr(){
        Token currentToken = Scanner.getCurrent();

        if(currentToken.getType().equals(TokenType.ERROR)){
            return;
        }
        if(currentToken.getType().equals(TokenType.NUMERIC) || currentToken.getType().equals(TokenType.LITERAL)){
            bTree.insertNode(currentToken);
            Scanner.moveToNext();
            //add a new node with token as left child and right as nil, which will be insertion point for next insert


        }else if(Scanner.getCurrent().getType().equals(TokenType.OPEN_PARENTHESIS)) {
            Node pointerToLastOpenParenthesis = bTree.insertPtr;
            Scanner.moveToNext();
            //keep on adding tokens to a list
            bTree.branchAndMoveLevelDown();
            int count = 0;
            while (!Scanner.getCurrent().getType().equals(TokenType.CLOSING_PARENTHESIS) && !Scanner.getCurrent().getType().equals(TokenType.ERROR)) {
                parseExpr();
                count++;

            }
            pointerToLastOpenParenthesis.token.value = count;
            pointerToLastOpenParenthesis.token.abstractType = Types.LISTOFNATS;
            bTree.insertNilAndMoveLevelUp();

            //add this list of
            Scanner.moveToNext();
        }else{
            Scanner.current.type = TokenType.ERROR;
//            System.out.println("Error parsing this shit");
            return;
        }
    }

    public static void main (String [] args){
        Scanner.init();
        try{
            parseStart();
        }catch (Exception e){
            System.out.println(e);
        }


    }

}
