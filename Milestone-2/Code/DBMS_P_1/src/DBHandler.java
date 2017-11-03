import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

enum LoggedInUserType{
	TA, Professor, Student, InvalidUser;
}

class DBHandler{

	protected static final String jdbcUrl = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	private static Connection conn;
	private final String dbUserName, dbPassword;
	
	private String loggedInUserName, loggedInUserId;
	private LoggedInUserType loggedInUserType;

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
	
	public LoggedInUserType login(String userId, String password) throws SQLException{	
		// By default, login any TA as TA. He can chooses if he wants
		// to continue as student or TA and informs.
		
		Connection conn= createConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT name, userid, roleid FROM users WHERE userid=? AND password=?");
		stmt.setString(1, userId);
		stmt.setString(2, password);
		ResultSet rs = stmt.executeQuery();
		boolean validResult=false;
		while(validResult==false && rs.next())  {
			loggedInUserName = rs.getString(1);
			loggedInUserId = rs.getString(2);
			int roleid = rs.getInt(3);
			if(roleid==1){
				loggedInUserType = LoggedInUserType.Professor;
			}else if(roleid==2){
				loggedInUserType = LoggedInUserType.TA;
			}else if(roleid==3){
				loggedInUserType = LoggedInUserType.Student;
			}
			validResult=true;
			isUserLoggedIn = true;
		}
		if(validResult==false){
			loggedInUserType = LoggedInUserType.InvalidUser;
			isUserLoggedIn = false;
		}
		
		return loggedInUserType;
	}
	
	public void changeTAToStudent(){
		loggedInUserType = LoggedInUserType.Student;
	}
	
	public void changeStudentToTA(){
		loggedInUserType = LoggedInUserType.TA;
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
	
	public LoggedInUserType getLoggedInUserType(){
		return loggedInUserType;
	}
	
	public List<String[]> getTaughtCoursesByProfessor(){
		// Return the taught courses by the logged in professor.
		// Syntax = <[courseName, courseId], [], []>

		List<String[]> taughtCourses = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String courseId, courseName;

		try{
			// Create a statement object that will send SQL statement to DB
			String query = "SELECT c_id, c_name FROM courses AS C WHERE userid = ?;";
			pstmt = conn.prepareStatement(query);
			pstmt.clearParameters();
			pstmt.setString(1, loggedInUserId);
			rs = pstmt.executeQuery(query);
			while (rs.next()) {
		    	courseName = rs.getString("c_name");
				courseId = Integer.toString(rs.getInt("c_id"));
				taughtCourses.add(new String[]{courseName, courseId});
			}
		}catch(Throwable oops) {
            oops.printStackTrace();
        }
		
		return taughtCourses;
	}
	
	public List<String[]> getTACourses(){
		// Return the courses for which the logged in user is TA.
		// Syntax = <[courseName, courseId], [], []>

//		List<String[]> TACourses = new ArrayList<>();
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		String courseId, courseName;
//		int studentId = Integer.parseInt(loggedInUserId);
//		try{
//			String sqlCourseDetails = "select C.c_id, C.c_name from Courses as C, Grad_Students as G where G.st_id = ?\
//			and G.TA_for = C.c_id;";
//			pstmt = conn.prepareStatement(sqlCourseDetails);
//			pstmt.clearParameters();
//			pstmt.setInt(1, studentId);
//			rs = pstmt.executeQuery(sqlCourseDetails);
//			while(rs.next()){
//				courseName = rs.getString("C.c_name");
//				courseId = Integer.toString(rs.getInt("C.c_id"));
//				TACourses.add(new String[]{courseName, courseId});
//			}
//		}
//		catch(Throwable oops){
//			oops.printStackTrace();
//		}
//		
//		return TACourses;
		return null;
	}
	
	public List<String[]> getStudentEnrolledCourses(){
		List<String[]> studentCourses = new ArrayList<String[]>();
		studentCourses.add(new String[]{"ALGO", "CSC-505"});
		studentCourses.add(new String[]{"SE", "CSC-510"});
		
		return studentCourses;
	}
	
	public boolean isProfessor(){
		return loggedInUserType == LoggedInUserType.Professor;
	}
	
	public boolean isTA(){
		return loggedInUserType == LoggedInUserType.TA;
	}
	
	public boolean isUserLoggedIn() {
		return isUserLoggedIn;
	}

	public Course getCourseInfo(String courseId){
		//Returns course ID, course name, start date, end date, array of TAs
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		String courseName, courseStartDate, courseEndDate;
//		int course_id = Integer.parseInt(courseId);
//		int numberOfTA = 0;
//		try{
//			String sqlTAdetails = "select st_name from Grad_Students where TA_for = ?;";
//			String sqlCount = "select count(*) from Grad_Students where TA_for = ?;";
//			pstmt = conn.prepareStatement(sqlCount);
//			pstmt.clearParameters();
//			pstmt.setInt(1, course_id);
//			while(rs.next()){
//				numberOfTA = rs.getInt("count(*)");
//			}
//			pstmt = conn.prepareStatement(sqlTAdetails);
//			pstmt.clearParameters();
//			pstmt.setInt(1, course_id);
//			rs = pstmt.executeQuery(sqlTAdetails);
//			int i = 0;
//			String TA = new String[numberOfTA];
//			while(rs.next()){
//				TA[i] = rs.getString("st_name");
//				i++;
//			}
//
//		}
//		catch(Throwable oops){
//			oops.printStackTrace();
//		}
//		try{
//			String sqlCourseDetails = "select c_id, c_name, c_start_date, c_end_date from Courses where c_id = ?;";
//			pstmt = conn.prepareStatement(sqlCourseDetails);
//			pstmt.clearParameters();
//			pstmt.setInt(1, course_id);
//			while(rs.next()){
//				courseName = rs.getString("c_name");
//				courseStartDate = rs.getString("c_start_date");
//				courseEndDate = rs.getString("c_end_date");
//			}
//		}
//		catch(Throwable oops){
//			oops.printStackTrace();
//		}
//		return new Course(courseId, courseName, courseStartDate, courseEndDate, TA);
//		//Course course = new Course("CSC-505", "ALGO", "08/15/2017", "07/12/2017", new String[]{"Udit Deshmukh", "Akanksha Shukla"});
		return null;
	}
	
	public List<Topic> getCourseTopics(String courseId){
		// Returns an array list of topics in the course.  
		
		return null;
	}
	
	public boolean addTopicToCourse(Topic topic, String courseId){
		// Returns true if the topic was successfully  added to the course.
		
		return true;
	}
	
	public boolean addNewCourse(Course course){
		// Returns true if the course was successfully added.
		
		return false;
	}
	
	public boolean addNewStudentToCourse(String studentId, String courseId){
		// Returns true if the student was successfully added to the course.
		
		return false;
	}
	
	public boolean dropStudentFromCourse(String studentId, String courseId){
		// Returns true if the student was successfully dropped from the course.
		
		return true;
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
//		String sql = 'select ex_id, ex_name, ex_mode, ex_start_date, ex_end_date, num_questions,
//		num_retires, policy, pt_correct, pt_incorrect from Exercises E, Topics T where E.tp_id = T.tp_id
//		and T.c_id = ?';
//		PreparedStatement ps = conn.prepareStatement(sql);
//		ps.setInt(1, Integer.parseInt(courseId));


		
		return null;
	}
	
	public boolean assignTAToCourse(int TAId, String courseId){
		// Returns true if the TA was successfully assigned to the course.
		
		return true;
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
	
	public HashSet<String> getQIdsInExercise(int exerciseId){
		
		return new HashSet<>();
	}
	
	public boolean addQuestionToExercise(int qId, int eId){
		// Returns true if the question was successfully added to the exercise.
		
		return true;
	}
	
	public boolean removeQuestionFromExercise(int qId, int eId){
		// Returns true if the question was successfully removed from the exercise.
		
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
}