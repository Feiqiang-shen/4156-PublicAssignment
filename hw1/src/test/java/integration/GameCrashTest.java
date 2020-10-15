package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import controllers.PlayGame;
import java.sql.Connection;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Message;
import models.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import utils.DatabaseJdbc;


@TestMethodOrder(OrderAnnotation.class) 
public class GameCrashTest {

  /**
   * Runs only once before the testing starts.
   */
  
  @BeforeAll
  public static void init() {
    // Start Server
    PlayGame.main(null);
    System.out.println("Before All");
  }
  /**
   * This method starts a new game before every test run. It will run every time before a test.
   */
  
  @BeforeEach
  public void startNewGame() {
    // Test if server is running. You need to have an endpoint /
    // If you do not wish to have this end point, it is okay to not have anything in this method.
    HttpResponse<String> response = Unirest.get("http://localhost:8080/").asString();
    int restStatus = response.getStatus();
    System.out.println("Before Each: " + String.valueOf(restStatus));
  }

  /**
   * This is a test case to evaluate the newgame endpoint.
   */
  
  @Test
  @Order(1)
  public void newGameTest() {

    // Create HTTP request and get response
    HttpResponse<String> response = Unirest.get("http://localhost:8080/newgame").asString();
    int restStatus = response.getStatus();

    // Check assert statement (New Game has started)
    assertEquals(restStatus, 200);
    System.out.println("Test New Game");
  }
   
  /**
   * This is a test case to evaluate the startgame endpoint.
   */
  
  @Test
  @Order(2)
  public void startGameTest() {

    // Create a POST request to startgame endpoint and get the body
    // Remember to use asString() only once for an endpoint call. 
    // Every time you call asString(), a new request will be sent to the endpoint.
    // Call it once and then use the data in the object.
    HttpResponse<String> response = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    String responseBody = response.getBody();

    // --------------------------- JSONObject Parsing ----------------------------------
    System.out.println("Start Game Response: " + responseBody);
    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);
    // Check if game started after player 1 joins: Game should not start at this point
    assertEquals(false, jsonObject.get("gameStarted"));
    // ---------------------------- GSON Parsing -------------------------
    // GSON use to parse data to object
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    Player player1 = gameBoard.getp1();
    // Check if player type is correct
    assertEquals('X', player1.gettype());
    System.out.println("Test Start Game");
  }
  
  /**
   * This is a test case to evaluate clean database in newgame.
   */
  
  @Test
  @Order(3)
  public void newgameCleanTest() {
    
    Unirest.get("http://localhost:8080/newgame").asString();
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection c = jdbc.createConnection();
    assertEquals("null", jdbc.selectBoardState(c, "BOARDSTATE", ""));
    assertEquals(0, jdbc.selectBoardInt(c, "TURN", ""));
    System.out.println("Order 3:  New Game Cleans");
  }
  
  /**
   * This is a test case to evaluate moveRestore in move.
   */
  
  @Test
  @Order(4)
  public void crashMoveRestoreTest() {

    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection c = jdbc.createConnection();
    String stringMap = jdbc.selectBoardState(c, "BOARDSTATE", "");
    assertEquals("UUUUXUUUU", stringMap);
    boolean curGameStarted = jdbc.selectBoardBoolean(c, "GETSTARTED", "");
    int curTurn = jdbc.selectBoardInt(c, "TURN", "");
    int curWin = jdbc.selectBoardInt(c, "WINNER", "");
    boolean curIsDraw = jdbc.selectBoardBoolean(c, "ISDRAW", "");
    char type1 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1");
    char type2 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2");
    
    PlayGame.stop();
    PlayGame.main(new String[0]);
    
    try {
      assertEquals(jdbc.selectBoardState(c, "BOARDSTATE", ""), stringMap);
      assertEquals(jdbc.selectBoardBoolean(c, "GETSTARTED", ""), curGameStarted);
      assertEquals(curTurn, jdbc.selectBoardInt(c, "TURN", ""));
      assertEquals(curWin, jdbc.selectBoardInt(c, "WINNER", ""));
      assertEquals(curIsDraw, jdbc.selectBoardBoolean(c, "ISDRAW", ""));
      assertEquals(type1, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1"));
      assertEquals(type2, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2"));
    } catch (Exception e) {
      Assertions.fail("Getting from Database should not fail");
    }
    
    Unirest.config().reset().automaticRetries(true);
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    int resStatus = response.getStatus();
    String resBody = response.getBody();

    // Assert the server responds with 200 OK
    assertEquals(200, resStatus);
    Message message = new Gson().fromJson(resBody, Message.class);
    assertEquals(false, message.getmoveValidity());
    assertEquals("Illegal move", message.getmessage());
    System.out.println("Order 4:  Move restore");
  }
  
  /**
   * This is a test case to evaluate Draw Restore in move.
   */
  
  @Test
  @Order(5)
  public void crashDrawRestoreTest() {

    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection c = jdbc.createConnection();
    String stringMap = jdbc.selectBoardState(c, "BOARDSTATE", "");
    assertEquals("XOXOOXXXO", stringMap);
    boolean curGameStarted = jdbc.selectBoardBoolean(c, "GETSTARTED", "");
    int curTurn = jdbc.selectBoardInt(c, "TURN", "");
    int curWin = jdbc.selectBoardInt(c, "WINNER", "");
    boolean curIsDraw = jdbc.selectBoardBoolean(c, "ISDRAW", "");
    char type1 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1");
    char type2 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2");
    
    PlayGame.stop();
    PlayGame.main(new String[0]);
    
    try {
      assertEquals(jdbc.selectBoardState(c, "BOARDSTATE", ""), stringMap);
      assertEquals(jdbc.selectBoardBoolean(c, "GETSTARTED", ""), curGameStarted);
      assertEquals(curTurn, jdbc.selectBoardInt(c, "TURN", ""));
      assertEquals(curWin, jdbc.selectBoardInt(c, "WINNER", ""));
      assertEquals(curIsDraw, jdbc.selectBoardBoolean(c, "ISDRAW", ""));
      assertEquals(type1, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1"));
      assertEquals(type2, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2"));
    } catch (Exception e) {
      Assertions.fail("Getting from Database should not fail");
    }
    
    Unirest.config().reset().automaticRetries(true);
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    int resStatus = response.getStatus();
    String resBody = response.getBody();

    // Assert the server responds with 200 OK
    assertEquals(200, resStatus);
    Message message = new Gson().fromJson(resBody, Message.class);
    assertEquals(false, message.getmoveValidity());
    assertEquals("Game over", message.getmessage());
    System.out.println("Order 5:  Draw restore");
  }
  
  /**
   * This is a test case to evaluate p1Winner Restore in move.
   */
  
  @Test
  @Order(6)
  public void crashp1WinnerRestoreTest() {

    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asString();
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection c = jdbc.createConnection();
    String stringMap = jdbc.selectBoardState(c, "BOARDSTATE", "");
    assertEquals("OXXUOUUUO", stringMap);
    boolean curGameStarted = jdbc.selectBoardBoolean(c, "GETSTARTED", "");
    int curTurn = jdbc.selectBoardInt(c, "TURN", "");
    int curWin = jdbc.selectBoardInt(c, "WINNER", "");
    boolean curIsDraw = jdbc.selectBoardBoolean(c, "ISDRAW", "");
    char type1 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1");
    char type2 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2");
    
    PlayGame.stop();
    PlayGame.main(new String[0]);
    
    try {
      assertEquals(jdbc.selectBoardState(c, "BOARDSTATE", ""), stringMap);
      assertEquals(jdbc.selectBoardBoolean(c, "GETSTARTED", ""), curGameStarted);
      assertEquals(curTurn, jdbc.selectBoardInt(c, "TURN", ""));
      assertEquals(curWin, jdbc.selectBoardInt(c, "WINNER", ""));
      assertEquals(curIsDraw, jdbc.selectBoardBoolean(c, "ISDRAW", ""));
      assertEquals(type1, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1"));
      assertEquals(type2, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2"));
    } catch (Exception e) {
      Assertions.fail("Getting from Database should not fail");
    }
    
    Unirest.config().reset().automaticRetries(true);
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=1").asString();
    int resStatus = response.getStatus();
    String resBody = response.getBody();

    // Assert the server responds with 200 OK
    assertEquals(200, resStatus);
    Message message = new Gson().fromJson(resBody, Message.class);
    assertEquals(false, message.getmoveValidity());
    assertEquals("Game over", message.getmessage());
    System.out.println("Order 6:  p1Winner restore");
  }
  
  /**
   * This is a test case to evaluate p2Winner Restore in move.
   */
  
  @Test
  @Order(7)
  public void crashp2WinnerRestoreTest() {

    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection c = jdbc.createConnection();
    String stringMap = jdbc.selectBoardState(c, "BOARDSTATE", "");
    assertEquals("OUXUOXOUX", stringMap);
    boolean curGameStarted = jdbc.selectBoardBoolean(c, "GETSTARTED", "");
    int curTurn = jdbc.selectBoardInt(c, "TURN", "");
    int curWin = jdbc.selectBoardInt(c, "WINNER", "");
    boolean curIsDraw = jdbc.selectBoardBoolean(c, "ISDRAW", "");
    char type1 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1");
    char type2 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2");
    
    PlayGame.stop();
    PlayGame.main(new String[0]);
    
    try {
      assertEquals(jdbc.selectBoardState(c, "BOARDSTATE", ""), stringMap);
      assertEquals(jdbc.selectBoardBoolean(c, "GETSTARTED", ""), curGameStarted);
      assertEquals(curTurn, jdbc.selectBoardInt(c, "TURN", ""));
      assertEquals(curWin, jdbc.selectBoardInt(c, "WINNER", ""));
      assertEquals(curIsDraw, jdbc.selectBoardBoolean(c, "ISDRAW", ""));
      assertEquals(type1, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1"));
      assertEquals(type2, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2"));
    } catch (Exception e) {
      Assertions.fail("Getting from Database should not fail");
    }
    
    Unirest.config().reset().automaticRetries(true);
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    int resStatus = response.getStatus();
    String resBody = response.getBody();

    // Assert the server responds with 200
    assertEquals(200, resStatus);
    Message message = new Gson().fromJson(resBody, Message.class);
    assertEquals(false, message.getmoveValidity());
    assertEquals("Game over", message.getmessage());
    System.out.println("Order 7:  p2Winner restore");
  }
  
  /**
   * This is a test case to evaluate: p1 joined but p2 not joined Restore in joingame.
   */
  
  @Test
  @Order(8)
  public void crashp1JoinedRestoreTest() {

    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection c = jdbc.createConnection();
    String stringMap = jdbc.selectBoardState(c, "BOARDSTATE", "");
    assertEquals("UUUUUUUUU", stringMap);
    boolean curGameStarted = jdbc.selectBoardBoolean(c, "GETSTARTED", "");
    int curTurn = jdbc.selectBoardInt(c, "TURN", "");
    int curWin = jdbc.selectBoardInt(c, "WINNER", "");
    boolean curIsDraw = jdbc.selectBoardBoolean(c, "ISDRAW", "");
    char type1 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1");
    char type2 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2");
    assertEquals('u', type2);
    
    PlayGame.stop();
    PlayGame.main(new String[0]);
    
    try {
      assertEquals(jdbc.selectBoardState(c, "BOARDSTATE", ""), stringMap);
      assertEquals(jdbc.selectBoardBoolean(c, "GETSTARTED", ""), curGameStarted);
      assertEquals(curTurn, jdbc.selectBoardInt(c, "TURN", ""));
      assertEquals(curWin, jdbc.selectBoardInt(c, "WINNER", ""));
      assertEquals(curIsDraw, jdbc.selectBoardBoolean(c, "ISDRAW", ""));
      assertEquals(type1, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1"));
      assertEquals(type2, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2"));
    } catch (Exception e) {
      Assertions.fail("Getting from Database should not fail");
    }
    
    Unirest.config().reset().automaticRetries(true);
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    HttpResponse<String> response = Unirest.get("http://localhost:8080/joingame").asString();
    int resStatus = response.getStatus();

    // Assert the server responds with 200
    assertEquals(200, resStatus);

    System.out.println("Order 8:  p1Joined restore");
  }
  
  /**
   * This is a test case to evaluate: p1 and p2 joined Restore in joingame.
   */
  
  @Test
  @Order(9)
  public void crashp2JoinedRestoreTest() {

    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection c = jdbc.createConnection();
    String stringMap = jdbc.selectBoardState(c, "BOARDSTATE", "");
    assertEquals("UUUUUUUUU", stringMap);
    boolean curGameStarted = jdbc.selectBoardBoolean(c, "GETSTARTED", "");
    int curTurn = jdbc.selectBoardInt(c, "TURN", "");
    int curWin = jdbc.selectBoardInt(c, "WINNER", "");
    boolean curIsDraw = jdbc.selectBoardBoolean(c, "ISDRAW", "");
    char type1 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1");
    char type2 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2");
    
    PlayGame.stop();
    PlayGame.main(new String[0]);
    
    try {
      assertEquals(jdbc.selectBoardState(c, "BOARDSTATE", ""), stringMap);
      assertEquals(jdbc.selectBoardBoolean(c, "GETSTARTED", ""), curGameStarted);
      assertEquals(curTurn, jdbc.selectBoardInt(c, "TURN", ""));
      assertEquals(curWin, jdbc.selectBoardInt(c, "WINNER", ""));
      assertEquals(curIsDraw, jdbc.selectBoardBoolean(c, "ISDRAW", ""));
      assertEquals(type1, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1"));
      assertEquals(type2, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2"));
    } catch (Exception e) {
      Assertions.fail("Getting from Database should not fail");
    }
    Unirest.config().reset().automaticRetries(true);
    HttpResponse<String> response = Unirest.get("http://localhost:8080/joingame").asString();
    int resStatus = response.getStatus();

    // Assert the server responds with 200
    assertEquals(200, resStatus);
    System.out.println("Order 9:  p2Joined restore");
  }
  
  /**
   * This is a test case to evaluate: p2 invalid move Restore in move.
   */
  
  @Test
  @Order(9)
  public void crashp2InvalidRestoreTest() {

    Unirest.get("http://localhost:8080/newgame").asString();
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection c = jdbc.createConnection();
    String stringMap = jdbc.selectBoardState(c, "BOARDSTATE", "");
    assertEquals("UUUUXUUUU", stringMap);
    boolean curGameStarted = jdbc.selectBoardBoolean(c, "GETSTARTED", "");
    int curTurn = jdbc.selectBoardInt(c, "TURN", "");
    int curWin = jdbc.selectBoardInt(c, "WINNER", "");
    boolean curIsDraw = jdbc.selectBoardBoolean(c, "ISDRAW", "");
    char type1 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1");
    char type2 = jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2");
    assertEquals(2, curTurn);
    
    PlayGame.stop();
    PlayGame.main(new String[0]);
    
    try {
      assertEquals(jdbc.selectBoardState(c, "BOARDSTATE", ""), stringMap);
      assertEquals(jdbc.selectBoardBoolean(c, "GETSTARTED", ""), curGameStarted);
      assertEquals(curTurn, jdbc.selectBoardInt(c, "TURN", ""));
      assertEquals(curWin, jdbc.selectBoardInt(c, "WINNER", ""));
      assertEquals(curIsDraw, jdbc.selectBoardBoolean(c, "ISDRAW", ""));
      assertEquals(type1, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 1"));
      assertEquals(type2, jdbc.selectPlayer(c, "TYPE", "WHERE PLAYER_ID = 2")); 
      assertEquals(1, jdbc.selectMove(c, "PLAYER_ID", ""));
    } catch (Exception e) {
      Assertions.fail("Getting from Database should not fail");
    }
    Unirest.config().reset().automaticRetries(true);
    
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    int resStatus = response.getStatus();
    assertEquals(200, resStatus);
    String resBody = response.getBody();
    Message message = new Gson().fromJson(resBody, Message.class);

    // Ensure that player 2 cannot make a move since the game ended in P1 winning
    assertEquals(true, message.getmoveValidity());
    assertEquals(100, message.getcode());
    assertEquals("", message.getmessage());
    // Assert the server responds with 200
    
    System.out.println("Order 9:  p2Invalid move restore");
  }
  
  /**
   * This will run every time after a test has finished.
   */

  @AfterEach
  public void finishGame() {
    System.out.println("After Each");
  }
  
  /**
   * This method runs only once after all the test cases have been executed.
   */
  
  @AfterAll
  public static void close() {
    // Stop Server
    PlayGame.stop();
    System.out.println("After All");
  }
}