import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

enum UserType{
	TA, Professor, Student, InvalidUser;
}

class DBHandler{

	protected static final String jdbcUrl = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	private static Connection conn;
	private final String dbUserName, dbPassword;
	
	private String loggedInUserName, loggedInUserId;
	
	private UserType loggedInUserType;
	private int loggedInUserNumericalId;

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

	// Approved by GV.
	public Course getCourseInfo(String courseId){
		//Returns complete course object.
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query;
		
		List<Person> TAs = new ArrayList<>();
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
			String name = "c_name";
			String startDate = "c_start_date";
			String endDate = "c_end_date";
			String levelGrad = "levelGrad";
			String maxStudentsIdentifier = "max_students";
			query = "SELECT " + name + ", " + startDate + ", " + endDate + ", " + levelGrad + ", " + maxStudentsIdentifier + " "
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
			while(rs.next()){
				courseName 		= rs.getString(name);
				courseStartDate = rs.getDate(startDate);
				courseEndDate 	= rs.getDate(endDate);
				isGradLevel		= rs.getInt(levelGrad);
				if(isGradLevel == 1) courseLevel = CourseLevel.Grad;
				else courseLevel = CourseLevel.UnderGrad;
				maxStudents 	= rs.getInt(maxStudentsIdentifier);
				return new Course(courseId, courseName, courseStartDate, courseEndDate, TAs, 
						topics, studentsEnrolled, courseLevel, maxStudents);
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
	private List<Person> getStudentsEnrolledInCourse(String courseId){
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
	private List<Person> getTAsInCourse(String courseId){
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
 	public Boolean isTAIdValidForCourse(String TAId, String courseId){
 		
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
				return false;
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
				return null;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		
 		return true;
 	}
 	
 	// Approved AS
 	public Boolean addNewCourse(Course course){
		// Returns true if the course was successfully added.
 		String query = " INSERT INTO Courses "
		        + "VALUES (?, ?, ?, ?,?,?,?)";
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
	
 	// Approved AS
	public Boolean addNewStudentToCourse(String studentId, String courseId){
		// Returns true if the student was successfully added to the course.
/*		int studentNumericalId = getId(studentId, UserType.Student);
		
		if(studentNumericalId == -1){
			// Invalid student id
			return false;
		}
		
*/		String query = "INSERT INTO Enrolled_In (C_ID, ST_ID)"
		        + "VALUES (?, ?)";
		PreparedStatement pstmt = null;
		
		try{
		      pstmt = conn.prepareStatement(query);
		      pstmt.setString(1, courseId);
		      pstmt.setInt(2, Integer.parseInt(studentId));

		      if(pstmt.executeUpdate() == 1){
		    	  // Successfully added.
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
	
	// Approved AS
	public Boolean dropStudentFromCourse(String studentId, String courseId){
		// Returns true if the student was successfully dropped from the course.
		// or null if was already not in the course.
		PreparedStatement pstmt = null;
		try{ 			
 			String query = "DELETE FROM Enrolled_in WHERE st_id=? and c_id=?";
 			pstmt = conn.prepareStatement(query);
 			pstmt.setString(1, studentId);
 			pstmt.setString(2, courseId);
 			
 			if(pstmt.executeUpdate() == 0){
 				return null;
 			}else{
 				return true;
 			}
 		}catch(SQLException e){
 			// Failure, constraint violation.
 			e.printStackTrace();
 			return false;
 		}finally{
 			closeStatement(pstmt);
 		}
	}	
	
	public List<StudentReport> getStudentReports(String courseId){
		// Returns the report of all students in the course.
		// Fields required in StudentReport:
		// All.

		// *******TODO Aggregate records for every student*****
//		List<StudentReport> stReport = new ArrayList<StudentReport>();
//		String sql = 'select st_id, st_name, with_score, ex_id from Has_Solved H, Grad_Students G
//		where H.st_id = G.st_id and H.ex_id in (select ex_id from Exercises E, Topics T 
//		where T.c_id = ? and E.tp_id = T.tp_id )';
//		PreparedStatement ps = conn.prepareStatement(sql);
//		ps.setInt(1, Integer.parseInt(courseId));
//		ResultSet rs = ps.executeQuery();
//		while(rs.next()){
//			String st_id = Integetr.toString(rs.getInt(1));
//			String st_name = rs.getString(2);
//			String lName = '';
//			String with_score = Integer.toString(rs.getInt(3));
//			String e_id = Integer.toString(rs.getInt(4));
//			String[][] scores = {{e_id, with_score}};
//			stReport.add(new StudentReport(st_name, lName, scores));
//		}
//
//		
//		return stReport;
		return null;
	}
	
	
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
		return null;
	}
	
	// Approved by GV
	// !!!!!!!!!!!!!!!!!Please correct the HasTA Table!!!!!!!!!!!!!!!!!!!!!!!!
	public Boolean assignTAToCourse(String TAId, String courseId){
		// Returns true if the TA was successfully assigned to the course.
		PreparedStatement pstmt = null;
		int TANumericalId = getId(TAId, UserType.TA);
		if(TANumericalId == -1){
			// Invalid TA Id!
			return false;
		}
 		try{ 			
 			// Now, insert into HasTA
 			String query = "INSERT INTO HasTA "
 					+ "VALUES(?, ?)";
 			pstmt = conn.prepareStatement(query);
 			pstmt.setString(1, courseId);
 			pstmt.setInt(2, TANumericalId);
 			
 			if(pstmt.executeUpdate() == 0){
 				// Failure, already added.
 				return null;
 			}else{
 				// Added.
 				return true;
 			}
 		}catch(SQLException e){
 			// Failure, constraint violation.
 			e.printStackTrace();
 			return false;
 		}finally{
 			closeStatement(pstmt);
 		}
	}
	
	
	public List<Question> getQuestionsForCourse(String courseId){
		// Returns a list containing all the questions in the course.
		// Fields required in a Question:
		// All.
		
		List<Question> questions = new ArrayList<>();
		
		return questions;
	}
	
	
	public List<Question> searchQuestionsWithTopicId(int topicId){
		// Returns a list of questions in topic with id = topicId.
		
		List<Question> questions = new ArrayList<>();
		
		return questions;
	}
	
	
	public List<Question> searchQuestionsWithQuestionId(int qId){
		// Returns a list of questions based on search by question ID.
		
//		List<Question> questions = new ArrayList<>();
//		boolean isTopic = true;
//		int questionId;
//		String topic;
//		try{
//			questionId = Integer.parseInt(searchQuery);
//			isTopic = false;
//		}
//		catch(Exception e){
//			topic = searchQuery;
//		}
//		if(isTopic){
//			
//		}
//		return questions;
		return null;
	}
	
	
	public boolean addQuestionToQuestionBank(Question question){
		// Returns true if the question was successfully added to the DB.
		
		return false;
	}
	
	
	public List<Question> getQuestionsInExercise(int exerciseId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Question> qs = new ArrayList<Question>();
		String sql;
		try {
			sql = "select q_text, q_hint from Question Q, Questions_In_Ex E where E.q_id=Q.q_id"
					+ "and E.ex_id=?; ";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, exerciseId);
			rs = ps.executeQuery();
			while(rs.next()) {
				String text = rs.getString(1);
				String hint = rs.getString(2);
				qs.add(new Question(text, hint));
			}
			return qs;
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}
		return null;
		
	}
	
	
	public boolean addExerciseToCourse(Exercise exercise, String courseId){
		
		return true;
	}
	
//	public HashSet<String> getQIdsInExercise(int exerciseId){
//		
//		return new HashSet<>();
//	}
	
	
	public boolean addQuestionToExercise(int qId, int eId){
		// Returns true if the question was successfully added to the exercise.
		
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
			sql = "select ex_id, ex_name, ex_mode, ex_start_date, ex_end_date, num_questions, num_retires, policy"
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
			sql = "select q_id from Questions_In_Ex where ex_id = ?;";
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
	
	
	public List<String> getCurrentOpenUnattemptedHWs(String courseId){
		// Returns the IDs of the exercises that are:
		// 1. currently open and;
		// 2. Can be attempted by the student.
		// Returns null if there are none.
		String user_id = loggedInUserId;
		int student_id = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql, c_id;
		try {
			sql = "select st_id from Students where userid = ?;";
			ps = conn.prepareStatement(sql);
			ps.setString(1, user_id);
			rs = ps.executeQuery();
			while(rs.next()) {
				student_id = rs.getInt(1);
			}
			if(student_id == -1) 
				return null;
			sql = "select c_id from Enrolled_In where c_id = ? and st_id = ?;";
			ps = conn.prepareStatement(sql);
			ps.setString(1, courseId);
			ps.setInt(2, student_id);
			rs = ps.executeQuery();
			while(rs.next()) {
				c_id = rs.getString(1);
			}
			
		}
		catch(Throwable oops){
			oops.printStackTrace();
		}		
		
		return null;
	}
	
	
	public List<String> getAttemptedHWs(String courseId){
		// Returns the IDs of the exercises that are:
		// 1. attempted by the student.
		// Returns null if there are none.
		
		
		return null;
	}
	
	
	public List<StudentHWAttempt> getAttamptedHWsOverView(String courseId, int exerciseId){
		// Returns the attempts of the student for the exercise with Id exerciseId
		// in course with course ID courseId.
		// Fields required: score and submission date and time.
		// Returns null if there are none.
		
		return null;
	}	
	
	
	public StudentHWAttempt getHWAttemptDetails(){
		// Returns the fully constructed Student HW Attempt.
		// If the deadline has passed, contains detailed solution too.
		// If the optional hint is not present, it is set to null.
		
		return null;
	}
	
	
	public boolean addHWAttempt(StudentHWAttempt attempt, String courseId, int exerciseId){
		
		
		return true;
	}
	
	
	public Question getNextQuestionInAdaptiveExercise(int exerciseId, String courseId, Boolean wasLastAnsweredCorrectly){
		// Get next question based on the user's answer.
		// wasLastAnsweredCorrectly = null for first question or if the last question
		// was skipped.
		
		return null;
	}
	
	
	public List<Question> getQuestionsInRandomExercise(int exerciseId, String courseId){
		// Returns the questions in the exercise created by the professor.
		
		return new ArrayList<>();
	}

	//Done-Akanksha
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
}