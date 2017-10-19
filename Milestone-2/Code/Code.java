public class IdentifierNotSetException extends Exception{
  public IdentifierNotSetException(String message){
    super(message);
  }
}

abstract class DBHandler{
  
  protected static final String jdbcUrl = "";
  protected Connection conn;
  protected String userName, password;


  protected boolean createConnection() throws IdentifierNotSetException, SQLException{
    if (jdbcUrl == null){
      throw new SQLException("JDBC URL can not be null!", "08001");
    }
    if (userName == null){
      throw new IdentifierNotSetException("User Name not set!");
    }
    if (password == null){
      throw new IdentifierNotSetException("Password not set!");
    }
    if (conn == null){
      try{
        conn = DriverManager.getConnection(jdbcUrl, userName, password);
      }catch(SQLException sqlExcpt){
        throw sqlExcpt;
      }
    }
    return conn;
  }

  protected void closeConnection(){
    if(conn != null) {
      try { 
        conn.close(); 
      } catch(Throwable whatever) {}
    }
  }

  protected void closeStatement(Statement st){
    if(st != null) {
      try { 
        st.close(); 
      } catch(Throwable whatever) {}
    }
  }

  protected void closeResultSet(ResultSet rs){
    if(rs != null) {
      try { 
        rs.close(); 
      } catch(Throwable whatever) {}
    }
  }

  protected void setUserName(String userName){
    this.userName = userName;
  }

  protected void setPassword(String password){
    this.password = password;
  }

  protected void setJDBCURL(String jdbcUrl){
    this.jdbcUrl = jdbcUrl;
  }
}

public class Code{
  public static void main(String[] args) {

  }
}
