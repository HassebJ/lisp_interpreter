import java.util.*;

/**
 * Created by mac on 1/20/17.
 */
class BTreeImpl {
    Node root;
    Node insertPtr;
    Queue<Node> queue;
    Node lastNodeOnRequiredPath;

    public BTreeImpl (){
        root = new Node();
        insertPtr = root;
        root.parent = root;
    }

    void insertNode(Object val) {
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
        if(insertPtr.leftNode != null && insertPtr.rightNode != null){
            insertPtr = parentInsertPtr.parent.rightNode;
            insertNilAndMoveLevelUp();
        }
        if(parentInsertPtr.rightNode == insertPtr) {
            insertPtr = new Node("NIL");
            insertPtr.parent = parentInsertPtr;
            parentInsertPtr.rightNode = insertPtr;
//        insertPtr.leftNode.parent = insertPtr.rightNode.parent = insertPtr;
            if(parentInsertPtr == parentInsertPtr.parent.leftNode){
                insertPtr = parentInsertPtr.parent.rightNode;
            }else{
                insertPtr = parentInsertPtr.parent;
            }

        }else{
            insertPtr = new Node("NIL");
            insertPtr.parent = parentInsertPtr;
            parentInsertPtr.leftNode = insertPtr;
//            parentInsertPtr.rightNode = new Node("NIL");
//        insertPtr.leftNode.parent = insertPtr.rightNode.parent = insertPtr;
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


class Node{
    Object value;
    Node leftNode;
    Node rightNode;
    Node parent;

    public Node(){
        value = "-";
        leftNode = null;
        rightNode = null;
        parent = null;
    }

    public Node(Object val){
        value = val;
        leftNode = null;
        rightNode = null;
        parent = null;
    }
}

