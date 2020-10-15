package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import controllers.PlayGame;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;



@TestMethodOrder(OrderAnnotation.class) 
public class GameWinTest {

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
    HttpResponse<String> response = Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
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
    assertEquals('O', player1.gettype());
    System.out.println("Test Start Game");
  }
  
  /**
   * This is a test case to evaluate the joingame endpoint.
   */
  
  @Test
  @Order(3)
  public void joinGameTest() {
    HttpResponse<String> response = Unirest.get("http://localhost:8080/joingame").asString();
    int restStatus = response.getStatus();

    // Check assert statement (New Game has started)
    assertEquals(restStatus, 200);
    System.out.println("Join Game");
  }
  
  /**
   * This is a test case to evaluate the move endpoint: isDraw.
   */  
  
  @Test
  @Order(4)
  public void moveWinTest() {
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asString();
    String responseBody = response.getBody();
    System.out.println("move Response: " + responseBody);
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(true, jsonObject.get("moveValidity"));
    assertEquals(100, jsonObject.get("code"));
    assertEquals("The winner is Player1", jsonObject.get("message"));
  }
  
  @Test
  @Order(5)
  public void moveGameoverTest() {
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = response.getBody();
    System.out.println("move Response: " + responseBody);
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals(0, jsonObject.get("code"));
    assertEquals("Game over", jsonObject.get("message"));
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

