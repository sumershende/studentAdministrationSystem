

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConsoleManager {
	
	private static ConsoleManager consoleManager;
	
	private Scanner sc;
	
	private DBHandler dbHandler;
	
	private ConsoleManager(){
		// Make it singleton
		sc = new Scanner(System.in);
		dbHandler = DBHandler.getDBHandler();
	}
	
	public static ConsoleManager getConsoleManager(){
		if(consoleManager == null) consoleManager = new ConsoleManager();
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
		System.out.println("2. View/Add Courses");
		System.out.println("3. Enroll/Drop A Student");
		System.out.println("4. Logout");
	}
	
	public void showProfOrTAProfile(String fName, String lName, String eId){
		clearScreen();
		System.out.println("1. First Name: " + fName);
		System.out.println("2. Last Name: " + lName);
		System.out.println("3. Employee Id: " + eId);
	}
		
	// Method stack for Student
	public void showStudentHomeScreen(){
		clearScreen();
		showMessageToGoToPreviousMenu();
		System.out.println("1. View/Edit Profile");
		System.out.println("2. View Courses");
		System.out.println("3. Logout");
	}
	
	public void clearScreen(){
		// May implement later if needed. This should only clear the console.
	}
	
	public int askForIntInput(String message){
		int choice;
		
		if (message != null){
			System.out.println(message);
		}else if(dbHandler.isUserLoggedIn()){
			System.out.println("Please enter your choice or press 0 to cancel: ");
		}
		
		try{
			choice = Integer.parseInt(sc.nextLine());
		}catch(NumberFormatException e){
			System.out.println("Invalid input! Please enter a number.");
			return askForIntInput(message);
		}		
		
		return choice;
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
		System.out.println("Please select a valid option!");
	}
	
	public void showCourses(List<String[]> courses, String message, boolean isProfessor){
		clearScreen();
		System.out.println(message);
		for(String[] course : courses){
			System.out.println(course[0] + " : " + course[1]);
		}
		System.out.println("\n0. Go back.");
		System.out.println("1. View course details.");
		if (isProfessor){
			System.out.println("2. Add new course.");
		}
	}
	
	public void showCourseDetails(String courseId, boolean isProf){
		Course course = dbHandler.getCourseInfo(courseId);
		System.out.println("\n0. Go back.");
		System.out.println("1. Course Name: " + course.getCourseName());
		System.out.println("2. Course Start Date: " + course.getStartDate());
		System.out.println("3. Course End Date: " + course.getEndDate());
		System.out.println("4. View Exercises.");
		System.out.println("5. Add Exercises.");
		System.out.println("6. Current TA(s): ");
		for(String TA : course.getTAs()){
			System.out.println("\t" + TA);
		}
		short itemNum = 7;
		if(isProf){
			System.out.println("7. Add TA.");
			itemNum++;
		}
		System.out.println(itemNum++ + ". Enroll a student.");
		System.out.println(itemNum++ + ". Drop a student.");
		System.out.println(itemNum++ + ". View report.");
	}
	
	private void showMessageToGoToPreviousMenu(){
		System.out.println("Enter 0 to go back to previous menu.");
	}
	
	public Course askNewCourseDetails(){
		showMessageToGoToPreviousMenu();
		String courseId =   askForStringInput("1. Enter Course Id: ");
		if (courseId.equals("0")){
			return null;
		}
		String courseName = askForStringInput("2. Enter Course Name: ");
		if (courseName.equals("0")){
			return null;
		}
		String startDate =  askForStringInput("3. Enter start date: ");
		if (startDate.equals("0")){
			return null;
		}
		String endDate =    askForStringInput("4. Enter end date: ");
		if (endDate.equals("0")){
			return null;
		}
		return new Course(courseId, courseName, startDate, endDate, null);
	}
	
	public void showMessageAndForUserToGoBack(String message){
		if(message!=null){
			System.out.println(message);
		}
		
		while(askForIntInput("Please enter 0 to go back.") != 0){
			
		}
	}
	
	public String[] askForNewStudentDetails(String courseId){
		showMessageToGoToPreviousMenu();
		String studentId = askForStringInput("1. Enter Student Id: ");
		if(studentId.equals("0")) return null;
		if(courseId == null){
			courseId = askForStringInput("2. Enter Course Id: ");
			if(courseId.equals("0")) return null;
		}
		return new String[]{studentId, courseId};
	}
	
	public void showStudentReport(StudentReport studentReport, String courseId){
		System.out.println("1. First Name: " + studentReport.getFirstName());
		System.out.println("2. Last Name: " + studentReport.getLastName());
		System.out.println("3. Scores for each exercise: ");
		for(String[] scorePerHW : studentReport.getScoresPerHW()){
			System.out.println("\t" + scorePerHW[0] + ": " + scorePerHW[1]);
		}
		int choice;
		while(true){
			choice = askForIntInput("Please enter 0 to go back.");
			if(choice == 0) break;
			showInvalidChoiceError();
		}
	}
	
	public void showExercisesForCourse(String courseId){
		System.out.println("Exercises in course: " + courseId);
		String[][] exercisesInThisCourse = dbHandler.getExercisesForCourse(courseId);
		for(String[] exerciseDetails : exercisesInThisCourse){
			System.out.println("\tName: " + exerciseDetails[0] + "\tID: " + exerciseDetails[1]);
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
	
	public String askForTAId(){
		showMessageToGoToPreviousMenu();
		String TAId = askForStringInput("Please enter Student ID of TA: ");
		if (TAId.equals("0")){
			// Cancel operation. Go back.
			return null;
		}else{
			return TAId;
		}
	}
	
	public void showQuestions(List<Question> questions, String message){
		int questionNum = 1;
		if(message != null){
			System.out.println("Questions for " + message);
		}
		for(Question question : questions){
			System.out.println("::Question " + questionNum++ + "::");
			System.out.println("\tID: " + question.getId());
			System.out.println("\t\tType: " + question.getQuestionType());
			System.out.println("\tText: " + question.getText());
			System.out.println("\tDifficulty level: " + question.getDifficultyLevel());
			System.out.println("\tTopic Details: ");
			System.out.println("\t\tTopic ID: " + question.getTopicId());
			System.out.println("\t\tTopic Name: " + question.getTopicName());
			System.out.print("\tHint: " );
			if(question.hasHint()){
				System.out.println(question.getHint());
			}else{
				System.out.println("None");
			}
		}
		// Can display detailed solution too here.
	}
	
	public void showOptionsAtViewQB(){
		System.out.println("\n\n");
		System.out.println("0. Go back.");
		System.out.println("1. Search by Question Id or Topic.");
		System.out.println("2. Add questions to the Question Bank.");
	}
	
	public Question askNewQuestionAndItsAnswerDetails(){
		showMessageToGoToPreviousMenu();
		String type = askForStringInput("Please enter question type: ");
		QuestionType questionType = null;
		if(type.equals("0")) return null;
		else if(type.equals("Parameterized")){
			System.out.println("NOTE: Please enter ___ as placeholder for question parameters.");
			System.out.println("NOTE: Please enter ### as the placeholder for answer.");
			questionType = QuestionType.Parameterized;
		}else{
			questionType = QuestionType.Fixed;
		}
		String text = askForStringInput("Please enter question text: ");
		if(text.equals("0")) return null;
		String[][] parameterValues = null;
		int totalValuesForOneParameter = 1;
		if(type.equals("Parameterized")){
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
		
		int totalCorrectAnswers = askForIntInput("How many correct answers will you enter?");
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
		int totalIncorrectAnswers = askForIntInput("How many incorrect answer options will you enter?");
		String[] incorrectAnswers = new String[totalIncorrectAnswers];
		System.out.println("Note: None of the incorrect answer can be same as any correct answer.");
		for(int incAns = 0 ; incAns < totalIncorrectAnswers ; ++incAns){
			incorrectAnswers[incAns] = askForStringInput("Please enter incorrect answer #" + (incAns+1) + ": ");
		}
		
		String hint = askForStringInput("Please enter the optional hint for the solution: ");
		String detailedSolution = askForStringInput("Please enter the detailed solution: ");
		int topicId = askForIntInput("Please enter the topic Id associated with this question: ");
		
		return new Question(text, hint, null, difficultyLevel, topicId, -1, questionType, detailedSolution, parameterValues, correctAnswers, incorrectAnswers);
	}
	
	private int getTotalParametersFromQuestionText(String qText, String placeholder){
		Pattern pattern = Pattern.compile(placeholder);
		Matcher matcher = pattern.matcher(qText);
		int count = 0;
		while(matcher.find()) count++;
		return count;
	}
}
