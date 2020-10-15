package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import models.GameBoard;
import org.junit.jupiter.api.Test;


public class MethodTest {
  
  //x -> X, y -> O
  
  //x=1,y=2,Winner=x
  
  @Test
  public void testXisOneWinnerbyColumn() {
    
    char[][] boardmap = {
      {'\u0000', 'X', '\u0000'},
      {'\u0000', 'X', 'O'},
      {'\u0000', 'X', 'O'}   
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(1, winner);
  }
  
  @Test
  public void testXisOneWinnerbyRow() {
    char[][] boardmap = {
      {'\u0000', '\u0000', '\u0000'},
      {'X', 'X', 'X'},
      {'O', 'O', '\u0000'}    
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(1, winner);
  }  
  
  @Test
  public void testXisOneWinnerbyDiagonalL() {
    char[][] boardmap = {
      {'X', 'O', '\u0000'},
      {'\u0000', 'X', '\u0000'},
      {'\u0000', 'O', 'X'}   
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(1, winner);
  }  
  
  @Test
  public void testXisOneWinnerbyDiagonalR() {
    char[][] boardmap = {
      {'\u0000', 'O', 'X'},
      {'\u0000', 'X', '\u0000'},
      {'X', 'O', '\u0000'}    
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(1, winner);
  } 

  //x=1,y=2,Winner=y
  
  @Test
  public void testYisTwoWinnerbyRow() {
    char[][] boardmap = {
      {'\u0000', 'X', '\u0000'},
      {'\u0000', 'X', 'X'},
      {'O', 'O', 'O'}    
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(2, winner);
  } 
  
  @Test
  public void testYisTwoWinnerbyColumn() {
    char[][] boardmap = {
      {'\u0000', 'O', 'O'},
      {'X', 'X', 'O'},
      {'X', 'X', 'O'}    
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(2, winner);
  } 
  
  @Test
  public void testYisTwoWinnerbyDiagonalL() {
    char[][] boardmap = {
      {'O', 'X', '\u0000'},
      {'\u0000', 'O', '\u0000'},
      {'X', 'X', 'O'}   
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(2, winner);
  }  
  
  @Test
  public void testYisTwoWinnerbyDiagonalR() {
    char[][] boardmap = {
      {'\u0000', 'X', 'O'},
      {'\u0000', 'O', 'X'},
      {'O', 'X', '\u0000'}   
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(2, winner);
  }   
  
  //No winner
  
  @Test
  public void testNowinnerL() {
    char[][] boardmap = {
      {'\u0000', 'X', 'O'},
      {'\u0000', '\u0000', 'X'},
      {'O', 'X', '\u0000'}   
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(0, winner);
  }     
  
  @Test
  public void testNowinnerR() {
    char[][] boardmap = {
      {'\u0000', '\u0000', '\u0000'},
      {'\u0000', '\u0000', '\u0000'},
      {'\u0000', '\u0000', '\u0000'}   
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    int x = 1;
    int y = 2;
    
    int winner = board.isWinner(boardmap, x, y);
    assertEquals(0, winner);
  }
  
  //Test isDraw
  
  @Test
  public void tesisDraw() {
    char[][] boardmap = {
      {'O', 'X', 'X'},
      {'X', 'O', 'O'},
      {'X', 'O', 'X'}   
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);

    boolean isDraw = board.isDraw(boardmap);
    assertEquals(true, isDraw);
  }
  
  @Test
  public void tesNotisDraw() {
    char[][] boardmap = {
      {'O', 'X', 'X'},
      {'X', '\u0000', 'O'},
      {'X', 'O', 'X'}   
    };
    GameBoard board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);

    boolean isDraw = board.isDraw(boardmap);
    assertEquals(false, isDraw);
  }
}

