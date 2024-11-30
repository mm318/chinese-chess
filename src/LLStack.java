////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       LLStack
//Description: A stack operating on pointers.
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

/**
 * A stack operating on pointers.
 */
public class LLStack {
  /** the head pointer, or beginning, of the stack */
  private Node headPtr;

  /**
   * Creates a new empty stack.
   */
  public LLStack() {
    headPtr = null;
  }


  /**
   * Pops, gets the item on the top of the stack.
   *
   * @return   The last move added onto the stack.
   */
  public CCMove pop() {
    if (headPtr != null) {
      CCMove output = headPtr.getData();
      headPtr = headPtr.getNext();
      return output;
    } else {
      return null;
    }
  }


  /**
   * Pushes, or puts, the item onto the top of the stack.
   *
   * @param  move the move to be put onto the stack
   * @return      whether the move was successfuly pushed onto the stack.
   */
  public boolean push(CCMove move) {
    try {
      Node newNode = new Node(move, headPtr);
      headPtr = newNode;
      return true;
    } catch (Exception e) {
      return false;
    }
  }


  /**
   * Returns whether the stack is empty or not
   *
   * @return   boolean of whether the stack is empty or not.
   */
  public boolean isEmpty() {
    if (headPtr == null) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * Returns whether the stack is full or not
   *
   * @return   boolean of whether the stack is full or not.
   */
  public boolean isFull() {
    return false;
  }
}
