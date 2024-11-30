////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       Node
//Description: A node used by the stack, which encapsulates move data.
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

/**
 * A node used by the stack, which encapsulates move data.
 */
public class Node {
  /** the move data */
  private CCMove move;
  /** the next node to be pointed to */
  private Node next;

  /**
   * Creates a new empty node.
   */
  public Node() {
    this(null, null);
  }


  /**
   * Creates a new node encapsulating a move.
   *
   * @param  move the move data to be held by this node.
   */
  public Node(CCMove move) {
    this.move = move;
  }


  /**
   * Creates a new node encapsulating a move and pointing to the next node.
   *
   * @param  move the move data to be held by this node.
   * @param  next the node that this node is to be pointing to.
   */
  public Node(CCMove move, Node next) {
    this.move = move;
    this.next = next;
  }


  /**
   * Sets the move data to be encapsulated by this node.
   *
   * @param  data the move data to be held by this node.
   */
  public void setData(CCMove data) {
    this.move = data;
  }


  /**
   * Sets the node this node should be pointing to.
   *
   * @param  next the node this node is to be point to.
   */
  public void setNext(Node next) {
    this.next = next;
  }


  /**
   * Gets the data being held by this node.
   *
   * @return   the move held by this node.
   */
  public CCMove getData() {
    return move;
  }


  /**
   * Gets the node being pointed to by this node.
   *
   * @return   the node pointed to by this node.
   */
  public Node getNext() {
    return next;
  }
}
