import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

enum UserType{
	TA, Professor, Student, InvalidUser;
}

class DBHandler{

	protected static final String jdbcUrl = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	private static Connection conn;
	private final String dbUserName, dbPassword;
	final String DATE_FORMAT = "MM/dd/yyyy";
	private String loggedInUserName, loggedInUserId;

	private UserType loggedInUserType;
	private int loggedInUserNumericalId;

	private boolean isUserLoggedIn;

	private static DBHandler dbHandler;

	private final int MAX_QUESTION_DIFFICULTY = 6, MIN_QUESTION_DIFFICULTY = 1;  
	
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

	public ConsoleManager getConsoleManager(){
		return ConsoleManager.getConsoleManager();
	} 

	public Connection createConnection() throws SQLException{
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
		return conn;
	}

	// Approved by GV, UD, AS.
	public UserType login(String userId, String password){	
		// By default, login any TA as TA. He can chooses if he wants
		// to continue as student or TA and informs DBHandler.

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		loggedInUserType = UserType.InvalidUser;
		isUserLoggedIn = false;

		try{
			pstmt = conn.prepareStatement("SELECT name, userid, roleid FROM users WHERE userid=? AND password=?");
			pstmt.setString(1, userId);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();
			while(rs.next())  {
				loggedInUserName = rs.getString(1);
				loggedInUserId = rs.getString(2);
				int roleid = rs.getInt(3);
				if(roleid==1){
					loggedInUserType = UserType.Professor;
				}else if(roleid==2){
					loggedInUserType = UserType.TA;
				}else if(roleid==3){
					loggedInUserType = UserType.Student;
				}
				isUserLoggedIn = true;
				loggedInUserNumericalId = getId(loggedInUserId, loggedInUserType);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return loggedInUserType;
	}

	public void changeTAToStudent(){
		loggedInUserType = UserType.Student;
	}

	public void changeStudentToTA(){
		loggedInUserType = UserType.TA;
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

	public String getLoggedInUserName(){
		return loggedInUserName;
	}

	public String getLoggedInUserId(){
		return loggedInUserId;
	}

	public UserType getLoggedInUserType(){
		return loggedInUserType;
	}

	// Approved by AS, GV.
	public List<String[]> getTaughtCoursesByProfessor(){
		// Return the taught courses by the logged in professor.
		// Syntax = <[courseName, courseId], [], []>

		List<String[]> taughtCourses = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String courseId, courseName;
		try{
			// Create a statement object that will send SQL statement to DB
			String query = "SELECT c_id, c_name "
					+ "FROM courses C "
					+ "WHERE prof_id = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.clearParameters();
			pstmt.setInt(1, loggedInUserNumericalId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				courseName = rs.getString("c_name");
				courseId = rs.getString("c_id");
				taughtCourses.add(new String[]{courseName, courseId});
			}
		}catch(Throwable oops) {
			oops.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return taughtCourses;
	}

	// Approved by AS.
	public List<String[]> getTACourses(){
		// Return the courses for which the logged in user is TA.
		// Syntax = <[courseName, courseId], [], []>

		List<String[]> TACourses = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String courseId, courseName;
		try{
			String sqlCourseDetails = "SELECT C.c_id, C.c_name "
					+ "FROM Courses C, HASTA T "
					+ "WHERE C.c_id = T.c_id and T.st_id = ?";
			pstmt = conn.prepareStatement(sqlCourseDetails);
			pstmt.clearParameters();
			pstmt.setInt(1, loggedInUserNumericalId);
			rs = pstmt.executeQuery();
			while(rs.next()){
				courseName = rs.getString("c_name");
				courseId = rs.getString("c_id");
				TACourses.add(new String[]{courseName, courseId});
			}
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return TACourses;

	}

	// Approved by GV.
	public List<String[]> getStudentEnrolledCourses(){
		// Returns list of syntax: [[courseName, courseID], [], []...]

		List<String[]> studentCourses = new ArrayList<String[]>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{
			String query = "SELECT C.c_name, C.c_id "
					+ "FROM Enrolled_In EI, Courses C "
					+ "WHERE EI.st_id = ? AND C.c_id = EI.c_id";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, loggedInUserNumericalId);

			rs = pstmt.executeQuery();
			String name = "c_name", id = "c_id";
			String courseName, courseID;
			while(rs.next()){
				courseName = rs.getString(name);
				courseID   = rs.getString(id);
				studentCourses.add(new String[]{courseName, courseID});
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return studentCourses;
	}

	public boolean isProfessor(){
		return loggedInUserType == UserType.Professor;
	}

	public boolean isTA(){
		return loggedInUserType == UserType.TA;
	}

	public boolean isUserLoggedIn() {
		return isUserLoggedIn;
	}

	public boolean checkAccessForCourse(String courseId){
		if(loggedInUserType==UserType.Professor){
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String query = "SELECT c_id "
					+ "FROM Courses "
					+ "WHERE c_id = ? AND prof_id = ?";
			try{
				pstmt = conn.prepareStatement(query);
				pstmt.clearParameters();
				pstmt.setString(1, courseId);
				pstmt.setInt(2, getId(loggedInUserId, UserType.Professor));
				rs = pstmt.executeQuery();
				if(rs.next()){
					return true;
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				closeResultSet(rs);
				closeStatement(pstmt);
			}
		}
		if(loggedInUserType==UserType.TA){
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			String query = "SELECT c_id "
					+ "FROM HASTA "
					+ "WHERE c_id = ? AND st_id = ?";
			try{
				pstmt = conn.prepareStatement(query);
				pstmt.clearParameters();
				pstmt.setString(1, courseId);
				pstmt.setInt(2, getId(loggedInUserId, UserType.TA));
				rs = pstmt.executeQuery();
				if(rs.next()){
					return true;
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				closeResultSet(rs);
				closeStatement(pstmt);
			}
		}
		if(loggedInUserType == UserType.Student) {
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			String query = "SELECT c_id "
					+ "FROM Enrolled_In "
					+ "WHERE c_id = ? AND st_id = ?";
			try{
				pstmt = conn.prepareStatement(query);
				pstmt.clearParameters();
				pstmt.setString(1, courseId);
				pstmt.setInt(2, getId(loggedInUserId, UserType.TA));
				rs = pstmt.executeQuery();
				if(rs.next()){
					return true;
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				closeResultSet(rs);
				closeStatement(pstmt);
			}

		}
		return false;
	}

	// Approved by GV
	public String getInstructorName(String courseId){
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{
			String query = "SELECT U.name "
					+ "FROM users U, ("
					+ "SELECT P.userid "
					+ "FROM Professor P, Courses C "
					+ "WHERE C.c_id = ? AND P.prof_id = C.prof_id"
					+ ") J "
					+ "WHERE U.userid = J.userid";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);

			rs = pstmt.executeQuery();
			
			if(rs.next()){
				return rs.getString(1);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return null;
	}
	
	// Approved by GV.
	public Course getCourseInfo(String courseId){
		//Returns complete course object.

		boolean access = checkAccessForCourse(courseId);
		if(access == false){
			return null;
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;

		List<Person> TAs;
		List<Topic> topics;
		List<Person> studentsEnrolled;

		try{
			// Get TAs.
			TAs = getTAsInCourse(courseId);
			
			// Get Topics
			topics = getCourseTopics(courseId);

			// Get enrolled students
			studentsEnrolled = getStudentsEnrolledInCourse(courseId);

			// Get course Details
			String cName = "c_name";
			String instName = getInstructorName(courseId);
			String startDate = "c_start_date";
			String endDate = "c_end_date";
			String levelGrad = "levelGrad";
			String maxStudentsIdentifier = "max_students";
			query = "SELECT " + cName + ", " + startDate + ", " + endDate + ", " + levelGrad + ", " + maxStudentsIdentifier + " " 
					+ "FROM courses "
					+ "WHERE c_id = ?";

			pstmt = conn.prepareStatement(query);
			pstmt.clearParameters();
			pstmt.setString(1, courseId);

			rs = pstmt.executeQuery();
			String courseName;
			Date courseStartDate, courseEndDate;
			int isGradLevel, maxStudents;
			CourseLevel courseLevel;
			if(rs.next()){
				courseName 		= rs.getString(cName);
				courseStartDate = rs.getDate(startDate);
				courseEndDate 	= rs.getDate(endDate);
				isGradLevel		= rs.getInt(levelGrad);
				if(isGradLevel == 1) courseLevel = CourseLevel.Grad;
				else courseLevel = CourseLevel.UnderGrad;
				maxStudents 	= rs.getInt(maxStudentsIdentifier);
				return new Course(courseId, courseName, courseStartDate, courseEndDate, TAs, 
						topics, studentsEnrolled, courseLevel, maxStudents, instName);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return null;
	}

	// Approved by GV.
	public List<Person> getStudentsEnrolledInCourse(String courseId){
		List<Person> enrolledStudents = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{
			String query = "SELECT U.name, U.userid "
					+ "FROM users U, ("
					+ "SELECT * "
					+ "FROM Enrolled_In EI INNER JOIN Students S ON S.st_id = EI.st_id"
					+ ") J "
					+ "WHERE J.c_id = ? AND U.userid = J.userid";
			pstmt = conn.prepareStatement(query);
			pstmt.clearParameters();
			pstmt.setString(1, courseId);

			rs = pstmt.executeQuery();
			String name = "name", id = "userid";
			String studentName, studentID;
			while(rs.next()){
				studentName = rs.getString(name);
				studentID   = rs.getString(id);
				enrolledStudents.add(new Person(studentName, studentID));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return enrolledStudents;
	}

	// Approved by GV.
	public List<Person> getTAsInCourse(String courseId){
		List<Person> TAs = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{
			String query = "SELECT U.name, U.userid "
					+ "FROM users U, ("
					+ "SELECT * "
					+ "FROM hasTA H INNER JOIN Students S ON S.st_id = H.st_id"
					+ ") J "
					+ "WHERE U.userid = J.userid AND J.c_id = ?";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);

			rs = pstmt.executeQuery();
			String name = "name", id = "userid";
			String TAName, TAID;
			while(rs.next()){
				TAName = rs.getString(name);
				TAID   = rs.getString(id);
				TAs.add(new Person(TAName, TAID));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return TAs;
	}

	// Approved by GV.
	public List<Topic> getCourseTopics(String courseId){
		// Returns an array list of topics in the course.  

		List<Topic> topics = new ArrayList<>();		
		String query = "SELECT MT.tp_name, MT.tp_id "
				+ "FROM Master_Topics MT INNER JOIN Topics T ON MT.tp_id = T.tp_id "
				+ "WHERE T.c_id = ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);

			rs = pstmt.executeQuery();
			String name = "tp_name";
			String id = "tp_id";
			String topicName;
			int topicID;
			while(rs.next()){
				topicName = rs.getString(name);
				topicID   = rs.getInt(id);
				topics.add(new Topic(topicID, topicName));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return topics;
	}

	// Approved by GV.
	public Boolean addTopicToCourse(int topicId, String courseId){
		// Returns true if the topic was successfully  added to the course.

		PreparedStatement pstmt = null;
		try{ 			
			// Now, insert into topics
			String query = "INSERT INTO Topics "
					+ "VALUES(?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);
			pstmt.setInt(2, topicId);

			if(pstmt.executeUpdate() == 0){
				// Failure
				return null;
			}else{
				return true;
			}

		}catch(SQLException e){
			return false;
		}finally{
			closeStatement(pstmt);
		}
	}

	// Approved by GV.
	public boolean isNewCourseIdValid(String courseId){
		String query = "SELECT C.c_id "
				+ "FROM Courses C "
				+ "WHERE C.c_id = ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);

			rs = pstmt.executeQuery();
			if(rs.next()){
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return true;
	}

	// Approved by GV
	public boolean isCourseIdValid(String courseId){
		String query = "SELECT C.c_id "
				+ "FROM Courses C "
				+ "WHERE C.c_id = ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);

			rs = pstmt.executeQuery();
			if(rs.next()){
				return true;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return false;
	}

	// Approved by GV
	public boolean isTopicIdValid(int TAId){
		String query = "SELECT T.tp_id "
				+ "FROM Master_Topics T "
				+ "WHERE T.tp_id= ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, TAId);

			rs = pstmt.executeQuery();
			if(rs.next()){
				return true;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return false;
	}

	// Approved by GV
	public boolean isExerciseIdValid(int exerciseId){
		String query = "SELECT E.ex_id "
				+ "FROM Exercises E "
				+ "WHERE E.ex_id= ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, exerciseId);

			rs = pstmt.executeQuery();
			if(rs.next()){
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return true;
	}

	// Approved by GV
	public boolean isNewQuestionIdValid(int questionId){
		String query = "SELECT Q.q_id "
				+ "FROM Questions Q "
				+ "WHERE Q.q_id= ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, questionId);

			rs = pstmt.executeQuery();
			if(rs.next()){
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return true;
	}

	
	private boolean isStudentEnrolledInCourse(String studentId, String courseId){
		String query = "SELECT c_id "
				+ "FROM Enrolled_In "
				+ "WHERE C_ID = ? AND ST_ID = ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);
			pstmt.setInt(2, getId(studentId, UserType.Student));
			
			rs = pstmt.executeQuery();
			if(rs.next()){
				return true;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return false;
	}
	
	// Approved by GV
	public int isTAIdValidForCourse(String TAId, String courseId){	
		// Check if TAId is even valid.
		int TANumericalId = getId(TAId, UserType.TA);
		if(TANumericalId == -1){
			// Invalid ID given by the professor.
			return -1;
		}

		// Student should be graduate level and not enrolled in the course.
		String query = "SELECT EI.st_id "
				+ "FROM Enrolled_In EI, ("
				+ "SELECT S.st_id "
				+ "FROM Students S "
				+ "WHERE S.userid = ? AND S.isGrad = 1) J "
				+ "WHERE EI.c_id = ? AND EI.st_id = J.st_id";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, TAId);
			pstmt.setString(2, courseId);

			rs = pstmt.executeQuery();

			if(rs.next()){
				// He is enrolled in the course. Cannot be set as the TA.
				return 0;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		// If he's already the TA, return null.
		query = "SELECT H.st_id "
				+ "FROM HasTA H, ("
				+ "SELECT S.st_id "
				+ "FROM Students S "
				+ "WHERE S.userid = ?) J "
				+ "WHERE H.c_id = ? AND H.st_id = J.st_id";

		rs = null;
		pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, TAId);
			pstmt.setString(2, courseId);

			rs = pstmt.executeQuery();

			if(rs.next()){
				// Already a TA.
				return 1;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		// Valid ID.
		return 2;
	}

	// Approved AS
	
	public Boolean addNewCourse(Course course){
		// Returns true if the course was successfully added.
		String query = " INSERT INTO Courses "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, course.getCourseId());
			pstmt.setString(2, course.getCourseName());
			pstmt.setDate(3, course.getStartDate());
			pstmt.setDate(4, course.getEndDate());
			pstmt.setInt(5, getId(loggedInUserId, loggedInUserType));
			pstmt.setInt(6, course.getCourseLevel().ordinal());
			pstmt.setInt(7, course.getMaxStudentsAllowed());
			if(pstmt.executeUpdate() == 1){
				// Successfully added.
				// Add TAs if there are.
				if(course.hasTAs()){
					for(Person TA : course.getTAs()){
						System.out.println(TA.getId());
						assignTAToCourse(TA.getId(), course.getCourseId());
					}
				}
				
				// Add students if given.
				if(course.hasEnrolledStudents()){
					for(Person student : course.getEnrolledStudents()){
						System.out.println(student.getId());
						System.out.println(addNewStudentToCourse(student.getId(), course.getCourseId()));
					}
				}
				return true;
			}else{
				// Already present in the course.
				return null;
			}
		}catch(SQLException s){
			// Failure, constraint violation.
			s.printStackTrace();
			return false;
		}finally {
			closeStatement(pstmt);
		}
	}

	// Approved by GV
	public boolean isStudentIdValid(String studentId){
		return getId(studentId, UserType.Student) == -1 ? false : true;
	}

	// Approved GV

	public int addNewStudentToCourse(String studentId, String courseId){

		// Returns true if the student was successfully added to the course.
		int studentNumericalId = getId(studentId, UserType.Student);

		if(studentNumericalId == -1){
			// Invalid student id
			return 0;
		}
		
		if(isStudentEnrolledInCourse(studentId, courseId)){
			return 0;
		}
		
		String query = "INSERT INTO Enrolled_In (C_ID, ST_ID) "
				+ "VALUES (?, ?)";
		PreparedStatement pstmt = null;

		try{

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);
			pstmt.setInt(2, studentNumericalId);

			pstmt.executeUpdate();
			return 1;

		}catch(SQLException s){
			// Failure, constraint violation.
			s.printStackTrace();
			return s.getErrorCode();
		}finally {
			closeStatement(pstmt);
		}
	}

	// Approved GV

	public Boolean dropStudentFromCourse(String studentId, String courseId){
		// Returns true if the student was successfully dropped from the course.
		// or null if was already not in the course.
		int studentNumericalId = getId(studentId, UserType.Student);

		if(studentNumericalId == -1){
			// Invalid student id
			return false;
		}
		if(!isStudentEnrolledInCourse(studentId, courseId)){	
			return null;
		}
		
		PreparedStatement pstmt = null;
		try{ 			
			String query = "DELETE FROM Enrolled_in "
					+ "WHERE st_id=? and c_id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, studentNumericalId);
			pstmt.setString(2, courseId);
			pstmt.executeUpdate();
			return true;
		}catch(SQLException e){
			// Failure, constraint violation.
			e.printStackTrace();
			return false;
		}finally{
			closeStatement(pstmt);
		}
	}	

	// Akanksha
	public List<StudentReport> getStudentReports(String courseId){
		// Returns the report of all students in the course.
		// Fields required in StudentReport:
		// All.
		List<StudentReport> reportList = new ArrayList<StudentReport>();
		String queryStudentId = "SELECT DISTINCT E.st_id, U.name "
				+ "FROM Enrolled_In E, Students S, Users U "
				+ "WHERE E.st_id=S.st_id and S.userid=U.userid and E.c_id=?";
		try{
			PreparedStatement ps = conn.prepareStatement(queryStudentId);
			ps.setString(1, courseId);
			ResultSet rs = ps.executeQuery();
			
			String query3 = "Select * from "
					+ "Has_Solved "
					+ "WHERE ex_id=? and St_id=?";
			
			String query="SELECT Distinct E.EX_ID "
					+ "FROM HAS_SOLVED H, TOPICS T, EXERCISES E "
					+ "WHERE H.st_id=? and H.ex_id=E.ex_id and E.tp_id=T.tp_id and T.c_id=?";
			
			while(rs.next()){
				StudentReport report = new StudentReport();
				int studentId=rs.getInt(1);
				report.setStudentId(studentId);
				report.setName(rs.getString(2));
				List<Integer[]> scoresPerHW = new ArrayList<Integer[]>();
				List<Integer> scorePerPolicy = new ArrayList<Integer>();
				
				try{
					PreparedStatement ps1= conn.prepareStatement(query);
					ps1.setInt(1, studentId);
					ps1.setString(2, courseId);
					ResultSet rs1=ps1.executeQuery();
					
					while(rs1.next()) {
						PreparedStatement ps2= conn.prepareStatement(query3);
						ResultSet rs2;
						int exerciseId = rs1.getInt(1);
						List<Integer> scores = new ArrayList<>();
						scores.add(exerciseId);
						try{
							ps2.clearParameters();
							ps2.setInt(1, exerciseId);
							ps2.setInt(2, studentId);
							rs2 = ps2.executeQuery();
							while (rs2.next()) {
							    scores.add(rs2.getInt(3));
							}
						}catch(SQLException e){
							e.printStackTrace();
						}finally{
							closeStatement(ps2);
						}
						Integer [] scoreIntegers = new Integer[scores.size()];
						for(int i = 0 ; i < scores.size() ; ++i){
							scoreIntegers[i] = scores.get(i);
						}
//						scoresPerHW.add((Integer [])scores.toArray());
						scoresPerHW.add(scoreIntegers);
						report.setScoresPerHW(scoresPerHW);
						scorePerPolicy.add((Integer)obtainedScore(exerciseId,studentId));
					}
				}
				catch(Exception e) {
					System.out.println(e);
				}
				report.setScorePerPolicy(scorePerPolicy);
				reportList.add(report);
			}
//			for(StudentReport report :reportList){
//				String query="SELECT Distinct E.EX_ID FROM HAS_SOLVED H, TOPICS T, EXERCISES E WHERE H.st_id=? and H.ex_id=E.ex_id and E.tp_id=T.tp_id and T.c_id=?"	
//				PreparedStatement ps1 = conn.prepareStatement(query);
//				ps1.setInt(1, report.getStudentId());
//				ps1.setString(2, courseId);
//				ResultSet rs1 = ps1.executeQuery();
//				Integer[] arr= new Integer[2];
//				List<Integer[]> list=new ArrayList<Integer[]>();
//				while(rs1.next()){
//					arr[0]=rs1.getInt(1);
//					arr[1]=rs1.getInt(2);
//					list.add(arr);
//				}
//				report.setScoresPerHW(list);
//			}	
		}catch(SQLException e){
			System.out.println(e);
			return null;
		}
		return reportList;
	}

	// Sumer
	// Also tested by: GV
	public List<Exercise> getExercisesForCourse(String courseId){
		// Returns a list of all the exercises in the course.
		// Fields required in Exercise:
		// 1. Name
		// 2. ID
		// 3. Mode
		// 4. Start Date
		// 5. End Date
		// 6. Number of questions
		// 7. Number of retries
		// 8. Scoring Policy
		//
		//
		//				String sql = 'select ex_id, ex_name, ex_mode, ex_start_date, ex_end_date, num_questions,
		//				num_retires, policy, pt_correct, pt_incorrect from Exercises E, Topics T where E.tp_id = T.tp_id
		//				and T.c_id = ?';
		//				PreparedStatement ps = conn.prepareStatement(sql);
		//				ps.setInt(1, Integer.parseInt(courseId));
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql;
		int id=-1, num_questions=-1, num_retries=-1, topic_id=-1, pt_correct=-1, pt_incorrect=-1;
		Date start_date = null, end_date = null;
		String name="", mode="", policy="", s_date="", e_date="";
		ExerciseMode e_mode = null;
		ScroingPolicy sp = null;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		HashSet<Integer> qIds = new HashSet<Integer>();
		List<Exercise> ans = new ArrayList<Exercise>();
		try {
			sql = "select ex_id, ex_name, ex_mode, ex_start_date, ex_end_date, num_questions, num_retries, policy, E.tp_id, pt_correct, pt_incorrect from Exercises E, Topics T where E.tp_id = T.tp_id and T.c_id = ?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, courseId);
			rs = ps.executeQuery();
			//			ResultSetMetaData rsmd = rs.getMetaData();
			//			   int columnsNumber = rsmd.getColumnCount();
			//			   while (rs.next()) {System.out.println();
			//			       for (int i = 1; i <= columnsNumber; i++) {
			//			           if (i > 1) System.out.print(",  ");
			//			           String columnValue = rs.getString(i);
			//			           System.out.print(columnValue + " " + rsmd.getColumnName(i));
			//			       }}
			while(rs.next()) {
				id = rs.getInt(1);
				name = rs.getString(2);
				mode = rs.getString(3);
				start_date = rs.getDate(4);
				end_date = rs.getDate(5);
				num_questions = rs.getInt(6);
				num_retries = rs.getInt(7);
				policy = rs.getString(8);
				topic_id = rs.getInt(9);
				pt_correct = rs.getInt(10);
				pt_incorrect = rs.getInt(11);


				mode = mode.toLowerCase();
				policy = policy.toLowerCase();
				s_date = df.format(start_date);
				e_date = df.format(end_date);
				if(mode != null) {
					if(mode.equals("adaptive"))
						e_mode = ExerciseMode.Adaptive;
					else
						e_mode = ExerciseMode.Random;
				}
				if(policy != null) {
					if(policy.equals("latest"))
						sp = ScroingPolicy.Latest;
					else {
						if(policy.equals("maximum"))
							sp = ScroingPolicy.Maximum;
						else
							sp = ScroingPolicy.Average;
					}
				}
				//			System.out.println(""+e_mode+sp+name+s_date+e_date+num_questions+num_retries+id+qIds+pt_correct+pt_incorrect+topic_id);
				ans.add(new Exercise(e_mode, sp, name, s_date, e_date, num_questions, num_retries, id, qIds, pt_correct,
						pt_incorrect, topic_id ));
			}
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}

		return ans;
	}

	// Approved by GV
	public int assignTAToCourse(String TAId, String courseId){
		// Returns true if the TA was successfully assigned to the course.
		PreparedStatement pstmt = null;
		int TANumericalId = getId(TAId, UserType.TA);
		try{ 			
			// Now, insert into HasTA
			String query = "INSERT INTO HasTA "
					+ "VALUES(?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, courseId);
			pstmt.setInt(2, TANumericalId);

			if(pstmt.executeUpdate() == 0){
				// Failure, already added.
				return 0;
			}else{
				// Added.
				return 1;
			}
		}catch(SQLException e){
			// Failure, constraint violation.
			return e.getErrorCode();
		}finally{
			closeStatement(pstmt);
		}
	}

	// Akanksha & Sumer
	// Also tested by: GV
	public List<Question> getQuestionsForCourse(String courseId){
		// Returns a list containing all the questions in the course.
		// Fields required in a Question:
		// All.

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Question> qs = new ArrayList<Question>();
		String sql;
		try {
			sql = "SELECT  q_text, q_hint, q_del_soln, difficulty, q_id "
					+ "FROM QUESTIONS q, TOPICS t "
					+ "WHERE q.tp_id=t.tp_id and t.c_id=?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, courseId);
			rs = ps.executeQuery();
			while(rs.next()) {
				Question q = new Question();
				q.setText(rs.getString(1));
				q.setHint(rs.getString(2));
				q.setDetailedSolution(rs.getString(3)); 
				q.setDifficultyLevel(rs.getInt(4));
				q.setId(rs.getInt(5));
				qs.add(q);
			}
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}
		return qs;
	}

	// Sumer
	public List<Question> getQuestionsForCourseAndTopic(String courseId,int topicId){
		// Returns a list containing all the questions in the course.
		// Fields required in a Question:
		// All.

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Question> qs = new ArrayList<Question>();
		String sql;
		try {
			sql = "SELECT  q_text, q_hint, q_del_soln, difficulty, q_id "
					+ "FROM QUESTIONS q, TOPICS t "
					+ "WHERE q.tp_id=t.tp_id and t.c_id=? and t.tp_id=?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, courseId);
			ps.setInt(2, topicId);
			rs = ps.executeQuery();
			while(rs.next()) {
				Question q = new Question();
				q.setText(rs.getString(1));
				q.setHint(rs.getString(2));
				q.setDetailedSolution(rs.getString(3)); 
				q.setDifficultyLevel(rs.getInt(4));
				q.setId(rs.getInt(5));
				qs.add(q);
			}
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}
		return qs;
	}

	// Akanksha
	// Tested by GV
	public List<Question> searchQuestionsWithTopicId(int topicId){
		// Returns a list of questions in topic with id = topicId.

		List<Question> questions = new ArrayList<>();
		String query = "SELECT q.tp_id,t.tp_name, q_id, q_text,q_del_soln, difficulty "+
				"FROM QUESTIONS q, MASTER_TOPICS t "+
				"WHERE q.tp_id=t.tp_id and q.tp_id = ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, topicId);

			rs = pstmt.executeQuery();
			while(rs.next()){
				Question q = new Question();
				q.setTopicId(rs.getInt(1));
				q.setTopicName(rs.getString(2));
				q.setId(rs.getInt(3));
				q.setText(rs.getString(4));
				q.setQuestionType(getQuestionType(q.getId()));
				q.setDetailedSolution(rs.getString(5)); 
				q.setDifficultyLevel(rs.getInt(6));
				questions.add(q);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return questions;
	}

	// Akanksha
	// Tested by GV
	public List<Question> searchQuestionsWithQuestionId(int qId){
		// Returns a list of questions based on search by question ID.

		List<Question> questions = new ArrayList<>();
		String query = "SELECT q.tp_id,t.tp_name, q_id, q_text,q_del_soln, difficulty "+
				"FROM QUESTIONS q, MASTER_TOPICS t "+
				"WHERE q.tp_id=t.tp_id and q.q_id = ?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, qId);

			rs = pstmt.executeQuery();
			while(rs.next()){
				Question q = new Question();
				q.setTopicId(rs.getInt(1));
				q.setTopicName(rs.getString(2));
				q.setId(rs.getInt(3));
				q.setText(rs.getString(4));
				q.setQuestionType(getQuestionType(q.getId()));
				q.setDetailedSolution(rs.getString(5)); 
				q.setDifficultyLevel(rs.getInt(6));
				questions.add(q);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return questions;
	}

	// Done
	public boolean addQuestionToQuestionBank(Question question){
		// Returns true if the question was successfully added to the DB.
		PreparedStatement pstmt = null;
		try{ 			
			// Now, insert into HasTA
			String query = "INSERT INTO QUESTIONS "
					+ "VALUES(?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, question.getTopicId());
			pstmt.setInt(2, question.getId());
			pstmt.setString(3, question.getText());
			pstmt.setString(4, question.getHint());
			pstmt.setString(5, question.getDetailedSolution());
			pstmt.setInt(6, question.getDifficultyLevel());

			if(pstmt.executeUpdate() == 0){
				// Failure, already added.
				return false;
			}

			if(question.getQuestionType().toString().equals("Fixed")) {
				try {
					query = "Insert into fixed_questions values(?,?)";
					PreparedStatement ps = conn.prepareStatement(query);
					ps.setInt(1,question.getId());
					String t[][]=question.getCorrectAnswers();
					ps.setString(2,t[0][0]);
					if(ps.executeUpdate() == 0){
						// Failure, already added.
						return false;
					}

					query = "Insert into fixed_inc_answers values(?,?)";
					String t1[]=question.getIncorrectAnswers();
					for(int i=0;i<t1.length;i++) {
						PreparedStatement ps1 = conn.prepareStatement(query);
						ps1.setInt(1,question.getId());
						ps1.setString(2, t1[i]);
						if(ps1.executeUpdate() == 0){
							// Failure, already added.
							return false;
						}
					}
				}
				catch(SQLException e){
					// Failure, constraint violation.
					e.printStackTrace();
					return false;
				} //end of fixed part
			}else {
				String t[][]=question.getParameterValues();
				int noOfCombs=t[0].length;
				for(int i=0;i<noOfCombs;i++) {
					for(int j=0;j<t.length;j++)
						try {
							query = "Insert into param_questions values(?,?,?,?)";
							PreparedStatement ps = conn.prepareStatement(query);
							ps.setInt(1,question.getId());
							ps.setInt(2, (j+1));
							ps.setInt(3, (i+1));
							ps.setString(4,t[j][i]);
							if(ps.executeUpdate() == 0){
								// Failure, already added.
								return false;
							}
						}
					catch(SQLException e){
						// Failure, constraint violation.
						e.printStackTrace();
						return false;
					}
					try {
						query = "Insert into param_answers values(?,?,?)";
						PreparedStatement ps = conn.prepareStatement(query);
						ps.setInt(1,question.getId());
						ps.setInt(2,(i+1));
						ps.setString(3,question.getCorrectAnswers()[i][0]);
						if(ps.executeUpdate() == 0){
							// Failure, already added.
							return false;
						}
					}catch(SQLException e){
						// Failure, constraint violation.
						e.printStackTrace();
						return false;
					}
					int l=question.getIncorrectAnswers().length;
					for(int k=0;k<l/noOfCombs;k++)
						try {
							query = "Insert into param_inc_questions values(?,?,?)";
							PreparedStatement ps = conn.prepareStatement(query);
							ps.setInt(1,question.getId());
							ps.setInt(2,(i+1));
							ps.setString(3,question.getIncorrectAnswers()[i*noOfCombs+k]);
							if(ps.executeUpdate() == 0){
								// Failure, already added.
								return false;
							}
						}catch(SQLException e){
							// Failure, constraint violation.
							e.printStackTrace();
							return false;
						}
				} //end for
			}


		}catch(SQLException e){
			// Failure, constraint violation.
			e.printStackTrace();
			return false;
		}finally{
			closeStatement(pstmt);
		}
		return true;
	}

	// Checked :Sumer
	// Done
	public List<Question> getQuestionsInExercise(int exerciseId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Question> qs = new ArrayList<Question>();
		String sql;
		try {
			sql = "SELECT Q.q_text, Q.q_hint, Q.q_id, Q.difficulty, Q.tp_id, Q.q_del_soln "
					+ "FROM Questions Q, Questions_In_Ex E "
					+ "WHERE E.q_id=Q.q_id and E.ex_id=? ";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, exerciseId);
			rs = ps.executeQuery();
			while(rs.next()) {
				String text = rs.getString(1);
				String hint = rs.getString(2);
				int id = rs.getInt(3);
				int difficulty = rs.getInt(4);
				int topicId = rs.getInt(5);
				String detailedSolution = rs.getString(6);
				qs.add(new Question(text, hint, id, difficulty, topicId, detailedSolution));
			}
			return qs;
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}
		return null;

	}

	public java.sql.Date getDate(String date){
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		df.setLenient(false);
		try {
			return (new java.sql.Date(df.parse(date).getTime()));			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// Sumer
	// Done
	public boolean addExerciseToCourse(Exercise ex, String courseId){
		PreparedStatement pstmt = null;
		try{ 			
			// Now, insert into topics
			String query = "Insert into Exercises Values(?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, ex.getId());
			pstmt.setString(2,ex.getName());
			pstmt.setString(3,ex.getExerciseMode().toString());
			pstmt.setDate(4,getDate(ex.getStartDate()));
			pstmt.setDate(5,getDate(ex.getEndDate()));
			pstmt.setInt(6, ex.getNumQuestions());
			pstmt.setInt(7, ex.getNumRetries());
			pstmt.setString(8,ex.getScroingPolicy().toString());
			pstmt.setInt(9,ex.getTopicId());
			pstmt.setInt(10,ex.getPointsAwardedPerCorrectAnswer());
			pstmt.setInt(11,ex.getPointsDeductedPerIncorrectAnswer());

			if(pstmt.executeUpdate() == 0){
				// Failure
				return false;
			}

			// Add questions, if present.
			if(ex.hasQuestions()){
				for(int qId : ex.getQIds()){
					addQuestionToExercise(qId, ex.getId());
				}
			}
			
		}catch(SQLException e){
			return false;
		}finally{
			closeStatement(pstmt);
		}
		return true;
	}

	//Akanksha & Sumer
	public boolean addQuestionToExercise(int qId, int eId){
		// Returns true if the question was successfully added to the exercise.
		PreparedStatement pstmt = null;
		try{ 			
			// Now, insert into topics
			String query = "INSERT INTO Questions_In_Ex "
					+ "VALUES(?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, eId);
			pstmt.setInt(2, qId);

			if(pstmt.executeUpdate() == 0){
				// Failure
				return false;
			}
			String query1 = " UPDATE Exercises "
					+ "SET NUM_QUESTIONS = (Select Count(*) from QUESTIONS_IN_EX where ex_id = ?) "
					+ "WHERE ex_id = ?";
			PreparedStatement pstmt1 = conn.prepareStatement(query1);
			pstmt1.setInt(1, eId);
			pstmt1.setInt(2, eId);
			pstmt1.executeQuery();
			if(pstmt1.executeUpdate() == 0){
				// Failure
				return false;
			}
		}catch(SQLException e){
			return false;
		}finally{
			closeStatement(pstmt);
		}
		return true;
	}

	//Akanksha
	public boolean removeQuestionFromExercise(int qId, int eId){
		// Returns true if the question was successfully removed from the exercise.
		PreparedStatement pstmt = null;
		try{ 			
			String query = "DELETE FROM Questions_In_Ex WHERE q_id=? and ex_id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, qId);
			pstmt.setInt(2, eId);

			if(pstmt.executeUpdate() == 0){
				return false;
			}
		}catch(SQLException e){
			// Failure, constraint violation.
			e.printStackTrace();
			return false;
		}finally{
			closeStatement(pstmt);
		}
		return true;
	}

	// Done. Verified - Udit
	public Exercise getExercise(int exerciseId){
		// Returns the exercise associated with the exerciseId
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql;
		int id=-1, num_questions=-1, num_retries=-1, topic_id=-1, pt_correct=-1, pt_incorrect=-1;
		Date start_date = null, end_date = null;
		String name="", mode="", policy="", s_date="", e_date="";
		ExerciseMode e_mode = null;
		ScroingPolicy sp = null;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		HashSet<Integer> qIds = new HashSet<Integer>();

		try {
			sql = "select ex_id, ex_name, ex_mode, ex_start_date, ex_end_date, num_questions, num_retries, policy"
					+ ", tp_id, pt_correct, pt_incorrect from Exercises where ex_id=?";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, exerciseId);
			rs = ps.executeQuery();
			while(rs.next()) {
				id = rs.getInt(1);
				name = rs.getString(2);
				mode = rs.getString(3);
				start_date = rs.getDate(4);
				end_date = rs.getDate(5);
				num_questions = rs.getInt(6);
				num_retries = rs.getInt(7);
				policy = rs.getString(8);
				topic_id = rs.getInt(9);
				pt_correct = rs.getInt(10);
				pt_incorrect = rs.getInt(11);

			}
			mode = mode.toLowerCase();
			policy = policy.toLowerCase();
			s_date = df.format(start_date);
			e_date = df.format(end_date);
			if(mode != null) {
				if(mode.equals("adaptive"))
					e_mode = ExerciseMode.Adaptive;
				else
					e_mode = ExerciseMode.Random;
			}
			if(policy != null) {
				if(policy.equals("latest"))
					sp = ScroingPolicy.Latest;
				else {
					if(policy.equals("maximum"))
						sp = ScroingPolicy.Maximum;
					else
						sp = ScroingPolicy.Average;
				}
			}
			sql = "select q_id from Questions_In_Ex where ex_id = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, exerciseId);
			rs = ps.executeQuery();
			while(rs.next()) {
				qIds.add(rs.getInt(1));
			}
			return new Exercise(e_mode, sp, name, s_date, e_date, num_questions, num_retries, id, qIds, pt_correct,
					pt_incorrect, topic_id );

		}

		catch(Throwable oops){
			oops.printStackTrace();
		}

		return null;
	}

	//Akanksha
	public List<String[]> getCurrentOpenUnattemptedHWs(String courseId){
		// Returns the IDs of the exercises that are:
		// 1. currently open and;
		// 2. Can be attempted by the student.
		// Returns null if there are none.
		int student_id = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql;
		int exercise_id;
		Date end_date;

		List<String []> exercise_list = null;

		int numRetries=0;
		
		try {

			student_id =getId(loggedInUserId, loggedInUserType);
			/*					sql = "select st_id from Students where userid = ?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, user_id);
					rs = ps.executeQuery();
					while(rs.next()) {
						student_id = rs.getInt(1);
					}
			 */
			if(student_id == -1) 
				 return null;
			 /*
					sql = "select c_id from Enrolled_In where c_id = ? and st_id = ?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, courseId);
					ps.setInt(2, student_id);
					rs = ps.executeQuery();
					while(rs.next()) {
						c_id = rs.getString(1);
					}
					if(!courseId.equals(c_id))
						return null;
			  */
	/*		 int max_attempted=0;
			 String sql1 ="SELECT COUNT(*) AS ATTEMPTS FROM HAS_SOLVED WHERE EX_ID=? AND ST_ID=?";
			 PreparedStatement ps1 = conn.prepareStatement(sql1);
			 ps1.setString(1, courseId);
			 ps1.setInt(2, student_id);
			 ResultSet rs1 = ps1.executeQuery();
			 while(rs1.next()) {
				 max_attempted=rs.getInt(1);
			 }
			 String sql2 ="SELECT NUM_RETRIES FROM EXERCISES E WHERE EX_ID=?";

			 int maxRetriesPossible=0;
			 PreparedStatement ps2 = conn.prepareStatement(sql2);
			 ps2.setString(1, courseId);
			 ps2.setInt(2, student_id);
			 ResultSet rs2 = ps2.executeQuery();
			 while(rs2.next()) {
				 maxRetriesPossible=rs2.getInt(1);
			 }
			 if(max_attempted>=maxRetriesPossible){
				 return null;
			 }
*/			 sql = "select ex_id, ex_end_date, num_retries from Exercises E, Topics T where T.c_id = ?"
					 + " and E.tp_id = T.tp_id";
			 ps = conn.prepareStatement(sql);
			 ps.setString(1, courseId);
	
			 rs = ps.executeQuery();
			 while(rs.next()) {
				 exercise_id = rs.getInt(1);
				 end_date = rs.getDate(2);
				 
				 numRetries = rs.getInt(3);	
				 if(isExerciseOpen(end_date) && !isMaxAttemptReached(exercise_id, numRetries)) {
					 if(exercise_list == null) exercise_list = new ArrayList<>();
					 exercise_list.add(new String[]{Integer.toString(exercise_id), end_date.toString()});
				 }
			 }
			 return exercise_list;	
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}
		return null;
	}

	private boolean isMaxAttemptReached(int exercise_id, int numRetries) throws SQLException {
		// TODO Auto-generated method stub
		 int max_attempted=0;
		 String sql1 ="SELECT COUNT(*) AS ATTEMPTS FROM HAS_SOLVED WHERE EX_ID=? AND ST_ID=?";
		 PreparedStatement ps1 = conn.prepareStatement(sql1);
		 ps1.setInt(1, exercise_id);
		 ps1.setInt(2, loggedInUserNumericalId);
		 ResultSet rs1 = ps1.executeQuery();
		 while(rs1.next()) {
			 max_attempted=rs1.getInt(1);
		 }
		 if(numRetries <= max_attempted){
			 return true;
		 }
		return false;
	}

	// Approved by GV
	public List<StudentHWAttempt> getAttamptedHWs(String courseId){
		// Returns all the attempts of the student 
		// in course with course ID courseId.

		PreparedStatement ps = null, ps2 = null;
		ResultSet rs = null, rs2 = null;
		String query1 = "SELECT H.with_score, H.submit_time, J.ex_end_date, H.ex_id, J.pt_correct, J.pt_incorrect "
				+ "FROM Has_Solved H, ("
				+ "SELECT E.ex_id, E.pt_correct, E.pt_incorrect, E.num_questions, E.ex_end_date "
				+ "FROM Exercises E, Topics T "
				+ "WHERE E.tp_id = T.tp_id and T.c_id = ?"
				+ ") J "
				+ "WHERE H.st_id = ? AND H.ex_id = J.ex_id";
		
		String query2 = "SELECT DISTINCT Q.q_text, Q.q_hint, A.is_correct, Q.q_del_soln, Q.q_id "
				+ "FROM Questions Q, Questions_In_Ex QE, Assign_Attempt A "
				+ "WHERE QE.ex_id=? and Q.q_id = QE.q_id "
				+ "and A.ex_id = ? and A.st_id = ? and A.q_id = QE.q_id";
		
		int pt_correct, pt_incorrect, exerciseId;
		double score;
		Date submit, ex_end_date;
		boolean hasDeadlinePassed;
		List<StudentHWAttempt> hw_attempt = new ArrayList<>();

		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			ps = conn.prepareStatement(query1);
			ps.setString(1, courseId);
			ps.setInt(2, loggedInUserNumericalId);

			rs = ps.executeQuery();
			while(rs.next()) {
				score = rs.getDouble(1);
				submit = rs.getDate(2);
				ex_end_date = rs.getDate(3);
				exerciseId = rs.getInt(4);
				pt_correct = rs.getInt(5);
				pt_incorrect = rs.getInt(6);
				hasDeadlinePassed = (!isExerciseOpen(ex_end_date));

				try{
					ps2 = conn.prepareStatement(query2);
					ps2.setInt(1, exerciseId);
					ps2.setInt(2, exerciseId);
					ps2.setInt(3, loggedInUserNumericalId);
					rs2 = ps2.executeQuery();
					
					String q_text, q_hint, q_del_soln;
					List<Question> questions = new ArrayList<Question>();
					List<Boolean> wasCorrectlyAnswered = new ArrayList<Boolean>();
					
					while(rs2.next()) {
						q_text = rs2.getString("q_text");
						q_hint = rs2.getString("q_hint");
						
						BigDecimal is_correct = (BigDecimal)rs2.getObject("is_correct");

						if(is_correct == null) {
							wasCorrectlyAnswered.add(null);
						}else if(is_correct.compareTo(BigDecimal.ONE) == 0){
							wasCorrectlyAnswered.add(true);
						}else{
							wasCorrectlyAnswered.add(false);
						}
						q_del_soln = null;
						if(hasDeadlinePassed) {
							q_del_soln = rs2.getString("q_del_soln");
						}
						questions.add(new Question(q_text, q_hint, q_del_soln));
					}
					
					
					hw_attempt.add(new StudentHWAttempt(score, df.format(submit), questions, wasCorrectlyAnswered, 
							pt_correct * questions.size()/2, pt_correct, pt_incorrect, hasDeadlinePassed, exerciseId));
				}catch(SQLException e){
					e.printStackTrace();
				}finally {
					closeResultSet(rs2);
					closeStatement(ps2);
				}

			}

			return hw_attempt;

		}
		catch(SQLException oops){
			oops.printStackTrace();
		}finally {
			closeResultSet(rs);
			closeStatement(ps);
		}

		return null;
	}	

	// Approved by GV
	public boolean addHWAttempt(StudentHWAttempt attempt, String courseId, int exerciseId){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;

		int attempt_number = 0;

		try {
			query = "SELECT MAX(AA.attempt_num) "
					+ "FROM Assign_Attempt AA "
					+ "WHERE AA.st_id = ? AND AA.ex_id = ?";

			pstmt = conn.prepareStatement(query);

			pstmt.setInt(1, loggedInUserNumericalId);
			pstmt.setInt(2, exerciseId);

			rs = pstmt.executeQuery();
			if(rs.next()) {
				attempt_number = rs.getInt(1);
			}
			attempt_number++;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}finally {
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		List<Question> questions = attempt.getQuestions();
		List<Boolean> wasCorrectlyAnswered = attempt.getWasCorrectlyAnswered();
		String queryForAssign_Attempt = "INSERT INTO "
				+ "Assign_Attempt "
				+ "VALUES(?, ?, ?, ?, ?, ?)";
		String queryForHas_Solved = "INSERT INTO Has_Solved "
				+ "VALUES(?, ?, ?, ?, ?)";

		for(int i=0;i<questions.size();i++) {
			Question question = questions.get(i);

			try {
				pstmt = conn.prepareStatement(queryForAssign_Attempt);

				pstmt.setInt(1, attempt_number);
				pstmt.setInt(2, exerciseId);
				pstmt.setInt(3, question.getId());
				pstmt.setInt(4, loggedInUserNumericalId);

				if(wasCorrectlyAnswered.get(i) == null){
					pstmt.setNull(5, oracle.jdbc.OracleTypes.NUMBER);
				}else if(wasCorrectlyAnswered.get(i)){
					// Correct
					pstmt.setInt(5, 1);
				}else{
					// Incorrect
					pstmt.setInt(5, 0);
				}

				if(question.getQuestionType() == QuestionType.Fixed){
					pstmt.setNull(6, oracle.jdbc.OracleTypes.INTEGER);
				}else{
					pstmt.setInt(6, question.getCombinationNumber());
				}

				if(pstmt.executeUpdate() == 0) {
					return false;
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}finally {
				closeStatement(pstmt);
			}
		}
		
		try {
			pstmt = conn.prepareStatement(queryForHas_Solved);
//			System.out.println(loggedInUserNumericalId);
//			System.out.println(exerciseId);
//			System.out.println(attempt.getScore());
//			System.out.println(getDate(attempt.getSubmissionDateTime()));
			
			pstmt.setInt(1, loggedInUserNumericalId);
			pstmt.setInt(2, exerciseId);
			pstmt.setDouble(3, attempt.getScore());
			pstmt.setDate(4, getDate(attempt.getSubmissionDateTime()));
			pstmt.setInt(5, attempt_number);
			
			
			if(pstmt.executeUpdate() == 0)
				return false;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	// Doing: GV

	public Question getNextQuestionInAdaptiveExercise(int exerciseId, String courseId, Boolean wasLastAnsweredCorrectly, int lastQuestionDifficulty, int topicId, List<Integer> questionsAlreadyDone){
		System.out.println(exerciseId + courseId + wasLastAnsweredCorrectly);
		// Get next question based on the user's answer.
		// wasLastAnsweredCorrectly = null for first question or if the last question
		// was skipped.

		int thisQuestionDifficulty = 3;
		
		if(wasLastAnsweredCorrectly != null && wasLastAnsweredCorrectly)
			thisQuestionDifficulty = lastQuestionDifficulty + 1;
		else if(wasLastAnsweredCorrectly != null)
			thisQuestionDifficulty = lastQuestionDifficulty - 1;

		// Since minimum difficulty is 1.
		thisQuestionDifficulty = Math.max(lastQuestionDifficulty, MIN_QUESTION_DIFFICULTY);
		// Since maximum difficulty is 6.
		thisQuestionDifficulty = Math.min(lastQuestionDifficulty, MAX_QUESTION_DIFFICULTY);
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder queryToGetQIdBuilder = new StringBuilder(); 
		
		queryToGetQIdBuilder.append("SELECT Q.q_id, Q.q_text "
				+ "FROM Questions Q "
				+ "WHERE Q.tp_id = ? AND Q.difficulty = ?");
		
		if(questionsAlreadyDone.size() != 0){
			queryToGetQIdBuilder.append(" AND Q.q_id NOT IN (");
			for(int i = 0 ; i < questionsAlreadyDone.size() ; ++i){
				if(i == questionsAlreadyDone.size() - 1){
					queryToGetQIdBuilder.append(questionsAlreadyDone.get(i) + ")");
				}else{
					queryToGetQIdBuilder.append(questionsAlreadyDone.get(i) + ", ");
				}
			}
		}
		
		String queryToGetQId = queryToGetQIdBuilder.toString();
		
		
		try {
			
			ps = conn.prepareStatement(queryToGetQId);
			
			boolean searchExhausted = false;
			while(true){
				ps.setInt(1, topicId);
				ps.setInt(2, thisQuestionDifficulty);
				rs = ps.executeQuery();
				
				if(rs.next()) {
					// we have a valid question with this difficulty.
					int q_id = rs.getInt(1);
					
					Question question;
					// Try to find it in fixed type:
					question = getFixedQuestion(q_id);
					if(question == null){
						// It is a parametric type of question.
						question = get_parameterized_question(q_id);
					}
					question.setId(q_id);
					question.setDifficultyLevel(thisQuestionDifficulty);
					question.setText(rs.getString("q_text"));
					questionsAlreadyDone.add(q_id);
					return question;
				}else{
					// No question with this difficulty level.
					if(searchExhausted){
						// No more search. Invalid exercise.
						return null;
					}
					if(wasLastAnsweredCorrectly != null && wasLastAnsweredCorrectly == true){
						// Try to increase difficulty.
						thisQuestionDifficulty += 1;
						if(thisQuestionDifficulty > MAX_QUESTION_DIFFICULTY){
							// Exhausted search space.
							thisQuestionDifficulty = lastQuestionDifficulty;
							searchExhausted = true;
						}
					}else{
						// Try to decrease difficulty.
						thisQuestionDifficulty -= 1;
						if(thisQuestionDifficulty < MIN_QUESTION_DIFFICULTY){
							// Exhausted search space.
							thisQuestionDifficulty = lastQuestionDifficulty;
							searchExhausted = true;
						}
					}
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	private Question getFixedQuestion(int qId){
		String correctAnswer, incorrectAnswer;
		List<String> options = new ArrayList<>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String queryTofindCorrectAnswerForFixedQuestion = "SELECT J.q_ans "
				+ "FROM ("
				+ "SELECT * "
				+ "FROM Fixed_Questions FQ "
				+ "WHERE FQ.q_id = ? "
				+ "ORDER BY dbms_random.value"
				+ ") J "
				+ "WHERE rownum <= 1";
		String queryTofindIncorrectAnswerForFixedQuestion = "SELECT J.q_inc_answers "
				+ "FROM ("
				+ "SELECT * "
				+ "FROM Fixed_Inc_Answers FIQ "
				+ "WHERE FIQ.q_id = ? "
				+ "ORDER BY dbms_random.value"
				+ ") J "
				+ "WHERE rownum <= 3";


		try{
			// Randomly choose 1 correct answer.
			pstmt = conn.prepareStatement(queryTofindCorrectAnswerForFixedQuestion);
			pstmt.setInt(1, qId);

			rs = pstmt.executeQuery();

			if(rs.next()){
				correctAnswer = rs.getString(1);
				options.add(correctAnswer);
			}else{

				return null;
			}

			closeResultSet(rs);
			closeStatement(pstmt);

			// Randomly choose at max 3 incorrect answers.
			pstmt = conn.prepareStatement(queryTofindIncorrectAnswerForFixedQuestion);
			pstmt.setInt(1, qId);

			rs = pstmt.executeQuery();
			while(rs.next()){
				incorrectAnswer = rs.getString(1);
				options.add(incorrectAnswer);
			}

			// Shuffle the options.
			Collections.shuffle(options);

			return new Question(options, (short)(options.indexOf(correctAnswer) + 2), qId);

		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return null;
	}

	public Question get_parameterized_question(int question_id) {
		try {
			String sql = "SELECT q_comb_num FROM (SELECT * FROM Param_Questions PQ WHERE PQ.q_id = ? "
					+ "ORDER BY dbms_random.value) where rownum <= 1";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, question_id);
			ResultSet rs = ps.executeQuery();
			int comb_num=-1, max_parameters=-1;
			while(rs.next()) {
				comb_num = rs.getInt(1);
			}
			sql = "SELECT MAX(q_par_num) from Param_Questions PQ where q_id = ? and q_comb_num = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, question_id);
			ps.setInt(2, comb_num);
			rs = ps.executeQuery();
			while(rs.next()) {
				max_parameters = rs.getInt(1);
			}
			ArrayList<String> parameter_values = new ArrayList<String>();
			for(int i = 1; i<=max_parameters; i++) {
				sql = "select q_param_value from Param_Questions where q_id = ? and q_par_num = ? and q_comb_num = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, question_id);
				ps.setInt(2, i);
				ps.setInt(3, comb_num);
				rs = ps.executeQuery();
				while(rs.next()) {
					parameter_values.add(rs.getString(1));
				}
			}
			sql = "select q_text, q_hint from Questions where q_id = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, question_id);
			rs = ps.executeQuery();
			String text = null, hint = null;
			while(rs.next()) {
				text = rs.getString(1);
				hint = rs.getString(2);
			}
			String refined_text = refine_parameterized_text(text, parameter_values);
			sql = "SELECT q_ans FROM (SELECT * FROM Param_Answers PA WHERE PA.q_id = ? "
					+ "and PA.q_comb_num = ? ORDER BY dbms_random.value) where rownum <= 1";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, question_id);
			ps.setInt(2, comb_num);
			rs = ps.executeQuery();
			String correct_answer=null;
			ArrayList<String> options = new ArrayList<String>();
			while(rs.next()) {
				correct_answer = rs.getString(1);
				options.add(correct_answer);
			}
			sql = "SELECT q_inc_ans FROM (SELECT * FROM Param_Inc_Questions PI WHERE PI.q_id = ? "
					+ "and PI.q_comb_num = ? ORDER BY dbms_random.value) where rownum <= 3";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, question_id);
			ps.setInt(2, comb_num);
			rs = ps.executeQuery();
			while(rs.next()) {
				options.add(rs.getString(1));
			}

			Collections.shuffle(options);
			short index_correct_answer = (short)(options.indexOf(correct_answer)+2);

			Question q = new Question(hint, options, index_correct_answer, comb_num, question_id);
			q.setText(refined_text);
			return q;

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public String refine_parameterized_text(String text, ArrayList<String> parameter_values) {
		for(int i=0; i<parameter_values.size(); i++) {
			text = text.replaceFirst("___", parameter_values.get(i));
		}
		return text;
	}

	// Approved by GV
	
	public List<Question> getQuestionsInRandomExercise(int exerciseId){

		// Returns the questions in the exercise created by the professor.
		// Fields required:
		// 1. Text
		// 2. Incorrect Answers.
		// 3. Correct Answer

		List<Question> questions = new ArrayList<Question>();
		String query = "SELECT q.q_text, q.q_id "
				+ "FROM QUESTIONS_IN_EX qe, QUESTIONS q "
				+ "WHERE qe.q_id=q.q_id and ex_id=?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, exerciseId);

			rs = pstmt.executeQuery();

			int qId;
			Question question;
			String qText;

			while(rs.next()){
				qText = rs.getString("q_text");
				qId = rs.getInt("q_id");
				if(getQuestionType(qId) == QuestionType.Fixed){
					question = getFixedQuestion(qId);
				}else{
					question = get_parameterized_question(qId);
				}
				question.setText(qText);
				questions.add(question);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		return questions;
	}

	// Done-Akanksha
	private int getId(String userId, UserType userType) {
		// TODO Auto-generated method stub
		String query;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String id_identifier;
		try{
			if(userType==UserType.Professor){
				query = "SELECT prof_id FROM Professor WHERE userid=?";
				id_identifier = "prof_id";
			}else{
				query = "Select st_id from students where userid=?";
				id_identifier = "st_id";
			}
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(id_identifier);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}

		// Invalid userId.
		return -1;
	}

	// Done by Udit
	public boolean isExerciseOpen(Date exerciseEndDate) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String  end_date_string = df.format(exerciseEndDate);
		LocalDate today = LocalDate.now();
		LocalDate end = LocalDate.parse(end_date_string);
		if(today.isBefore(end) || today.isEqual(end))
			return true;
		return false;	

	}
	
	// Akanksha
	// Verified: GV
	private QuestionType getQuestionType(int questionId){

		String query = "SELECT F.q_id "
				+ "FROM FIXED_QUESTIONS F "
				+ "WHERE F.q_id=?";

		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, questionId);
			rs = pstmt.executeQuery();
			if(rs.next()){
				return QuestionType.Fixed;
			}else{
				return QuestionType.Parameterized;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
	}
	
	//Sumer: tested

	public int obtainedScore(int exerciseId, int Student_id){
		/* 
		 * Returns obtained marks by a student as per exercise policy
		 */
		try{
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String query = "Select Policy from Exercises where ex_id=?";
			String policy=null;
			try{
				pstmt = conn.prepareStatement(query);
				pstmt.clearParameters();
				pstmt.setInt(1, exerciseId);
				rs = pstmt.executeQuery();
				while (rs.next()) {
				    policy=rs.getString(1);
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				closeResultSet(rs);
				closeStatement(pstmt);
			}
			query = "Select * from Has_Solved where ex_id=? and St_id=?";

			int counter=0;
			int scores[]= new int[100];
			try{
				pstmt = conn.prepareStatement(query);
				pstmt.clearParameters();
				pstmt.setInt(1, exerciseId);
				pstmt.setInt(2, Student_id);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
				    scores[counter++]=rs.getInt(3);
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				closeResultSet(rs);
				closeStatement(pstmt);
			}
			if(counter==0)
				return 0;
			if(policy.equals("Maximum")) {
				int max=scores[0];
				for(int i=1;i<counter;i++)
					if(scores[i]>max)
						max=scores[i];		
				return max;
			}
			else if(policy.equals("Latest"))
				return scores[counter-1];
			else {
				int avg=0;
				for(int i=0;i<counter;i++)
					avg+=scores[i];
				avg/=counter;
				return avg;
			}
		}
		catch(Exception e) 
		{
			System.out.println(e);
		}
		
		return 0;
	}
}