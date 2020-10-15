package models;

public class Move {

  private Player player;
  
  public Player getplay() {
    return player;
  }
  
  public void setplayer(Player player) {
    this.player = player;
  }
  
  private int moveX;
  
  public int getmoveX() {
    return moveX;
  }
  
  public void setmoveX(int moveX) {
    this.moveX = moveX;
  }
  
  private int moveY;
  
  public int getmoveY() {
    return moveY;
  }
  
  public void setmoveY(int moveY) {
    this.moveY = moveY;
  }
  
  /**
   * Set the Move Structure.
   * 
   * @param  player moveX moveY   the Move structure.
   */
  public Move(Player player, int moveX, int moveY) {
    super();
    this.player = player;
    this.moveX = moveX;
    this.moveY = moveY;
  }
}
