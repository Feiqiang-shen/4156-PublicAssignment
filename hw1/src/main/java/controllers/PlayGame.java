package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import java.io.IOException;
import java.sql.Connection;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;
import utils.DatabaseJdbc;


public class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;
  private static GameBoard board;
  //protected static DatabaseJdbc jdbc;
  
  /** Main method of the application.
   * @param args Command line arguments
   */
  
  public static void main(final String[] args) {
    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    boolean moveTable = jdbc.createMoveTable(con, "ASE_I3_MOVE");
    boolean boardTable = jdbc.createBoardTable(con, "ASE_I3_BOARD");
    boolean playerTable = jdbc.createPlayerTable(con, "ASE_I3_PLAYER");
    System.out.println(moveTable); 
    System.out.println(boardTable);
    System.out.println(playerTable);
    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);
    
    Gson gson = new GsonBuilder().create();
    JavalinJson.setFromJsonMapper(gson::fromJson);
    JavalinJson.setToJsonMapper(gson::toJson);
    
    // Test Echo Server
    // app.post("/echo", ctx -> {
    //  ctx.result(ctx.body());
    // });
    
    //create new game and send the HTML.
    
    board = new GameBoard(null, null, false, 1, new char[3][3], 0, false);
    
    app.get("/newgame", ctx -> {
      ctx.redirect("/tictactoe.html");
      boolean moveCleanedTable = jdbc.cleanTable(con, "ASE_I3_MOVE");
      boolean boardCleanedTable = jdbc.cleanTable(con, "ASE_I3_BOARD");
      boolean playerCleanedTable = jdbc.cleanTable(con, "ASE_I3_PLAYER");
      System.out.println("newgame" + moveCleanedTable); 
      System.out.println("newgame" + boardCleanedTable);
      System.out.println("newgame" + playerCleanedTable);
    });
    
    //start the game and initialize the board and player1.
    
    app.post("/startgame", ctx -> {
      char type = jdbc.selectPlayer(con, "TYPE", "WHERE PLAYER_ID = 1");
      Player p1 = new Player(type, 1);
      if (type == 'u') {
        type = ctx.body().charAt(5);
        p1.settype(type);
      }
      board.setp1(p1);
      board.setp2(null);
      board.setgameStarted(false);
      board.setturn(1);
      board.setboardState(new char[3][3]);
      board.setWinner(0);
      board.setDraw(false);
      ctx.result(gson.toJson(board));
      boolean playerAddedTable = jdbc.addPlayerData(con, p1);
      boolean boardAddedTable = jdbc.addBoardData(con, board);
      System.out.println("startgame" + playerAddedTable);
      System.out.println("startgame" + boardAddedTable);
    });
    
    //wait for player2 to join the game, initialize the player2 and send HTML to player2.
    
    app.get("/joingame", ctx -> {
      try {
        char type1 = jdbc.selectPlayer(con, "TYPE", "WHERE PLAYER_ID = 1");
        char type2 = jdbc.selectPlayer(con, "TYPE", "WHERE PLAYER_ID = 2");
        Player p1 = new Player(type1, 1);
        Player p2 = new Player(type2, 2);
        if (type2 == 'u') {
          if (type1 == 'X') {
            p2.settype('O');
            p2.setid(2);
          } else {
            p2.settype('X');
            p2.setid(2);
          }
        }
        board.setp1(p1);
        board.setp2(p2);
        board.setgameStarted(true);
        ctx.redirect("/tictactoe.html?p=2");
        sendGameBoardToAllPlayers(gson.toJson(board));
        boolean playerAddedTable = jdbc.addPlayerData(con, p2);
        boolean boardAddedTable = jdbc.addBoardData(con, board);
        System.out.println("joingame" + playerAddedTable);
        System.out.println("joingame" + boardAddedTable);
      } catch (NullPointerException e) {
        ctx.result("No game existed");
      }
    });
    
    //move one by one and update the board 
    
    app.post("/move/:playerId", ctx -> {
      char type1;
      char type2;
      Player curPlayer;
      
      if (jdbc.charString(board.getboardState()).equals("UUUUUUUUU")) {
        type1 = jdbc.selectPlayer(con, "TYPE", "WHERE PLAYER_ID = 1");
        type2 = jdbc.selectPlayer(con, "TYPE", "WHERE PLAYER_ID = 2");
        Player p1 = new Player(type1, 1);
        Player p2 = new Player(type2, 2);
        board.setp1(p1);
        board.setp2(p2);
        boolean curGameStarted = jdbc.selectBoardBoolean(con, "GETSTARTED", "");
        board.setgameStarted(curGameStarted);
        int curTurn = jdbc.selectBoardInt(con, "TURN", "");
        board.setturn(curTurn);
        int curWin = jdbc.selectBoardInt(con, "WINNER", "");
        board.setWinner(curWin);
        boolean curIsDraw = jdbc.selectBoardBoolean(con, "ISDRAW", "");
        board.setDraw(curIsDraw);
        char[][] curBoardState;
        if (jdbc.selectBoardState(con, "BOARDSTATE", "") == "UUUUUUUUU") {
          curBoardState = jdbc.stringChar("UUUUUUUUU");
        } else {
          curBoardState = jdbc.stringChar(jdbc.selectBoardState(con, "BOARDSTATE", ""));
        }
        board.setboardState(curBoardState);
      }

      int xplayer;
      int yplayer;
      char charX = ctx.body().charAt(2);
      char charY = ctx.body().charAt(6);
      Move move = new Move(null, 0, 0);
      move.setmoveX(Integer.parseInt(String.valueOf(charX)));
      move.setmoveY(Integer.parseInt(String.valueOf(charY)));
      
      Message message = new Message(true, 100, "");
      
      if (board.getp1().gettype() == 'X') {
        xplayer = 1;
        yplayer = 2;

      } else {
        xplayer = 2;
        yplayer = 1;
      }
      String playerId = ctx.pathParam("playerId");
      if (Integer.parseInt(playerId) == 1) {
        curPlayer = board.getp1();
      } else {
        curPlayer = board.getp2();
      }
      move.setplayer(curPlayer);
      
      try { 
        
        if (!board.getgameStarted()) {
          throw new IOException("Need two players");
        } else if (board.getDraw() || board.getWinner() != 0) {
          throw new IOException("Game over");
        }
      
        char[][] boardState = board.getboardState();        
        
        if (board.getturn() == 1 && playerId.equals("2")) {
          throw new IOException("Wait for Player1");
        } else if (board.getturn() == 2 && playerId.equals("1")) {
          throw new IOException("Wait for Player2");
        } else if (boardState[move.getmoveX()][move.getmoveY()] != '\u0000') {
          throw new IOException("Illegal move");
        }
        
        boardState[move.getmoveX()][move.getmoveY()] = move.getplay().gettype();
        board.setboardState(boardState);
        int winner = board.isWinner(boardState, xplayer, yplayer);
        boolean status = board.isDraw(boardState);
        
        
        if (winner == 0 && status == true) {
          board.setDraw(status);
          message.setmessage("IsDraw");
        } else if (winner > 0) {
          board.setWinner(winner);
          message.setmessage("The winner is Player" + String.valueOf(winner));
        } 

        if (board.getturn() == 1) {
          board.setturn(2);
        } else {
          board.setturn(1);
        }
        
        ctx.result(gson.toJson(message));
        boolean moveAddedTable = jdbc.addMoveData(con, move);
        boolean boardAddedTable = jdbc.addBoardData(con, board);
        System.out.println("move" + moveAddedTable);
        System.out.println("move" + boardAddedTable);
      } catch (IOException e) {
        message.setmoveValidity(false);
        message.setcode(0);
        message.setmessage(e.getMessage());
        ctx.result(gson.toJson(message));
      }
      sendGameBoardToAllPlayers(gson.toJson(board));
    });
    
    

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
  }

  /** Send message to all players.
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
      } catch (IOException e) {
        // Add logger here
      }
    }
  }

  public static void stop() {
    app.stop();
  }
}
