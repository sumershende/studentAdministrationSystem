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
		if(isProfessor()){
			List<String[]> taughtCourses = new ArrayList<>();
			Statement stmt = null;
			PreparedStatement pstmt = null;
    		ResultSet rs = null;
			String courseId, courseName;
			int n = 0;
			int profID = Integer.parseInt(loggedInUserId);

		try{
			// Create a statement object that will send SQL statement to DB
			String sqlCourseDetails = "select c_id, c_name from COURSES as C where prof_id = ?;";
			pstmt = conn.prepareStatement(sqlProfDetails);
			pstmt.clearParameters();
			pstmt.setInt(1, profID);
			rs = pstmt.executeQuery(sqlProfDetails);
			while (rs.next()) {
		    	courseName = rs.getString("c_name");
				courseId = Integer.toString(rs.getInt("c_id"));
				taughtCourses.add(new String[]{courseName, courseId});

			}


		}catch(Throwable oops) {
            oops.printStackTrace();
        }
		}
		
		return taughtCourses;
	}
	
	public List<String[]> getTACourses(){
		// Return the courses for which the logged in user is TA.
		// Syntax = <[courseName, courseId], [], []>
		List<String[]> TACourses = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String courseId, courseName;
		int studentId = Integer.parseInt(loggedInUserId);
		try{
			String sqlCourseDetails = "select C.c_id, C.c_name from Courses as C, Grad_Students as G where G.st_id = ?\
			and G.TA_for = C.c_id;";
			pstmt = conn.prepareStatement(sqlCourseDetails);
			pstmt.clearParameters();
			pstmt.setInt(1, studentId);
			rs = pstmt.executeQuery(sqlCourseDetails);
			while(rs.next()){
				courseName = rs.getString("C.c_name");
				courseId = Integer.toString(rs.getInt("C.c_id"));
				TACourses.add(new String[]{courseName, courseId});
			}
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}
		
		return TACourses;
	}
	
	public boolean isProfessor(){
		return loggedInUserType == 1;
	}
	
	public boolean isUserLoggedIn() {
		return isUserLoggedIn;
	}

	public Course getCourseInfo(String courseId){
		//Returns course ID, course name, start date, end date, array of TAs
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String courseName, courseStartDate, courseEndDate;
		int course_id = Integer.parseInt(courseId);
		int numberOfTA = 0;
		try{
			String sqlTAdetails = "select st_name from Grad_Students where TA_for = ?;";
			String sqlCount = "select count(*) from Grad_Students where TA_for = ?;";
			pstmt = conn.prepareStatement(sqlCount);
			pstmt.clearParameters();
			pstmt.setInt(1, course_id);
			while(rs.next()){
				numberOfTA = rs.getInt("count(*)");
			}
			pstmt = conn.prepareStatement(sqlTAdetails);
			pstmt.clearParameters();
			pstmt.setInt(1, course_id);
			rs = pstmt.executeQuery(sqlTAdetails);
			int i = 0;
			String TA = new String[numberOfTA];
			while(rs.next()){
				TA[i] = rs.getString("st_name");
				i++;
			}

		}
		catch(Throwable oops){
			oops.printStackTrace();
		}
		try{
			String sqlCourseDetails = "select c_id, c_name, c_start_date, c_end_date from Courses where c_id = ?;";
			pstmt = conn.prepareStatement(sqlCourseDetails);
			pstmt.clearParameters();
			pstmt.setInt(1, course_id);
			while(rs.next()){
				courseName = rs.getString("c_name");
				courseStartDate = rs.getString("c_start_date");
				courseEndDate = rs.getString("c_end_date");
			}
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}
		return new Course(courseId, courseName, courseStartDate, courseEndDate, TA);
		//Course course = new Course("CSC-505", "ALGO", "08/15/2017", "07/12/2017", new String[]{"Udit Deshmukh", "Akanksha Shukla"});
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
		boolean isTopic = true;
		int questionId;
		String topic;
		try{
			questionId = Integer.parseInt(searchQuery);
			isTopic = false;
		}
		catch(Exception e){
			topic = searchQuery;
		}
		if(isTopic){
			
		}
		return questions;
	}
	
	public boolean addQuestionToQuestionBank(Question question){
		
		return false;
	}
}