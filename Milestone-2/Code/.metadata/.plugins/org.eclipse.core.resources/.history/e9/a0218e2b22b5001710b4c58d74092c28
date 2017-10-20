package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class IdentifierNotSetException extends Exception{
	private static final long serialVersionUID = 1L;

	public IdentifierNotSetException(String message){
		super(message);
	}
}

class DBHandler{

	protected static final String jdbcUrl = "";
	private Connection conn;
	protected String userName, password;

	private static DBHandler dbHandler;
	
	private DBHandler(){
		
	}
	
	public static DBHandler getDBHandler(){
		if(dbHandler == null){
			dbHandler = new DBHandler();
		}
		return dbHandler;
	}

	public boolean createConnection() throws SQLException, IdentifierNotSetException{
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
				conn = (Connection) DriverManager.getConnection(jdbcUrl, userName, password);
			}catch(SQLException sqlExcpt){
				throw sqlExcpt;
			}
		}
		return true;
	}
	
	public boolean login(){
		
		return true;
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

	public void profViewProfile(int profId){
        Statement stmt = null;
		PreparedStatement pstmt = null;
        ResultSet rs = null;
		String profName;
		String[] courses = new String[10];
		int n = 0;

		try{
			// Create a statement object that will send SQL statement to DB
			String sqlProfDetails = "select P.prof_name, C.c_name from PROFESSOR P, COURSES C where P.prof_id = ? and \
			P.prof_id = C.prof_id ?"
			pstmt = conn.prepareStatement(sqlProfDetails);
			pstmt.clearParameters();
			pstmt.setInt(1, profID);
			rs = pstmt.executeQuery(sqlProfDetails);
			while (rs.next()) {
		    	profName = rs.getString("P.prof_name");
				courses[n] = rs.getString("C.c_name");

			}
			//return array of profName, profId, courses


		}catch(Throwable oops) {
            oops.printStackTrace();
        }
	

}
