package models;

public class GameBoard {

  /**
   * Set the GameBoard Structure.
   * 
   * @param  p1 p2 gameStarted turn boardState winner isDraw   the GameBoard structure.
   */
  public GameBoard(Player p1, Player p2, boolean gameStarted, int turn, 
      char[][] boardState, int winner, boolean isDraw) {

    super();
    this.p1 = p1;
    this.p2 = p2;
    this.gameStarted = gameStarted;
    this.turn = turn;
    this.boardState = (char[][]) boardState.clone();
    this.winner = winner;
    this.isDraw = isDraw;
  }
  
  private Player p1;
  
  public Player getp1() {
    return p1;
  }
  
  public void setp1(Player p1) {
    this.p1 = p1;
  }
  
  private Player p2;
  
  public Player getp2() {
    return p2;
  }
  
  public void setp2(Player p2) {
    this.p2 = p2;
  }
  
  private boolean gameStarted;
  
  public boolean getgameStarted() {
    return gameStarted;
  }
  
  public void setgameStarted(boolean gameStarted) {
    this.gameStarted = gameStarted;
  }
  
  private int turn;
  
  public int getturn() {
    return turn;
  }
  
  public void setturn(int turn) {
    this.turn = turn;
  }
  
  private char[][] boardState;
  
  public char[][] getboardState() {
    return (char[][]) boardState.clone();
  }
  
  public void setboardState(char[][] boardState) {
    this.boardState = (char[][]) boardState.clone();
  }
  
  private int winner;
  
  public int getWinner() {
    return winner;
  }
  
  public void setWinner(int winner) {
    this.winner = winner;
  }
  
  private boolean isDraw;
  
  public boolean getDraw() {
    return isDraw;
  }
  
  public void setDraw(boolean isDraw) {
    this.isDraw = isDraw;
  }
  
  // check all the rows and columns and diagonals to find whether there is a winner.
  /** Check the winner.
   * @param boardmap x y   x means X, y means O. 
   * @return int.
   */  
  
  public int isWinner(char[][] boardmap, int x, int y) {
    int j = 0;
    boolean flag;
    char curvalue;
 
    for (int i = 0; i < boardmap.length; i++) {
      j = 0;
      flag = true;
      curvalue = boardmap[i][j];
      while (j < boardmap[i].length) {
        if (boardmap[i][j] != curvalue) {
          flag = false;
          break;
        }
        j++;
      }
      if (flag == true) {
        if (curvalue == 'X') {
          return x;
        } else if (curvalue == 'O') {
          return y;
        }
      }
    }
    for (int i = 0; i < boardmap[0].length; i++) {
      j = 0;
      flag = true;
      curvalue = boardmap[j][i];
      while (j < boardmap.length) {
        if (boardmap[j][i] != curvalue) {
          flag = false;
          break;
        }
        j++;
      }
      if (flag == true) {
        if (curvalue == 'X') {
          return x;
        } else if (curvalue == 'O') {
          return y;
        }
      }
    }
    j = 0;
    curvalue = boardmap[0][0];
    flag = true;
    while (j < boardmap.length) {
      if (boardmap[j][j] != curvalue) {
        flag = false;
        break;
      }
      j++;
    }
    if (flag == true) {
      if (curvalue == 'X') {
        return x;
      } else if (curvalue == 'O') {
        return y;
      }
    }
    j = 0;
    curvalue = boardmap[j][boardmap[j].length - 1];
    flag = true;
    while (j < boardmap.length) {
      if (boardmap[j][boardmap[j].length - 1 - j] != curvalue) {
        flag = false;
        break;
      }
      j++;
    }
    if (flag == true) {
      if (curvalue == 'X') {
        return x;
      } else if (curvalue == 'O') {
        return y;
      }
    }
    return 0;
  }
  // check whether all the board is drawn. 
  // True means all isDraw. False means there is empty.
  
  /** Check whether it is a Draw.
   * @param boardmap   board information.
   * @return boolean.
   */ 
  
  public boolean isDraw(char[][] boardmap) {
    for (int i = 0; i < boardmap.length; i++) {
      for (int j = 0; j < boardmap[i].length; j++) {
        if (boardmap[i][j] == '\u0000') {
          return false;
        }
      }
    }
    return true;
  }
}
