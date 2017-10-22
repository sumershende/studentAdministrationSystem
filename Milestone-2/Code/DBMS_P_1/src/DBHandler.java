import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class DBHandler{

	protected static final String jdbcUrl = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	private Connection conn;
	private final String dbUserName, dbPassword;
	
	private String loggedInUserFirstName, loggedInUserLastName, loggedInUserId;
	private short loggedInUserType;

	private boolean isUserLoggedIn;
	
	private static DBHandler dbHandler;
	
	private DBHandler(){
		// Singleton
		dbUserName = "gverma";
		dbPassword = "200158973";
		isUserLoggedIn = false;
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
		
		isUserLoggedIn = true;
		
		return loggedInUserType;
	}
	
	public boolean logout(){
		isUserLoggedIn = false;
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
	
	public List<String[]> getTaughtCoursesByProfessor(){
		// Return the taught courses by the logged in professor.
		// Syntax = <[courseName, courseId], [], []>
		List<String[]> taughtCourses = new ArrayList<>();
		taughtCourses.add(new String[]{"ALGO", "CSC-505"});
		taughtCourses.add(new String[]{"SE", "CSC-510"});
		
		return taughtCourses;
	}
	
	public List<String[]> getTACourses(){
		// Return the courses for which the logged in user is TA.
		// Syntax = <[courseName, courseId], [], []>
		List<String[]> TACourses = new ArrayList<>();
		TACourses.add(new String[]{"ALGO", "CSC-505"});
		TACourses.add(new String[]{"SE", "CSC-510"});
		
		return TACourses;
	}
	
	public boolean isProfessor(){
		return loggedInUserType == 1;
	}
	
	public boolean isUserLoggedIn() {
		return isUserLoggedIn;
	}

	public Course getCourseInfo(String courseId){
		Course course = new Course("CSC-505", "ALGO", "08/15/2017", "07/12/2017", new String[]{"Udit Deshmukh", "Akanksha Shukla"});
		
		return course;
	}
	
	public boolean addNewCourse(Course course){
		
		return false;
	}
	
	public boolean addNewStudentToCourse(String studentId, String courseId){
		
		return false;
	}
	
	public boolean dropStudentFromCourse(String studentId, String courseId){
		
		return true;
	}
	
	public StudentReport getStudentReport(String studentId){
		return new StudentReport("Gautam", "Verma", new String[][]{new String[]{"HW1", "100"}, new String[]{"HW2", "97"}, new String[]{"HW3", "93"}});
	}
	
	public String[][] getExercisesForCourse(String courseId){
		return new String[][]{new String[]{"HW1", "1234"}, new String[]{"HW2", "1235"}};
	}
	
	public boolean assignTAToCourse(String TAId, String courseId){
		
		return true;
	}
	
	public List<Question> getQuestionsForCourse(String courseId, String courseName){
		List<Question> questions = new ArrayList<>();
		
		return questions;
	}
	
	public List<Question> getQuestionsWithSearchQuery(String searchQuery){
		List<Question> questions = new ArrayList<>();
		
		return questions;
	}
	
	public boolean addQuestionToQuestionBank(Question question){
		
		return false;
	}
}