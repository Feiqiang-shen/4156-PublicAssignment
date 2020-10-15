package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.SQLException;
import java.sql.Statement;
import models.GameBoard;
import models.Move;
import models.Player;

public class DatabaseJdbc {
  
  ///**
  // * Create new Database.
  // */

  //public static void main(String[] args) {
  //  DatabaseJdbc jdbc = new DatabaseJdbc();
    
  //  Connection con = jdbc.createConnection();
  //  boolean tableCreated = jdbc.createMoveTable(con, "ASE_I3_MOVE");
    
  //  Move move = new Move(new Player('X', 2), 0, 0);
  //  boolean tupleAdded = jdbc.addMoveData(con, move);
    
  //  try {
  //    con.close();
  //  } catch (SQLException e) {
  //    e.printStackTrace();
  //  }
  //}
  
  /**
   * Create new connection.
   * @return Connection object
   */
  public Connection createConnection() {
    Connection c = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      //System.exit(0);
    }
    System.out.println("Opened database successfully");
    return c;
  }
  
  /**
   * Create new table for Move.
   * @param c Connection object
   * @param tableName table name
   * @return Boolean true if table created successfully, and false if an error occurred
   */
  
  public boolean createMoveTable(Connection c, String tableName) {
    PreparedStatement stmt = null;
    
    try {
      String sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName)
          .append(" (PLAYER_ID INT NOT NULL,MOVE_X INT NOT NULL,MOVE_Y INT NOT NULL)").toString();

      //String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " " 
      //               + "(PLAYER_ID INT NOT NULL," 
      //               + " MOVE_X INT NOT NULL," 
      //               + " MOVE_Y INT NOT NULL)";
      stmt = c.prepareStatement(sql);
      stmt.executeUpdate();
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("MoveTable created successfully");
    return true;
  }

  /**
   * Adds move data to the database table.
   * @param c Connection object
   * @param move Move object containing data
   * @return Boolean true if data added successfully, and false if an error occurred
   */
  public boolean addMoveData(Connection c, Move move) {
    Statement stmt = null;
    
    try {

      System.out.println("Opened database of Move successfully");
      
      stmt = c.createStatement();
      String sql = "INSERT INTO ASE_I3_MOVE (PLAYER_ID,MOVE_X,MOVE_Y) " 
                     + "VALUES (" + move.getplay().getid() + ", " 
                     + move.getmoveX() + ", " + move.getmoveY() + " );";
      stmt.executeUpdate(sql);
      
      stmt.close();

    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Record of Move created successfully");
    return true;
  }
  
  /**
   * Create new table for Board.
   * @param c Connection object
   * @param tableName table name
   * @return Boolean true if table created successfully, and false if an error occurred
   */
  public boolean createBoardTable(Connection c, String tableName) {
    Statement stmt = null;
    
    try {
      stmt = c.createStatement();
      String sql = String.format("CREATE TABLE IF NOT EXISTS %s" 
              + " (GETSTARTED BOOLEAN NOT NULL," 
              + " TURN INT NOT NULL," 
              + " BOARDSTATE STRING NOT NULL," 
              + " WINNER INT NOT NULL,"
              + " ISDRAW BOOLEAN NOT NULL)", tableName);
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("BoardTable created successfully");
    return true;
  }
  
  /**
   * Create new table for Player.
   * @param c Connection object
   * @param tableName table name
   * @return Boolean true if table created successfully, and false if an error occurred
   */
  public boolean createPlayerTable(Connection c, String tableName) {
    Statement stmt = null;
    
    try {
      stmt = c.createStatement();
      String sql = String.format("CREATE TABLE IF NOT EXISTS %s" 
              + " (PLAYER_ID INT NOT NULL," 
              + " TYPE CHAR NOT NULL)", tableName);
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("PlayerTable created successfully");
    return true;
  }

  /**
   * Transfer Char[][] to String.
   * @param boardState board map
   * @return String String version of board map
   */
  
  public String charString(char[][] boardState) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < boardState.length; i++) {
      for (int j = 0; j < boardState[i].length; j++) {
        if (boardState[i][j] == '\u0000') {
          buf.append("U");
        } else {
          buf.append(String.valueOf(boardState[i][j]));
        }
      }
    }
    String out = buf.toString();
    return out;
  }
  
  /**
   * Transfer String to Char[][].
   * @param out String version of board map
   * @return char[][] board map
   */
  
  public char[][] stringChar(String out) {
    char[][] boardmap = new char[3][3];
    int index = 0;
  
    for (int i = 0; i < boardmap.length; i++) {
      for (int j = 0; j < boardmap[i].length; j++) {
        if (out.charAt(index) == 'U') {
          boardmap[i][j] = '\u0000';
        } else {
          boardmap[i][j] = out.charAt(index);
        }
        index += 1;
      }
    }
    return boardmap;
  }
  
  /**
   * Adds board data to the database table.
   * @param c Connection object
   * @param board GameBoard object containing data
   * @return Boolean true if data added successfully, and false if an error occurred
   */
  
  public boolean addBoardData(Connection c, GameBoard board) {
    PreparedStatement stmt = null;
    
    try {

      System.out.println("Opened database of Board successfully");
      
      
      String stringMap = "";
      stringMap = charString(board.getboardState());
      //String sql = new StringBuilder("INSERT INTO ASE_I3_BOARD "
      //     + "(GETSTARTED,TURN,BOARDSTATE,WINNER,ISDRAW) VALUES (")
      //        .append(board.getgameStarted()).append(", ")
      //        .append(board.getturn()).append(", '")
      //        .append(stringMap).append("', ")
      //        .append(board.getWinner()).append(", ")
      //        .append(board.getDraw()).append(" );").toString();
      String sql = String.format("INSERT INTO ASE_I3_BOARD "
            + "(GETSTARTED,TURN,BOARDSTATE,WINNER,ISDRAW) "
            + "VALUES (%b, %d, '%s', %d, %b);",
              board.getgameStarted(), board.getturn(),
              stringMap, board.getWinner(), board.getDraw());
      
      //String sql = "INSERT INTO ASE_I3_BOARD (GETSTARTED,TURN,BOARDSTATE,WINNER,ISDRAW) " 
      //               + "VALUES (" + board.getgameStarted() + ", " + board.getturn() + ", '"
      //               + stringMap + "', " + board.getWinner() + ", "
      //               + board.getDraw() + " );";
      
      stmt = c.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Record of Board created successfully");
    return true;
  }
  
  /**
   * Adds player data to the database table.
   * @param c Connection object
   * @param player Player object containing data
   * @return Boolean true if data added successfully, and false if an error occurred
   */
  public boolean addPlayerData(Connection c, Player player) {
    Statement stmt = null;
    
    try {

      System.out.println("Opened database of Player successfully");
      
      stmt = c.createStatement();
      String sql = "INSERT INTO ASE_I3_PLAYER (PLAYER_ID,TYPE) " 
                     + "VALUES (" + player.getid() + ", '" + player.gettype() + "' );";
      stmt.executeUpdate(sql);
      
      stmt.close();

    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Record of Player created successfully");
    return true;
  }
  
  /**
   * Select player data from player table.
   * @param c Connection object
   * @param ele the selected element
   * @param where where clause
   * @return the value
   */
  
  public char selectPlayer(Connection c, String ele, String where) {
    Statement stmt = null;
    char out = 'u';
    ResultSet rs = null;
    try {
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + ele + " FROM ASE_I3_PLAYER "
              + where + ";");
      while (rs.next()) {
        out = rs.getString(ele).charAt(0);
      }
      rs.close();
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return 'u';
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Record of Player selected successfully");
    return out;
  }
  
  /**
   * Select BoardState from board table.
   * @param c Connection object
   * @param ele the selected element
   * @param where where clause
   * @return the value
   */
  
  public String selectBoardState(Connection c, String ele, String where) {
    Statement stmt = null;
    String out = "null";
    ResultSet rs = null;
    try {
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + ele + " FROM ASE_I3_BOARD "
              + where + ";");
      while (rs.next()) {
        out = rs.getString("BOARDSTATE");
      }
      rs.close();
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return null;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Record of BoardState selected successfully");
    return out;
  }
  
  /**
   * Select BoardInt from board table.
   * @param c Connection object
   * @param ele the selected element
   * @param where where clause
   * @return the value
   */
  
  public int selectBoardInt(Connection c, String ele, String where) {
    Statement stmt = null;
    int out = 0;
    ResultSet rs = null;
    try {
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + ele + " FROM ASE_I3_BOARD "
              + where + ";");
      while (rs.next()) {
        out = rs.getInt(ele);
      }
      rs.close();
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return 0;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Record of BoardInt selected successfully");
    return out;
  }
  
  /**
   * Select BoardBoolean from board table.
   * @param c Connection object
   * @param ele the selected element
   * @param where where clause
   * @return the value
   */
  
  public boolean selectBoardBoolean(Connection c, String ele, String where) {
    Statement stmt = null;
    boolean out = false;
    ResultSet rs = null;
    try {
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + ele + " FROM ASE_I3_BOARD "
              + where + ";");
      while (rs.next()) {
        out = rs.getBoolean(ele);
      }
      rs.close();
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Record of BoardBoolean selected successfully");
    return out;
  }
  
  /**
   * Select Move data from Move table.
   * @param c Connection object
   * @param ele the selected element
   * @param where where clause
   * @return the value
   */
  
  public int selectMove(Connection c, String ele, String where) {
    Statement stmt = null;
    ResultSet rs = null;
    int out = 0;
    try {
      stmt = c.createStatement();
      rs = stmt.executeQuery("SELECT " + ele + " FROM ASE_I3_MOVE "
              + where + ";");
      while (rs.next()) {
        out = rs.getInt(ele);
      }
      rs.close();
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return 0;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Record of BoardBoolean selected successfully");
    return out;
  }
  
  /**
   * Clean table.
   * @param c Connection object
   * @param tableName table name
   * @return Boolean true if table cleared successfully, and false if an error occurred
   */
  
  public boolean cleanTable(Connection c, String tableName) {
    Statement stmt = null;
    
    try {
      stmt = c.createStatement();
      String sql = "DELETE FROM " +  tableName + ";";
      stmt.executeUpdate(sql);
      
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;      
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
      }
    }
    System.out.println("Table cleaned successfully");
    return true;
  }
}