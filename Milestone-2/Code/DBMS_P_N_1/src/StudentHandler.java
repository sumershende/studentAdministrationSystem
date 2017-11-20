import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class StudentHandler {
	private static DBHandler dbHandler;
	private static ConsoleManager consoleManager;
	
	private StudentHandler(){

	}
	
	public static boolean execute(boolean isAlsoTA){
		dbHandler = DBHandler.getDBHandler();
		consoleManager = ConsoleManager.getConsoleManager();
		
		int choice;
		
		while(true){
			consoleManager.showStudentHomeScreen(isAlsoTA);
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			if(isAlsoTA){
				if (choice == 1){
					// View Profile
					viewProfile();
				}else if(choice == 2){
					// View courses of which the person is TA.
					displayStudentCourses();
				}else if(choice == 3){
					// Switch to TA account.
					return true;
				}else if(choice == 4){
					// Logout
					if(dbHandler.logout()) break;
					else consoleManager.showMessageAndWaitForUserToGoBack("Error logging out.");	
				}else{
					consoleManager.showInvalidChoiceError();
				}
			}else{
				if (choice == 1){
					// View Profile
					viewProfile();
				}else if(choice == 2){
					// View courses in which the student is enrolled.
					displayStudentCourses();
				}else if(choice == 3){
					// Logout
					if(dbHandler.logout()) break;
					else consoleManager.showMessageAndWaitForUserToGoBack("Error logging out.");	
				}else{
					consoleManager.showInvalidChoiceError();
				}
			}
		}
		return false;
	}
	
	private static void viewProfile(){
		consoleManager.showStudentProfile();
		consoleManager.showMessageAndWaitForUserToGoBack(null);
	}
	
	private static void displayStudentCourses(){
		List<String[]> enrolledCourses = dbHandler.getStudentEnrolledCourses();
		while(true){
			consoleManager.showCourses(enrolledCourses, "Courses you are enrolled in: ");
			
			int choice = consoleManager.askForChoiceAtStudentEnrolledCourses();
			if(choice == 0) return;
			else{
				// View details of a particular course
				String courseId = consoleManager.askForStringInput("Please enter the course ID: ");
				if(courseId.equals("0")) continue;
				boolean validSelection = false;
				for(String[] enrolledCourse : enrolledCourses){
					if(enrolledCourse[1].equals(courseId)){
						validSelection = true;
						break;
					}
				}
				if(validSelection){
					consoleManager.showCourseDetails(courseId);
					viewSelectedCourse(courseId);
				}else{
					consoleManager.showInvalidChoiceError("Please select a valid course ID!");
				}
				
			}
		}
	}
	
	private static void viewSelectedCourse(String courseId){
		int choice;
		
		while(true){
			choice = consoleManager.asForChoiceAtStudentViewSelectedCourse();
			int exerciseId;
			if(choice == 0) return;
			else if(choice == 1){
				// View/Attempt current open HWs
				List<String []> exercises = dbHandler.getCurrentOpenUnattemptedHWs(courseId);
				if(exercises == null){
					// No available exercises.
					consoleManager.showMessageAndWaitForUserToGoBack("No open exercises available.");
					continue;
				}else{
					exerciseId = consoleManager.showExerciseListToStudentAndAskChoice(exercises);
					if(exerciseId == 0) continue;
					attemptExercise(courseId, exerciseId);
				}
			}else{
				// See past HW submissions.
				List<StudentHWAttempt> attempts = dbHandler.getAttamptedHWs(courseId);
				if(attempts == null){
					// No available attempts.
					consoleManager.showMessageAndWaitForUserToGoBack("No past submissions for this course.");
				}else{
					int attemptNum = consoleManager.showAttemptedHWsOverviewtAndAskChoice(attempts);
					if(attemptNum == 0) continue;
					consoleManager.showStudentAttemptDetails(attempts.get(attemptNum-1));
				}
			}			
		}
	}
	
	private static void attemptExercise(String courseId, int exerciseId){
		Exercise exercise = dbHandler.getExercise(exerciseId);
		
		Question question;
		List<Question> questions = null;
		Boolean wasLastQuestionAnsweredCorrectly = null;
		int totalOptions, choice;
		
		int score = 0, maxScore = exercise.getNumQuestions() * exercise.getPointsAwardedPerCorrectAnswer();
		int pointsPerCorrectAnswer = exercise.getPointsAwardedPerCorrectAnswer();
		int pointsPerIncorrectAnswer = exercise.getPointsDeductedPerIncorrectAnswer();
		List<Boolean> wasCorrectlyAnswered = new ArrayList<>();
		String submissionDateTime = null;
		
		List<Integer> questionsDoneInAdaptive = null;
		
		if(exercise.getExerciseMode() == ExerciseMode.Random){
			// Random exercise
			questions = dbHandler.getQuestionsInRandomExercise(exerciseId);
		}else{
			System.out.println("Adaptive. Total Questions: " + exercise.getNumQuestions());
			questions = new ArrayList<>();
			questionsDoneInAdaptive = new ArrayList<>();
		}
		
		int questionDifficulty = 3;
		for(int questionNum = 0 ; questionNum < exercise.getNumQuestions() ; ++questionNum){
			if(exercise.getExerciseMode() == ExerciseMode.Random){
				question = questions.get(questionNum);
			}else{
				question = dbHandler.getNextQuestionInAdaptiveExercise(exerciseId, courseId, wasLastQuestionAnsweredCorrectly, questionDifficulty, exercise.getTopicId(), questionsDoneInAdaptive);
//				System.out.println(question);
				questionDifficulty = question.getDifficultyLevel();
				questions.add(question);
			}
			
			totalOptions = consoleManager.showQuestionToAttempt(question, questionNum + 1);
			choice = consoleManager.askForIntInputBetweenRange("Please enter your choice: ", 0, totalOptions + 1);
			
			if(choice == question.getCorrectChoice()){
				// Correct answer
				wasLastQuestionAnsweredCorrectly = true;
				score += pointsPerCorrectAnswer;
			}else if(choice == 1){
				// Skipped
				wasLastQuestionAnsweredCorrectly = null;
			}else if(choice == 0){
				// Cancel attempt and go back
				return;
			}else{
				// Incorrect answer
				wasLastQuestionAnsweredCorrectly = false;
				score -= pointsPerIncorrectAnswer;
			}
			
			wasCorrectlyAnswered.add(wasLastQuestionAnsweredCorrectly);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		submissionDateTime = dateFormat.format(date);
		
		try {
			if(new Date().compareTo(dateFormat.parse(exercise.getEndDate() + " 0:0:0")) <= 0){
				// Show the summary of attempt.
				StudentHWAttempt attempt = new StudentHWAttempt(score, submissionDateTime, 
						questions, wasCorrectlyAnswered, maxScore, pointsPerCorrectAnswer, 
						pointsPerIncorrectAnswer, false, exerciseId);
				
				consoleManager.showStudentAttemptDetails(attempt);
				dbHandler.addHWAttempt(attempt, courseId, exerciseId);
			}else{
				consoleManager.showMessageAndWaitForUserToGoBack("Sorry, the deadline for this HW has passed!");
				return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		StudentHWAttempt attempt = new StudentHWAttempt(score, submissionDateTime, 
//				questions, wasCorrectlyAnswered, maxScore, pointsPerCorrectAnswer, 
//				pointsPerIncorrectAnswer, false, exerciseId);
//		
//		consoleManager.showStudentAttemptDetails(attempt);
//		dbHandler.addHWAttempt(attempt, courseId, exerciseId);
	}
}
