
/**
 * Created by mac on 1/20/17.
 */
@SuppressWarnings("unchecked")
class BTreeImpl {
    Node root;
    Node insertPtr;

    public BTreeImpl (){
        root = new Node();
        insertPtr = root;
        root.parent = root;
    }

    public BTreeImpl(Node r){
        root = r;
        insertPtr = root;
        root.parent = root;
    }
    @Override
    public String toString(){
        return (String)root.token.value.toString();
    }

    public static BTreeImpl getTree(String val){
        return new BTreeImpl(new Node(new Token(TokenType.LITERAL, val)));
    }

    public int length(){
        Node countPtr = root;
        int count = 0;
        while (countPtr.token.equalsVal("NIL") == false ){
            count ++;
            countPtr = countPtr.rightNode;
        }
        return count;
    }

    public boolean equals(BTreeImpl newBtree){
        if(root.token.equalsVal(newBtree.root.token.value) || root.token.equalsType(newBtree.root.token.getType())){
            return true;
        }else{
            return false;
        }
    }

    public boolean equalsTokenVal(Token token){
        if(root.token.equalsVal(token.value) ){
            return true;
        }else{
            return false;
        }
    }

    void insertNode(Token val) {
        /* TO DO */

        if(root.leftNode == null){
            insertPtr = root = new Node(val);
        }else{
            insertPtr.leftNode = new Node(val);
            insertPtr.rightNode = new Node();
            insertPtr.leftNode.parent = insertPtr.rightNode.parent = insertPtr;
            insertPtr = insertPtr.rightNode;
        }
    }

    void insertNilAndMoveLevelUp(){
        Node parentInsertPtr = insertPtr.parent;
//        //if insertPtr points to root
        if(insertPtr == root){
            root = new Node(new Token(TokenType.LITERAL,"NIL"));
            return;
        }
        if(insertPtr.leftNode != null && insertPtr.rightNode != null){
            insertPtr = parentInsertPtr.parent.rightNode;
            insertNilAndMoveLevelUp();

        }else if(parentInsertPtr.rightNode == insertPtr) {
            insertPtr = new Node(new Token(TokenType.LITERAL,"NIL"));
            insertPtr.parent = parentInsertPtr;
            parentInsertPtr.rightNode = insertPtr;

            if(parentInsertPtr == parentInsertPtr.parent.leftNode){
                insertPtr = parentInsertPtr.parent.rightNode;
            }else{
                while(insertPtr.parent.rightNode == insertPtr){
                    insertPtr = insertPtr.parent;
                }
                insertPtr = insertPtr.parent.rightNode;
            }
        }else{//parentInsertPtr.leftNode == insertPtr
            insertPtr = new Node(new Token(TokenType.LITERAL,"NIL"));
            insertPtr.parent = parentInsertPtr;
            parentInsertPtr.leftNode = insertPtr;
            insertPtr = parentInsertPtr.rightNode;
        }
    }

    void branchAndMoveLevelDown(){
        insertPtr.leftNode = new Node();
        insertPtr.rightNode = new Node();
        insertPtr.leftNode.parent = insertPtr.rightNode.parent = insertPtr;
        if(insertPtr != insertPtr.parent){
            insertPtr = insertPtr.leftNode;
        }

    }

}

@SuppressWarnings("unchecked")
class Node{
    Token token;
    Node leftNode;
    Node rightNode;
    Node parent;

    public Node(){
        token = new Token(TokenType.LITERAL,"-");
        leftNode = null;
        rightNode = null;
        parent = null;
    }

    public Node(Token val){
        token = val;
        leftNode = null;
        rightNode = null;
        parent = null;
    }
}

