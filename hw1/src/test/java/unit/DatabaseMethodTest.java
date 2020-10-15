package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import models.GameBoard;
import models.Move;
import models.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import utils.DatabaseJdbc;

public class DatabaseMethodTest {
  
  @Test
  @Order(1)
  public void testcCreateDatabase() {
    try {
      DatabaseJdbc jdbc = new DatabaseJdbc();
      Connection con = jdbc.createConnection();
    } catch (Exception e) {
      Assertions.fail("Create Database connection fail");
    }
    System.out.println("Database constructor tested");
  }
  
  @Test
  @Order(2)
  public void testCreateMoveTable() {
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    boolean moveTable = jdbc.createMoveTable(con, "ASE_I3_MOVE");
    assertEquals(moveTable, true);
    System.out.println("Created Move table tested");
  }
  
  @Test
  @Order(3)
  public void testCreateBoardTable() {
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    boolean boardTable = jdbc.createBoardTable(con, "ASE_I3_BOARD");
    assertEquals(boardTable, true);
    System.out.println("Created Board table tested");
  }
  
  @Test
  @Order(4)
  public void testCreatePlayerTable() {
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    boolean playerTable = jdbc.createPlayerTable(con, "ASE_I3_PLAYER");
    assertEquals(playerTable, true);
    System.out.println("Created Player table tested"); 
  }
  
  @Test
  @Order(5)
  public void testAddMoveTableAndGet() {
    Player p1 = new Player('X', 1);
    Move move = new Move(p1, 1, 1);
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    boolean addedTable = jdbc.addMoveData(con, move);
    assertEquals(addedTable, true);
    int playerId = jdbc.selectMove(con, "PLAYER_ID", "");
    assertEquals(playerId, 1);
    System.out.println("Added and Get Move table tested");
  }
  
  @Test
  @Order(6)
  public void testAddBoardTableAndGet() {
    Player p1 = new Player('X', 1);
    char[][] boardState = {
        {'\u0000', '\u0000', '\u0000'},
        {'\u0000', 'O', '\u0000'},
        {'\u0000', '\u0000', 'X'}
    };
    GameBoard board = new GameBoard(p1, null, false, 1, boardState, 0, false);
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    boolean addedTable = jdbc.addBoardData(con, board);
    assertEquals(addedTable, true);
    String stringMap = jdbc.selectBoardState(con, "BOARDSTATE", "");
    String stringTMap = jdbc.charString(boardState);
    assertEquals(stringMap, stringTMap);
    char[][] curBoard = jdbc.stringChar(stringMap);
    assertEquals(curBoard[0][0], boardState[0][0]);
    assertEquals(curBoard[1][1], boardState[1][1]);
    assertEquals(curBoard[2][2], boardState[2][2]);
    boolean curGameStarted = jdbc.selectBoardBoolean(con, "GETSTARTED", "");
    assertEquals(curGameStarted, false);
    int curTurn = jdbc.selectBoardInt(con, "TURN", "");
    assertEquals(curTurn, 1);
    int curWin = jdbc.selectBoardInt(con, "WINNER", "");
    assertEquals(curWin, 0);
    boolean curIsDraw = jdbc.selectBoardBoolean(con, "ISDRAW", "");
    assertEquals(curIsDraw, false);
    System.out.println("Added and Get Board table tested");
  }
  
  @Test
  @Order(7)
  public void testAddPlayerTableAndGet() {
    Player p1 = new Player('X', 1);
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    boolean addedTable = jdbc.addPlayerData(con, p1);
    assertEquals(addedTable, true);
    char type1 = jdbc.selectPlayer(con, "TYPE", "WHERE PLAYER_ID = 1");
    assertEquals(type1, 'X');
    System.out.println("Added and Get Player table tested");
  }
  
  @Test
  @Order(8)
  public void testCleanTable() {
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    boolean boardTable = jdbc.createBoardTable(con, "ASE_I3_BOARD");
    boolean cleanTable = jdbc.cleanTable(con, "ASE_I3_BOARD");
    assertEquals(boardTable, true);
    assertEquals(cleanTable, true);
    System.out.println("Clean table tested");
  }
}
