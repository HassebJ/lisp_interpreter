import java.text.ParseException;

/**
 * Created by mac on 1/20/17.
 */

public class Parser {

    static BTreeImpl bTree = new BTreeImpl();

    public static void dotPrint(Node node){
        if(node.leftNode == null || node.rightNode == null){
            System.out.print(node.value);
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

    public static void prettyPrint(){
        dotPrint(bTree.root);

    }

    public static void parseStart(){
        do{
            parseExpr();
            if(Scanner.getCurrent().getType().equals(TokenType.ERROR)){
                System.out.println("ERROR: Unrecognized token " + Scanner.getCurrent().getValue());
                return;
            }
            prettyPrint();
            System.out.println();
            bTree = new BTreeImpl();

        }while(!Scanner.getCurrent().getType().equals(TokenType.EOF));
    }
    public static void parseExpr(){
        Token currentToken = Scanner.getCurrent();
        if(currentToken.getType().equals(TokenType.ERROR)){
            return;
        }
        if(currentToken.getType().equals(TokenType.NUMERIC) || currentToken.getType().equals(TokenType.LITERAL)){
            bTree.insertNode(currentToken.getValue());
            Scanner.moveToNext();
            //add a new node with token as left child and right as nil, which will be insertion point for next insert


        }else if(Scanner.getCurrent().getType().equals(TokenType.OPEN_PARENTHESIS)) {
            Scanner.moveToNext();
            //keep on adding tokens to a list
            bTree.branchAndMoveLevelDown();
            while (!Scanner.getCurrent().getType().equals(TokenType.CLOSING_PARENTHESIS) && !Scanner.getCurrent().getType().equals(TokenType.ERROR)) {
                parseExpr();

            }
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
        parseStart();

    }

}
