import java.util.List;

class ProfessorHandler extends TAHandler{
	
	public static void execute(){
		consoleManager = ConsoleManager.getConsoleManager();
		dbHandler = DBHandler.getDBHandler();
		
		int choice;
		
		fName = dbHandler.getLoggedInUserFirstName();
		lName = dbHandler.getLoggedInUserLastName();
		eId   = dbHandler.getLoggedInUserId();
		
		while(true){
			consoleManager.showProfessorHomeScreen();
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			
			if (choice == 1){
				// View Profile
				viewProfile();
			}else if(choice == 2){
				// View/Add Courses
				viewOrAddCourses();
			}else if(choice == 3){
				// Enroll/Drop A Student
				String prompt = "1. Enroll a student\n2. Drop a student\nPress 0 to go back.";
				int subChoice = -1;
				while (subChoice != 0){
					switch(consoleManager.askForIntInput(prompt)){
					case 0:
						subChoice = 0;
						break;
					case 1:
						enrollOrDropStudent(true, null);
						break;
					case 2:
						enrollOrDropStudent(false, null);
						break;
					default:
						consoleManager.showInvalidChoiceError();
					}
				}
			}else if(choice == 4){
				// Search/Add questions to Question Bank
				searchOrAddQB();
			}else if(choice == 5){
				// Logout
				if(dbHandler.logout()) break;
				else consoleManager.showMessageAndForUserToGoBack("Error logging out.");
			}else{
				consoleManager.showInvalidChoiceError();
			}
		}
	}
	
	private static void viewOrAddCourses(){
		int choice;
		while(true){
			displayTaughtCourses();
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			if (choice == 0){
				break;
			}else if(choice == 1){
				// View a particular course.
				String courseId = consoleManager.askForStringInput("Please enter the course ID:");
				while(choice != 0){
					consoleManager.showCourseDetails(courseId, dbHandler.isProfessor());
					choice = consoleManager.askForIntInput("Please enter your choice: ");
					switch (choice) {
					case 0:
						// Return
						break;
					case 4:
						// View Exercises
						viewOrEditExercises(courseId);
						choice = 1;
						break;
					case 5:
						// Add exercise
						addExercises(courseId);
						choice = 1;
						break;
					case 7:
						// Add TA
						addTA(courseId);
						choice = 1;
						break;
					case 8:
						// Enroll a student
						enrollOrDropStudent(true, courseId);
						choice = 1;
						break;
					case 9:
						// Drop a student
						enrollOrDropStudent(false, courseId);
						choice = 1;
						break;
					case 10:
						// View report
						viewReport(courseId);
						choice = 1;
						break;
					default:
						consoleManager.showInvalidChoiceError();
						break;
					}
				}
			}else if(choice == 2){
				// Add a new course.
				choice = 0;
				Course newCourse = consoleManager.askNewCourseDetails();
				if (newCourse == null){
					// User cancelled the operation.
					
				}else{
					// Add this new course.
					if(dbHandler.addNewCourse(newCourse)){
						// Successfully added the course
						consoleManager.showMessageAndForUserToGoBack("New course successfully added.");
					}else{
						// Error adding the new course.
						consoleManager.showMessageAndForUserToGoBack("Sorry, there was an error while adding the new course. Please try again.");
					}
				}
			}else{
				consoleManager.showInvalidChoiceError();
			}
		}
	}
	
	private static void displayTaughtCourses(){
		// Shows the subjects taught by the professor.
		List<String[]> taughtCourses = dbHandler.getTaughtCoursesByProfessor();
		consoleManager.showCourses(taughtCourses, "Courses taught by you:", dbHandler.isProfessor());
	}
	
	private static void addTA(String courseId){
		String newTAId = consoleManager.askForTAId();
		if (newTAId != null){
			// Try to assign this TA
			if(dbHandler.assignTAToCourse(courseId, newTAId)){
				// Successfully assigned as TA.
				consoleManager.showMessageAndForUserToGoBack("Successfully added " + newTAId + " as TA for course " + courseId);
			}else{
				// Error while assigning as TA.
				consoleManager.showMessageAndForUserToGoBack("Error while assigning " + newTAId + " as TA for course " + courseId);
			}
		}
	}
	
	private static void searchOrAddQB(){
		List<String[]> courses = dbHandler.getTaughtCoursesByProfessor();
		for(String[] course : courses){
			List<Question> questions = dbHandler.getQuestionsForCourse(course[1], course[0]);
			consoleManager.showQuestions(questions, "course: " + course[0]);
		}
		int choice = -1;
		while(choice != 0){
			consoleManager.showOptionsAtViewQB();
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			switch (choice) {
			case 0:
				break;
			case 1:
				// Search QB
				String searchQuery = consoleManager.askForStringInput("Please enter your searchQuery:");
				if(searchQuery.equals("0")){
					// Professor cancelled search operation.
				}else{
					// Professor submitted a search query
					List<Question> questions = dbHandler.getQuestionsWithSearchQuery(searchQuery);
					consoleManager.showQuestions(questions, "search query: " + searchQuery);
					consoleManager.showMessageAndForUserToGoBack(null);
				}
				break;
			case 2:
				// Add questions to QB
				addQuestionToQB();
				break;
			default:
				consoleManager.showInvalidChoiceError();
				break;
			}
		}
	}
	
	private static void addQuestionToQB(){
		Question newQuestion = consoleManager.askNewQuestionAndItsAnswerDetails();
		if(newQuestion != null){
			// Professor successfully entered a valid question.
			if(dbHandler.addQuestionToQuestionBank(newQuestion)){
				// Successfully added the question to the question bank.
				consoleManager.showMessageAndForUserToGoBack("Successfully added the question to the question bank.");
			}else{
				consoleManager.showMessageAndForUserToGoBack("Error while adding the question to the question bank. Please try again.");
			}
		}
	}
}
