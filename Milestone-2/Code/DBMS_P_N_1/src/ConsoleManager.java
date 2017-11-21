import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConsoleManager {
	
	private static ConsoleManager consoleManager;
	
	private Scanner sc;
	
	private static DBHandler dbHandler;
	final String DATE_FORMAT = "MM/dd/yyyy";
	
	private ConsoleManager(){
		// Make it singleton
		sc = new Scanner(System.in);
		dbHandler = DBHandler.getDBHandler();
	}
	
	public static ConsoleManager getConsoleManager(){
		if(consoleManager == null) consoleManager = new ConsoleManager();
		if(dbHandler == null) dbHandler = DBHandler.getDBHandler();
		return consoleManager;
	}
	
	public void showStartConsole(){
		clearScreen();
		System.out.println("1. Login");
		System.out.println("2. Exit");
	}
	
	
	// Home screen for professor
	public void showProfessorHomeScreen(){
		clearScreen();
		System.out.println("1. View Profile");
		System.out.println("2. View/Add Courses");
		System.out.println("3. Enroll/Drop A Student");
		System.out.println("4. Search/Add questions to Question Bank");
		System.out.println("5. Logout");
	}
	
	public void showTAHomeScreen(){
		clearScreen();
		System.out.println("1. View Profile");
		System.out.println("2. View Courses");
		System.out.println("3. Enroll/Drop A Student");
		System.out.println("4. Switch to student account.");
		System.out.println("5. Logout");
	}
	
	public void showStudentHomeScreen(boolean isAlsoTA){
		clearScreen();
		showMessageToGoToPreviousMenu();
		System.out.println("1. View/Edit Profile.");
		System.out.println("2. View Courses.");
		if(isAlsoTA){
			System.out.println("3. Switch to TA account.");
			System.out.println("4. Logout.");
		}else{
			System.out.println("3. Logout.");
		}
	}
	
	private void showCommonProfileData(UserType userType){
		System.out.println("1. First Name: " + dbHandler.getLoggedInUserName());
		if(userType == UserType.Student){
			System.out.println("2. Student Id: " + dbHandler.getLoggedInUserId());
		}else{
			System.out.println("2. Employee Id: " + dbHandler.getLoggedInUserId());
		}
	}
	
	public void showProfProfile(){
		showCommonProfileData(UserType.Professor);
		
		List<String[]> taughtCourses = dbHandler.getTaughtCoursesByProfessor();
		consoleManager.showCourses(taughtCourses, "Courses taught by you:");
	}
	
	public void showTAProfile(){
		showCommonProfileData(UserType.TA);
		
		List<String[]> TACourses = dbHandler.getTACourses();
		showCourses(TACourses, "Courses of which you are TA:");
	}
	
	public void showStudentProfile(){
		showCommonProfileData(UserType.Student);
		
		List<String[]> studentCourses = dbHandler.getStudentEnrolledCourses();
		showCourses(studentCourses, "Courses in which you are enrolled: ");
	}
	
	public void clearScreen(){
		// May implement later if needed. This should only clear the console.
	}
	
	public int askForIntInput(String message){
		return askForIntInputBetweenRange(message, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public int askForIntInputBetweenRange(String message, int min, int max){
		int choice;
		
		while(true){
			if (message != null){
				System.out.println(message);
			}else if(dbHandler.isUserLoggedIn()){
				System.out.println("Please enter your choice or press 0 to cancel: ");
			}
			
			try{
				choice = Integer.parseInt(sc.nextLine());
				if(choice == 0) return 0;
				else if(choice >= min && choice <= max) return choice;
				else showInvalidChoiceError();
			}catch(NumberFormatException e){
				System.out.println("Invalid input! Please enter a number.");
			}
		}
	}
	
	public String askForStringInput(String message){
		String choice;
		if (message != null){
			System.out.println(message);
		}else if(dbHandler.isUserLoggedIn()){
			System.out.println("Please enter your choice or press 0 to cancel: ");
		}
		
		choice = sc.nextLine();
		
		return choice;
	}
	
	@Override
	protected void finalize() throws Throwable {
	    try {
	        sc.close();
	    } finally {
	        super.finalize();
	    }
	}
	
	public void showInvalidChoiceError(){
		showInvalidChoiceError("Please select a valid option!");
	}
	
	public void showInvalidChoiceError(String message){
		System.out.println(message);
	}
	
	public void showCourses(List<String[]> courses, String message){
		clearScreen();
		System.out.println("> " + message);
		int i = 1;
		for(String[] course : courses){
			System.out.println("\t" + i++ + ". " + course[0] + " : " + course[1]);
		}
	}
	
	public int askForUserChoiceAfterShowingCourses(boolean isProfessor){
		System.out.println("\n0. Go back.");
		System.out.println("1. View course details.");
		if (isProfessor){
			System.out.println("2. Add new course.");
		}
		return askForIntInput("Please enter your choice: ");
	}
	
	public void showCourseDetails(Course course){
		System.out.println("> Instructor Name: " + course.getInstructorName());
		System.out.println("> Course Name: " + course.getCourseName());
		System.out.println("> Course Start Date: " + course.getStartDate());
		System.out.println("> Course End Date: " + course.getEndDate());
		System.out.println("> Course Level: " + course.getCourseLevel());
		System.out.println("> Max students allowed: " + course.getMaxStudentsAllowed());
		System.out.println("> Total students enrolled: " + course.getEnrolledStudents().size());
		System.out.print("> Current TA(s): ");
		if(course.hasTAs()){
			for(Person TA : course.getTAs()){
				System.out.print("\n\t> " + TA.getName() + "; ID = " + TA.getId());
			}
		}else{
			System.out.print("None");
		}
		System.out.println();
		if(course.hasTopics()){
			List<Topic> topics = course.getTopics();
			showCourseTopics(topics);
		}else{
			System.out.println("> Topics: None");
		}
		System.out.println("Students enrolled: ");
		int i = 1;
		for(Person student :  course.getEnrolledStudents()){
			System.out.println("\n\t> " + i++ + ". " + student.getName() + "; ID: " + student.getId());
		}
	}
	
	public void showCourseDetails(String courseId){
		Course course = dbHandler.getCourseInfo(courseId);
		showCourseDetails(course);
	}
		
	public int askForUserChoiceAfterShowingCourseDetails(boolean isProf){
		System.out.println("\n0. Go back.");
		System.out.println("1. View Exercises.");
		short itemNum = 2;
		if(isProf){
			System.out.println(itemNum++ + ". Add Exercises.");
			System.out.println(itemNum++ + ". Add TA.");
		}
		System.out.println(itemNum++ + ". Enroll a student.");
		System.out.println(itemNum++ + ". Drop a student.");
		System.out.println(itemNum++ + ". View report.");
		System.out.println(itemNum++ + ". Add topic.");
		
		return askForIntInput("Please enter your choice: ");
	}
	
	
	private void showMessageToGoToPreviousMenu(){
		System.out.println("Enter 0 to go back to previous menu.");
	}
	
	private boolean isDateValid(String date){		
		try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
	}
	
	public java.sql.Date getDate(String date){
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        df.setLenient(false);
        try {
			Date parsed = df.parse(date);
			return (new java.sql.Date(parsed.getTime()));			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Course askNewCourseDetails(){
		showMessageToGoToPreviousMenu();
		// Ask course ID.
		String courseId;
		while(true){
			courseId =	 	askForStringInput("1. Enter Course Id: ");
			if (courseId.equals("0")){
				return null;
			}else if(dbHandler.isNewCourseIdValid(courseId)) break;
			else{
				showInvalidChoiceError("A course with this ID already exists! Please try again.");
			}
		}
		// Ask course name.
		String courseName = askForStringInput("2. Enter Course Name: ");
		if (courseName.equals("0")){
			return null;
		}
		
		// Ask start date.
		String startDateStr;
		java.sql.Date startDate;
		while(true){
			startDateStr =  askForStringInput("3. Enter start date (mm/dd/yyyy): ");
			if (startDateStr.equals("0")){
				return null;
			}else if(isDateValid(startDateStr)){
				startDate=getDate(startDateStr);
				break;
			}
			else{
				showInvalidChoiceError("Please enter a valid date!");
			}
		}
		
		// Ask end date.
		String endDateStr;
		java.sql.Date endDate;
		while(true){
			endDateStr =  askForStringInput("3. Enter end date (mm/dd/yyyy): ");
			if (endDateStr.equals("0")){
				return null;
			}else if(isDateValid(endDateStr)){
				endDate=getDate(endDateStr);
				break;
			}
			else{
				showInvalidChoiceError("Please enter a valid date!");
			}
		}	
		
		// Ask course level.
		CourseLevel courseLevel;
		System.out.println("Select course level:");
		System.out.println("0. Cancel and go back.");
		System.out.println("1. Graduate.");
		System.out.println("2. Under Graduate.");
		
		int courseLevelChoice = askForIntInputBetweenRange("Please enter your choice: ", 0, 2);
		if(courseLevelChoice == 0){
			return null;
		}else if(courseLevelChoice == 1){
			courseLevel = CourseLevel.Grad;
		}else{
			courseLevel = CourseLevel.UnderGrad;
		}
		
		// Ask max students allowed.
		int maxStudentsAllowed = askForIntInputBetweenRange("Please enter the max number of students allowed to take course: ", 0, Integer.MAX_VALUE);
		if(maxStudentsAllowed == 0) return null;
		
		// Ask the TAs.
		List<Person> TAs = null;
		String choice;
		while(true){
			choice = askForStringInput("Do you want to assign TA(s) right now (y/n)?");
			if(choice.toLowerCase().equals("y")){
				String TAId;
				int subChoice;
				while(true){
					System.out.println("0. Cancel and Go back.");
					System.out.println("1. Enter TA ID.");
					System.out.println("2. Go back and assign TA(s).");
					subChoice = askForIntInputBetweenRange("Please enter your choice: ", 0, 2);
					if(subChoice == 0){
						TAs = null;
						break;
					}else if(subChoice == 1){
						while(true){
							TAId = askForStringInput("Please enter the Student ID of TA or press 0 to cancel: ");
							if(TAId.equals("0")){
								TAs = null;
								break;
							}else if(dbHandler.isTAIdValidForCourse(TAId, courseId) == 2){
								// Valid
								if(TAs == null) TAs = new ArrayList<>();
								// Check if professor already mentioned him.
								boolean alreadyMentioned = false;
								for(Person TA : TAs){
									if(TA.getId().equals(TAId)){
										alreadyMentioned = true;
									}
								}
								if(!alreadyMentioned)
									TAs.add(new Person(TAId));
								else
									showInvalidChoiceError("You have already mentioned him to be a TA for the class!");
								break;
							}else{
								showInvalidChoiceError("Please enter a valid TA ID!");
							}
						}
					}else if(subChoice == 2){
						break;
					}
				}
			}else if(choice.equals("n")){
				break;
			}else{
				showInvalidChoiceError("Please enter either y or n!");
			}
		}
		
		// Ask topics.
		List<Topic> topics = null;
		while(true){
			choice = askForStringInput("Do you want to add topics right now (y/n)?");
			if(choice.toLowerCase().equals("y")){
				int subChoice;
				while(true){
					System.out.println("0. Cancel and Go back.");
					System.out.println("1. Enter topic ID.");
					System.out.println("2. Go back and add topic(s).");
					subChoice = askForIntInputBetweenRange("Please enter your choice: ", 0, 2);
					if(subChoice == 0){
						topics = null;
						break;
					}else if(subChoice == 1){
						int newTopicId = askForIntInput("Please enter topic ID or press 0 to cancel: ");
						if(newTopicId != 0){
							if(topics == null) topics = new ArrayList<>();
							topics.add(new Topic(newTopicId, null));
						}
					}else if(subChoice == 2){
						break;
					}
				}
			}else if(choice.equals("n")){
				break;
			}else{
				showInvalidChoiceError("Please enter either y or n!");
			}
			
		}

		// Ask student IDs too!
		List<Person> students = null;
		while(students == null || (students != null && students.size() < maxStudentsAllowed)){
			choice = askForStringInput("Do you want to enroll students right now (y/n)?");
			if(choice.toLowerCase().equals("y")){
				String studentId;
				int subChoice;
				while(true){
					System.out.println("0. Cancel and Go back.");
					System.out.println("1. Enter Student ID.");
					System.out.println("2. Go back and assign student(s).");
					subChoice = askForIntInputBetweenRange("Please enter your choice: ", 0, 2);
					if(subChoice == 0){
						students = null;
						break;
					}else if(subChoice == 1){
						studentId = askForStringInput("Please enter the Student ID of TA or press 0 to cancel: ");
						if(studentId.equals("0")){
							continue;
						}else{
							// Check he was not mentioned a TA.
							if(TAs != null){
								boolean wasMentionedAsTA = false;
								for(Person TA : TAs){
									if(TA.getId().equals(studentId)){
										wasMentionedAsTA = true;
										break;
									}
								}
								if(wasMentionedAsTA){
									showInvalidChoiceError("Student already mentioned to be assigned as TA for the course. Cannot add him!");									
								}else{
									if(students == null) students = new ArrayList<>();
									students.add(new Person(studentId));
								}
							}else{
								if(students == null) students = new ArrayList<>();
								students.add(new Person(studentId));
							}
						}
					}else if(subChoice == 2){
						break;
					}
				}
			}else if(choice.equals("n")){
				break;
			}else{
				showInvalidChoiceError("Please enter either y or n!");
			}
		}
		
		return new Course(courseId, courseName, startDate, endDate, TAs, topics, 
				students, courseLevel, maxStudentsAllowed, null);
	}	
	
		
	public void showMessageAndWaitForUserToGoBack(String message){
		if(message!=null){
			System.out.println(message);
		}
		
		while(askForIntInput("Please enter 0 to go back.") != 0){
			// Pass
		}
	}
	
	public String[] askForNewStudentDetails(String courseId){
		showMessageToGoToPreviousMenu();
		String studentId = askForStringInput("1. Enter Student Id: ");
		if(studentId.equals("0")) return null;
		else if(dbHandler.isStudentIdValid(studentId)){
			if(courseId == null){
				courseId = askForStringInput("2. Enter Course Id: ");
				if(courseId.equals("0")) return null;
				else if(dbHandler.isCourseIdValid(courseId)){
					return new String[]{studentId, courseId};
				}else{
					showInvalidChoiceError("Please enter a valid course ID!");
					return askForNewStudentDetails(courseId);
				}
			}else{
				return new String[]{studentId, courseId};
			}
		}else{
			showInvalidChoiceError("Please enter a valid student ID!");
			return askForNewStudentDetails(courseId);
		}
	}
	
	public void showReportForCourse(List<StudentReport> studentReports, String courseId){
		System.out.println("Report for course: " + courseId);
		for(StudentReport studentReport : studentReports){
			System.out.println("> Name: " + studentReport.getName());
			List<Integer> scoreAccToPolicy = studentReport.getScoresPerPolicy();
			if(studentReport.getScoresPerHW() != null){
				System.out.println("> Scores for each exercise: ");
				int j = 0;
				for(Integer[] scorePerHW : studentReport.getScoresPerHW()){
					System.out.print("\t Exercise #" + scorePerHW[0] + ": ");
					for(int i=1;i<scorePerHW.length;i++){
						System.out.print(", " + scorePerHW[i]);
					}
					System.out.println("\nFor Exercise #" + scorePerHW[0] + ", score according to the HW policy is: " + scoreAccToPolicy.get(j++));
				}
			}else{
				System.out.println("> The student has not yet attempted any HW.");
			}			
		}
		showMessageAndWaitForUserToGoBack("Please enter 0 to go back.");
	}
	
	public void showExercisesDetailsForCourse(String courseId, List<Exercise> exercisesInThisCourse){
		if(exercisesInThisCourse == null){
			showMessageAndWaitForUserToGoBack("There are no exercised added currently.");
		}else{
			System.out.println("> Details of Exercises in the course: " + courseId);
			int exerciseNum = 1;
			for(Exercise exercise : exercisesInThisCourse){
				System.out.println("--------------------------EXERCISE #" + exerciseNum + " DETAILS BEGIN--------------------------");
				showExerciseDetails(exercise);
				System.out.println("--------------------------EXERCISE #" + exerciseNum++ + " DETAILS END----------------------------");
			}
		}
	}
	
	public int askToAddOrRemoveQuestionFromExercise(){
		int choice;
		while(true){
			System.out.println("0. Go back.");
			System.out.println("1. Add question to the exercise.");
			System.out.println("2. Remove question from the exercise.");
			choice = askForIntInput("Please enter your choice: ");
			if(choice == 0 || choice == 1 || choice == 2) break;
			showInvalidChoiceError();
		}
		return choice;
	}
	
	
	public String askTAId(String courseId){
		showMessageToGoToPreviousMenu();
		String TAId = askForStringInput("Please enter Student ID of TA: ");
		if (TAId.equals("0")){
			// Cancel operation. Go back.
			return null;
		}
		int isTAIdValid = dbHandler.isTAIdValidForCourse(TAId, courseId);
		if(isTAIdValid == -1){
			showInvalidChoiceError("Please enter a valid TA ID!");
			return askTAId(courseId);
		}else{
			return TAId;
		}
	}
	
	public void showQuestions(List<Question> questions, String message){
		int questionNum = 1;
		if(message != null){
			System.out.println(message);
		}
		if(questions == null || questions.size() == 0){
			System.out.println("\t> None!");
		}else{
			for(Question question : questions){
				System.out.println("------------------ QUESTION #" + questionNum + " BEGINS -----------------");
				System.out.println("> ID: " + question.getId());
				System.out.println("> Text: " + question.getText());
				if(question.getQuestionType() != null){
					System.out.println("> Type: " + question.getQuestionType());
				}
				System.out.println("> Difficulty level: " + question.getDifficultyLevel());
				System.out.println("> Topic ID: " + question.getTopicId());
				System.out.print("> Hint: " );
				if(question.hasHint()){
					System.out.println(question.getHint() + "");
				}else{
					System.out.println("None");
				}
				// Can display detailed solution too here. StudentHandler does not uses this function.
				System.out.println("> Detailed Solution" + question.getDetailedSolution() + "\n");
				System.out.println("------------------ QUESTION " + questionNum++ + " ENDS -------------------");
			}
		}
	}
	
	public int askQuestionSearchType(){
		System.out.println("0. Go back.");
		System.out.println("1. Search by Question ID.");
		System.out.println("2. Search by Topic ID.");
		
		return askForIntInputBetweenRange("Please enter your choice: ", 0, 2);
	}
	
	public void showOptionsAtViewQB(){
		System.out.println("\n\n");
		System.out.println("0. Go back.");
		System.out.println("1. Search by Question Id or Topic.");
		System.out.println("2. Add questions to the Question Bank.");
	}
	
	public Question askNewQuestionAndItsAnswerDetails(){
		showMessageToGoToPreviousMenu();
		int id;
		while(true){
			id = askForIntInput("Please enter id for the question: ");
			if(id == 0) return null;
			else if(dbHandler.isNewQuestionIdValid(id)){
				break;
			}
		}
		String type;
		QuestionType questionType = null;
		while(true){
			type = askForStringInput("Please enter question type (Fix or Param or 0 to cancel): ");
			if(type.equals("0")) return null;
			else if(type.toLowerCase().equals("param")){
				System.out.println("NOTE: Please enter ___ as placeholder for question parameters.");
				System.out.println("NOTE: Please enter ### as the placeholder for answer.");
				questionType = QuestionType.Parameterized;
				break;
			}else if(type.toLowerCase().equals("fix")){
				questionType = QuestionType.Fixed;
				break;
			}else{
				showInvalidChoiceError();
			}
		}
		String text = null;
		while(true){
			text = askForStringInput("Please enter question text: ");
			if(text.equals("0")) return null;
			else if(text.length() == 0){
				showInvalidChoiceError("Question text cannot be empty!");
			}else{
				break;
			}
		}
		String[][] parameterValues = null;
		int totalValuesForOneParameter = 1;
		if(type.toLowerCase().equals("param")){
			int totalQuestionPlaceholders = getTotalParametersFromQuestionText(text, "___");
			if(totalQuestionPlaceholders == 0){
				System.out.println("No placeholder for parameters found! Converting to Fixed type.");
				questionType = QuestionType.Fixed;
			}else{
				totalValuesForOneParameter = askForIntInput("How many values will you input for any parameter?");
				parameterValues = new String[totalQuestionPlaceholders][totalValuesForOneParameter];
				for(int param = 1 ; param <= totalQuestionPlaceholders ; ++param){
					for(int i = 1 ; i <= totalValuesForOneParameter ; ++i){
						parameterValues[param-1][i-1] = askForStringInput("Please enter " + i + " value for parameter " + param + ": ");
					}
				}
			}
		}
		
		int difficultyLevel = askForIntInput("Please enter difficulty level: ");
		if(difficultyLevel == 0) return null;
		
		int totalCorrectAnswers = 0;
		while(totalCorrectAnswers <= 0){
			totalCorrectAnswers = askForIntInput("How many correct answers will you enter?");
			if(totalCorrectAnswers <= 0){
				System.out.println("At least 1 correct answer is required!");
			}
		}

		String[] incorrectAnswers = null;
		String[][] correctAnswers = new String[totalValuesForOneParameter][totalCorrectAnswers];
		// Get the correct answer(s)
		for(int ansNum = 0 ; ansNum < totalCorrectAnswers ; ++ansNum){
			if(questionType == QuestionType.Fixed){
				correctAnswers[0][ansNum] = askForStringInput("Please enter the correct answer: ");
			}else{
				int totalAnswerPlaceHolders = getTotalParametersFromQuestionText(text, "###");
				for(int ans = 0 ; ans < totalValuesForOneParameter ; ++ans){
					StringBuilder answer = new StringBuilder();
					for(int placeHolder = 0 ; placeHolder < totalAnswerPlaceHolders ; ++placeHolder){
						answer.append(askForStringInput("Enter the value of " + (placeHolder + 1) + " place holder in the correct answer for combination " + (ans + 1) + ": "));
						if(placeHolder != totalAnswerPlaceHolders - 1)
							answer.append("; ");
					}
					correctAnswers[ans][ansNum] = answer.toString();
				}
			}
		}
		
		// Get the incorrect answer(s)
		int totalIncorrectAnswers = 0;
		if(!type.toLowerCase().equals("param")) {
		while(totalIncorrectAnswers <= 0){
			totalIncorrectAnswers = askForIntInput("How many incorrect answer options will you enter?");
			if(totalIncorrectAnswers <= 0){
				System.out.println("At least 1 incorrect answer is required!");
			}
		}
		incorrectAnswers = new String[totalIncorrectAnswers];
		System.out.println("Note: None of the incorrect answer can be same as any correct answer.");
		for(int incAns = 0 ; incAns < totalIncorrectAnswers ; ++incAns){
			incorrectAnswers[incAns] = askForStringInput("Please enter incorrect answer #" + (incAns+1) + ": ");
		}
		}
		//for parameterized
		else {
			int totalIncorrectAnswers1 = 0;
			if(type.toLowerCase().equals("param")) {
			while(totalIncorrectAnswers1 <= 0){
				totalIncorrectAnswers1 = askForIntInput("How many incorrect answer options will you enter?");
				if(totalIncorrectAnswers1 <= 0){
					System.out.println("At least 1 incorrect answer is required!");
				}
			}
			incorrectAnswers = new String[totalIncorrectAnswers1*totalValuesForOneParameter];
			System.out.println("Note: None of the incorrect answer can be same as any correct answer.");
			for(int parno=0;parno<totalValuesForOneParameter;parno++)
				for(int incAns = 0 ; incAns < totalIncorrectAnswers1 ; ++incAns){
				incorrectAnswers[parno*totalIncorrectAnswers1+incAns] = askForStringInput("Please enter incorrect answer #" + (incAns+1) + " for combination "+(parno+1)+": ");
			}
			}
		}	//end parameter
		String hintChoice, hint = null;
		while(true){
			hintChoice = askForStringInput("Do you want to give a hint for this question (y or n or 0 to cancel)?");
			if(hintChoice.equals("0")){
				return null;
			}else if(hintChoice.equals("y")){
				hint = askForStringInput("Please enter the optional hint for the solution: ");
				break;
			}else if(hintChoice.equals("n")){
				break;
			}else{
				showInvalidChoiceError();
			}
		}
		
		String detailedSolution;
		while(true){
			detailedSolution = askForStringInput("Please enter the detailed solution: ");
			if(detailedSolution.equals("0")) return null;
			else if(detailedSolution.length() == 0){
				showInvalidChoiceError("Detailed solution cannot be empty!");
			}else{
				break;
			}
		}
		
		int topicId = askForIntInput("Please enter the topic Id associated with this question: ");
		if(topicId == 0) return null;
			
		return new Question(text, hint, null, difficultyLevel, topicId, id, questionType, detailedSolution, parameterValues, correctAnswers, incorrectAnswers);
	}
	
	private int getTotalParametersFromQuestionText(String qText, String placeholder){
		Pattern pattern = Pattern.compile(placeholder);
		Matcher matcher = pattern.matcher(qText);
		int count = 0;
		while(matcher.find()) count++;
		return count;
	}
	

	public void showCourseTopics(List<Topic> topics){
		System.out.println("> Topics in this course: ");
		for(int topic = 1; topic <= topics.size() ; ++topic){
			System.out.println("Topic #" + (topic));
			System.out.println("\t> ID: " + topics.get(topic-1).getTopicId());
			System.out.println("\t> Name: " + topics.get(topic-1).getTopicName());
		}
	}
	
	public Exercise askDetailsAboutNewExercise(ExerciseMode exerciseMode, String courseId){
		// Ask the details of new exercise.
		
		// Ask the id of the associated topic.
		// First show the topics in the course.
		List<Topic> topics = dbHandler.getCourseTopics(courseId);
		if(topics == null){
			// No topic in the course!
			showMessageAndWaitForUserToGoBack("No topics present in the course! Please add a topic first.");
			return null;
		}
		showCourseTopics(topics);
		int topicId;
		while(true){
			topicId = askForIntInput("Please enter Id of the associated topic: ");
			if(topicId == 0) return null;
			else if(dbHandler.isTopicIdValid(topicId)){
				break;
			}else{
				showInvalidChoiceError("Please enter a valid topic ID.");
			}
		}
		
		// Ask the exercise Id.
		int exerciseId;
		while(true){
			exerciseId = askForIntInput("Please enter Id for exercise: ");
			if(exerciseId == 0) return null;
			else if(dbHandler.isExerciseIdValid(exerciseId)){
				break;
			}else{
				showInvalidChoiceError("An exercise with this ID already exists. Please enter a new exercise ID!");
			}
		}	
		
		// Ask exercise name.
		String name = askForStringInput("Please enter exercise name: ");
		if(name.equals("0")) return null;
		
		// Ask start date.
		String startDate;
		while(true){
			startDate =  askForStringInput("Please enter exercise start date (" + DATE_FORMAT + "): ");
			if (startDate.equals("0")){
				return null;
			}else if(isDateValid(startDate)){
				break;
			}
			else{
				showInvalidChoiceError("Please enter a valid date!");
			}
		}
		
		// Ask end date.
		String endDate;
		while(true){
			endDate =  askForStringInput("Please enter exercise end date (" + DATE_FORMAT + "): ");
			if (endDate.equals("0")){
				return null;
			}else if(isDateValid(endDate)){
				break;
			}
			else{
				showInvalidChoiceError("Please enter a valid date!");
			}
		}
		
		// Ask scoring policy
		System.out.println("Please select the scoring policy: ");
		System.out.println("0. Cancel operation");
		System.out.println("1. Average");
		System.out.println("2. Latest");
		System.out.println("3. Maximum");
		int sP = askForIntInputBetweenRange("Please enter your choice: ", 0, 3);
		
		ScroingPolicy scroingPolicy;
		
		if(sP == 0) return null;
		else if(sP == 1) scroingPolicy = ScroingPolicy.Average;
		else if(sP == 2) scroingPolicy = ScroingPolicy.Latest;
		else scroingPolicy = ScroingPolicy.Maximum;
		
		// Ask number of retries allowed.
		int numRetries = askForIntInputBetweenRange("Please enter number of allowed retires (Enter -1 for unlimited retries.): ", -1, Integer.MAX_VALUE);
		
		// Ask the number of questions in the exercise.
		int numQuestions = askForIntInput("Please enter the number of questions in the exercise: "); 
		while(numQuestions < 1){
			System.out.println("At least 1 question must be present in the exercise!");
			numQuestions = askForIntInput("Please enter the number of questions in the exercise: ");
		}
		
		// Ask about the points per correct answer:
		int pointsPerCorrectAnswer = askForIntInput("Please enter the points awarded for each correct answer: ");
		while(pointsPerCorrectAnswer < 1){
			System.out.println("Points awarded for any correct answer should be at least 1!");
			pointsPerCorrectAnswer = askForIntInput("Please enter the points awarded for each correct answer: ");
		}
		
		// Ask about the points per incorrect answer:
		int pointsPerIncorrectAnswer = askForIntInput("Please enter the points deducted for each incorrect answer: ");
		while(pointsPerIncorrectAnswer < 0){
			System.out.println("Points deducted for any incorrect answer cannot be negative!");
			pointsPerIncorrectAnswer = askForIntInput("Please enter the points deducted for each incorrect answer: ");
		}
		
		HashSet<Integer> qIds = null;
		if(exerciseMode == ExerciseMode.Random){
			// Ask whether he wants to add questions to the exercise right now.
			String decision = askForStringInput("Do you want to add questions right now? (y/n): ");
			if(decision.equals("y")){
				// Get the questions from the professor.
				// Get the valid questions for course from DB:
				qIds = new HashSet<>();
				List<Question> courseQuestions = dbHandler.getQuestionsForCourseAndTopic(courseId,topicId);
				// Show the questions and wait for user to select one:
				int choice = -1;
				while (choice != 0 && qIds.size() != numQuestions){
					showQuestions(courseQuestions, "Questions in the course: ");
					System.out.println("0. Cancel and continue.");
					System.out.println("1. Enter ID of a question to be added to the exercise.");
					System.out.println("2. Enter ID of a question to be removed from the exercise.");
					choice = askForIntInputBetweenRange("Please enter your choice: ", 0, 2);
					switch (choice) {
					case 0:
						break;
					case 1:
						int newQId = askNewQuestionToBeAddedExercise(qIds);
						if(newQId != 0){
							qIds.add(newQId);
						}
						break;
					case 2:
						int qId = askQuestionToBeRemovedFromExercise(qIds);
						if(qId != 0){
							qIds.remove(qId);
						}
						break;
					default:
						break;
					}
				}
			}
		}
				
		// Professor successfully entered the information about exercise.
		return new Exercise(exerciseMode, scroingPolicy, name, startDate, endDate, 
				numQuestions, numRetries, exerciseId, qIds, pointsPerCorrectAnswer, 
				pointsPerIncorrectAnswer, topicId);
	}
	
	public int askNewQuestionToBeAddedExercise(HashSet<Integer> qIds){
		int selectedQuestionId;
		while(true){
			selectedQuestionId = askForIntInput("Enter the ID of the question to be added or press 0 to cancel: ");
			if(selectedQuestionId == 0){
				// Cancel
				return 0;
			}else{
				if(qIds.contains(selectedQuestionId)){
					// This question was already added by the professor.
					System.out.println("This question was already added by you! Please try again.");
				}else{
					return selectedQuestionId;
				}
			}
		}
	}
	
	public int askQuestionToBeRemovedFromExercise(HashSet<Integer> qIds){
		int selectedQuestionId;
		while(true){
			selectedQuestionId = askForIntInput("Enter the ID of the question to be removed or press 0 to cancel: ");
			if(selectedQuestionId == 0){
				// Cancel
				return 0;
			}else{
				if(!qIds.contains(selectedQuestionId)){
					// This question was not added by the professor beforehand.
					System.out.println("This question has not added by you! Please try again.");
				}else{
					return selectedQuestionId;
				}
			}
		}
	}

	public int askForChoiceAtStudentEnrolledCourses(){
		int choice;
		while(true){
			System.out.println("0. Go back");
			System.out.println("1. Enter ID of a course to view details.");
			
			choice = askForIntInput("Please enter your choice: ");
			if(choice == 0) return 0;
			else if(choice == 1) return 1;
			else showInvalidChoiceError();
		}		
	}
	
	public int asForChoiceAtStudentViewSelectedCourse(){
		while(true){
			System.out.println("0. Go back.");
			System.out.println("1. Attempt/View current open HWs.");
			System.out.println("2. Past HW submissions.");
			
			int choice = askForIntInput("Please enter your choice: ");
			if(choice >= 0 && choice <= 2) return choice;
			else showInvalidChoiceError();
		}
	}
	
	public int showExerciseListToStudentAndAskChoice(List<String []> exercises){
		for(int i = 1 ; i <= exercises.size() ; ++i){
			System.out.println(i + ". HW #" + exercises.get(i-1)[0] + " Deadline: " + exercises.get(i-1)[1]);
		}
		int exerciseId;
		int choice;
		while(true){
			System.out.println("0. Go back.");
			System.out.println("1. Enter exercise ID.");
			choice = askForIntInput("Please enter your choice: ");
			if(choice == 0){
				return 0;
			}else if(choice == 1){
				while(true){
					exerciseId = askForIntInput("Please enter the ID of exercise you'd like to see or 0 to cancel: ");
					if(exerciseId == 0) break;
					else {
						for(String[] detail : exercises){
							if(detail[0].equals(""+exerciseId)){
								// Valid choice
								return exerciseId;
							}
						}
						// Invalid choice!
						showInvalidChoiceError("Please enter a valid exercise ID!");
					}
				}
			}
			else showInvalidChoiceError();
		}
	}

	public int showAttemptedHWsOverviewtAndAskChoice(List<StudentHWAttempt> attempts){
		System.out.println("0. Go back.");
		for(int i = 0 ; i < attempts.size() ; ++i){
			StudentHWAttempt attempt = attempts.get(i);
			System.out.println((i+1) + ". Attempt for HW #" + attempt.getExerciseId() + "; Score: " + attempt.getScore() + "/" + attempt.getMaxScore() + "; Attempt Date: " + attempt.getSubmissionDateTime());
		}
		
		return askForIntInputBetweenRange("Please enter your choice ", 0, attempts.size());
	}
	
	
	public void showStudentAttemptDetails(StudentHWAttempt attempt){
		System.out.println("--------- Details of attempt for exercise " + attempt.getExerciseId() + " ------------");
		
		System.out.printf("> Score: %.2f/%.2f\n", attempt.getScore(), attempt.getMaxScore());
		System.out.println("> Attempt Date/Time: " + attempt.getSubmissionDateTime());
		System.out.println("> Points awarded per correct answer: " + attempt.getPointsPerCorrectAnswer());
		System.out.println("> Points deducted per incorrect answer: " + attempt.getPointsPerIncorrectAnswer());
		
		List<Question> questions = attempt.getQuestions();
		List<Boolean> wasCorrectlyAttempted = attempt.getWasCorrectlyAnswered();
		boolean showHint = true;
		Question question;
		for(int i = 0 ; i < questions.size() ; ++i){
			showHint = true;
			question = questions.get(i);
			System.out.println("> Q" + (i+1) + ". " + question.getText());
			if(wasCorrectlyAttempted.get(i) == null){
				System.out.println("\t> You did not attempt this question.");
			}else if(wasCorrectlyAttempted.get(i) == false){
				System.out.println("\t> You answered this question incorrectly.");
			}else{
				System.out.println("\t> You answered this question correctly.");
				showHint = false;
			}
			
			if(showHint && question.hasHint()){
				System.out.println("> Hint: " + question.getHint());
			}
			
			if(attempt.hasDeadlinePassed()){
				// Show detailed solution
				System.out.println("> Solution: " + question.getDetailedSolution());
			}
		}
		showMessageAndWaitForUserToGoBack("");
	}
	
	public void showStudentAttemptsOverview(List<StudentHWAttempt> studentHWAttempts, int exerciseId){
		System.out.println("Your attempt(s) for exercise " + exerciseId);
		
		for(int i = 1 ; i <= studentHWAttempts.size() ; ++i){
			StudentHWAttempt attempt = studentHWAttempts.get(i-1);
			System.out.println("Attempt #" + i);
			System.out.printf("> Score: %.2f/%.2f\n", attempt.getScore(), attempt.getMaxScore());
			System.out.println("> Attempt Date/Time: ");
		}
	}
	
	public int askWhichAttemptToView(int totalAttempts){
		return askForIntInputBetweenRange("Please enter the Attempt # to see its details or press 0 to cancel: ", 1, totalAttempts);
	}
	
	public int showQuestionToAttempt(Question question, int num){
		// Shows the question and its options.
		// Returns the number of options.
		System.out.println("Question #" + num);
		System.out.println(question.getText());
		
		List<String> options = question.getOptions();
		System.out.println("0. Cancel attempt and go back.");
		System.out.println("1. Skip the question.");
		for(int option = 0 ; option < options.size() ; ++option){
			System.out.println((option + 2) + ". " + options.get(option));
		}
		
		return options.size();
	}
	
	public void showExerciseDetailsToProfessor(Exercise exercise){
		System.out.println("------------- BEGIN DETAILS OF EXERCISE WITH ID " + exercise.getId() + " ------------");
		showExerciseDetails(exercise);
		System.out.println("------------- END DETAILS OF EXERCISE WITH ID " + exercise.getId() + " ------------");
	}
	
	private void showExerciseDetails(Exercise exercise){
		
		System.out.println("> Name: " + exercise.getName());
		System.out.println("> ID: " + exercise.getId());
		System.out.println("> Mode: " + exercise.getExerciseMode());
		System.out.println("> Start Date: " + exercise.getStartDate());
		System.out.println("> End Date: " + exercise.getEndDate());
		System.out.println("> Number of questions: " + exercise.getNumQuestions());
		System.out.println("> Number of retries: " + exercise.getNumRetries());
		System.out.println("> Scoring Policy: " + exercise.getScroingPolicy());
		
		List<Question> questionsInThisExercise = dbHandler.getQuestionsInExercise(exercise.getId());
		showQuestions(questionsInThisExercise, "Questions in this exercise: ");
		
	}
	
	
	public UserType askTAHowHeWantsToLogin(){
		System.out.println("0. Logout.");
		System.out.println("1. Continue as TA.");
		System.out.println("2. Continue as student.");
		
		int choice = askForIntInputBetweenRange("Please enter your choice: ", 0, 2);
		switch (choice) {
		case 0:
			return null;
		case 1:
			return UserType.TA;
		case 2:
			return UserType.Student;
		default:
			// Pass. This can never occur.
			return null;
		}
		
	}
	
	public void showExitMessage(){
		System.out.println("Thank you for using the project!");
	}
}
