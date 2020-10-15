package models;

public class Message {

  private boolean moveValidity;
 
  public boolean getmoveValidity() {
    return moveValidity;
  }
  
  public void setmoveValidity(boolean moveValidity) {
    this.moveValidity = moveValidity;
  }
  
  private int code;
  
  public int getcode() {
    return code;
  }
  
  public void setcode(int code) {
    this.code = code;
  }
  
  private String message;
  
  public String getmessage() {
    return message;
  }
  
  public void setmessage(String message) {
    this.message = message;
  }
  
  /**
   * Extracts the message and set the message.
   * 
   * @param  moveValidity code message   the message structure.
   */
  public Message(boolean moveValidity, int code, String message) {
    super();
    this.moveValidity = moveValidity;
    this.code = code;
    this.message = message;
  }
}
