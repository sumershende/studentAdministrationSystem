import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class DBHandler{

	protected static final String jdbcUrl = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	private Connection conn;
	private final String dbUserName, dbPassword;
	
	private String loggedInUserFirstName, loggedInUserLastName, loggedInUserId;
	private short loggedInUserType;

	private static DBHandler dbHandler;
	
	private DBHandler(){
		// Singleton
		dbUserName = "gverma";
		dbPassword = "200158973";
	}
	
	public static DBHandler getDBHandler(){
		if(dbHandler == null){
			dbHandler = new DBHandler();
		}
		return dbHandler;
	}

	public boolean createConnection() throws SQLException{
		if (jdbcUrl == null){
			throw new SQLException("JDBC URL can not be null!", "08001");
		}
		if (conn == null){
			try{
				conn = (Connection) DriverManager.getConnection(jdbcUrl, dbUserName, dbPassword);
			}catch(SQLException sqlExcpt){
				throw sqlExcpt;
			}
		}
		return true;
	}
	
	public short login(String userName, String password){
		/*
		 * 0: Invalid
		 * 1: Professor
		 * 2: TA
		 * 3: Student
		 * */
		
		loggedInUserFirstName = "Gautam";
		loggedInUserLastName  = "Verma";
		loggedInUserId = "200158973";
		loggedInUserType = (short)1;
		
		return loggedInUserType;
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
	
	public String getLoggedInUserFirstName(){
		return loggedInUserFirstName;
	}
	
	public String getLoggedInUserLastName(){
		return loggedInUserLastName;
	}
	
	public String getLoggedInUserId(){
		return loggedInUserId;
	}
	
	public short getLoggedInUserType(){
		return loggedInUserType;
	}
	
	public String[][] getTaughtCoursesByProfessor(){
		// Return the taught courses by the logged in professor.
		
		return new String[][]{new String[]{"ALGO", "CSC-505"}, new String[]{"SE", "CSC-510"}};
	}
	
	public String[][] getTACourses(){
		// Return the courses for which the logged in user is TA.
		
		return new String[][]{new String[]{"ALGO", "CSC-505"}, new String[]{"SE", "CSC-510"}};
	}
	
	public boolean isProfessor(){
		return loggedInUserType == 1;
	}
	
	public Course getCourseInfo(String courseId){
		Course course = new Course("CSC-505", "ALGO", "08/15/2017", "07/12/2017", new String[]{"Udit Deshmukh", "Akanksha Shukla"});
		return course;
	}
}