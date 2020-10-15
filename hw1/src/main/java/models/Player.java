package models;

public class Player {
  // A Player contain type ('X' or 'O') and id (1 or 2)
  private char type;
  
  public char gettype() {
    return type;
  }
  
  public void settype(char type) {
    this.type = type;
  }
  
  private int id;
  
  public int getid() {
    return id;
  }
  
  public void setid(int id) {
    this.id = id;
  }
  
  /**
   * Set the Player Structure.
   * 
   * @param  type id   the Player structure.
   */
  public Player(char type, int id) {
    super();
    this.type = type;
    this.id = id;
  }
  
}
