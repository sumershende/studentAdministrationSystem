import java.util.List;

class TAHandler {
	protected static ConsoleManager consoleManager;
	
	protected static String fName, lName, eId;
	
	protected static DBHandler dbHandler;
	
	public static void execute(){
		consoleManager = ConsoleManager.getConsoleManager();
		dbHandler = DBHandler.getDBHandler();
		
		int choice;
		
		fName = dbHandler.getLoggedInUserFirstName();
		lName = dbHandler.getLoggedInUserLastName();
		eId   = dbHandler.getLoggedInUserId();
		
		while(true){
			consoleManager.showTAHomeScreen();
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			
			if (choice == 1){
				// View Profile
				viewProfile();
			}else if(choice == 2){
				// View courses of which the person is TA.
				displayTACourses();
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
				// Logout
				if(dbHandler.logout()) break;
				else consoleManager.showMessageAndForUserToGoBack("Error logging out.");
			}else{
				consoleManager.showInvalidChoiceError();
			}
		}
	}
	
	protected static void viewProfile(){
		int choice;
		consoleManager.showProfOrTAProfile(fName, lName, eId);
		while(true){
			choice = consoleManager.askForIntInput("Please enter 0 to go back.");
			if (choice != 0){
				consoleManager.showInvalidChoiceError();
			}else{
				break;
			}
		}
	}
	
	private static void displayTACourses(){
		List<String[]> TACourses = dbHandler.getTACourses();
		
		int choice;
		while(true){
			consoleManager.showCourses(TACourses, "Courses of which you are TA:", dbHandler.isProfessor());
			choice = consoleManager.askForIntInput("Please enter your choice:");
			if (choice == 0){
				break;
			}else if(choice == 1){
				// View a particular course.
				String courseId = consoleManager.askForStringInput("Please enter the course ID:");
				while(choice != 0){
					consoleManager.showCourseDetails(courseId, dbHandler.isProfessor());
					choice = consoleManager.askForIntInput("Please enter your choice:");
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
						// Enroll a student
						enrollOrDropStudent(true, courseId);
						choice = 1;
						break;
					case 8:
						// Drop a student
						enrollOrDropStudent(false, courseId);
						choice = 1;
						break;
					case 9:
						// View report
						viewReport(courseId);
						choice = 1;
						break;
					default:
						consoleManager.showInvalidChoiceError();
						break;
					}
				}
			}else{
				consoleManager.showInvalidChoiceError();
			}
		}
	}
	
	protected static void viewOrEditExercises(String courseId){
		while(true){
			consoleManager.showExercisesForCourse(courseId);
			String exerciseToEdit = consoleManager.askForStringInput("To edit an exercise, please enter its ID or press 0 to go back: ");
			if(exerciseToEdit.equals("0")){
				// Go back
				break;
			}else{
				// Edit the exercise.
				while(true){
					int choice = consoleManager.askToAddOrRemoveQuestionFromExercise();
					if(choice == 1){
						// Add question
					}else if(choice == 2){
						// Remove question
					}else if(choice == 0){
						// Go back
						break;
					}else{
						consoleManager.showInvalidChoiceError();
					}
				}
			}
		}
	}
	
	protected static void addExercises(String courseId){
		String prompt = "Enter exercise type (Random or Adaptive) or press 0 to go back.";
		while(true){
			String choice = consoleManager.askForStringInput(prompt);
			if(choice.equals("Random")){
				// Add random exercise
				break;
			}else if(choice.equals("Adaptive")){
				// Add adaptive Exercise
				break;
			}else if(choice.equals("0")){
				// Go back
				break;
			}else{
				consoleManager.showInvalidChoiceError();
			}
		}
	}
	
	protected static void enrollOrDropStudent(boolean isEnroll, String courseId){
		String[] newStudentAndCourseDetails = consoleManager.askForNewStudentDetails(courseId);
		if(newStudentAndCourseDetails == null){
			// User cancelled the operation.
		}else{
			if(isEnroll){
				// Try to add the student to the course.
				if(dbHandler.addNewStudentToCourse(newStudentAndCourseDetails[0], newStudentAndCourseDetails[1])){
					// Successfully added the student to the course.
					consoleManager.showMessageAndForUserToGoBack("New student successfully added.");
				}else{
					// Error while adding the student to the course.
					consoleManager.showMessageAndForUserToGoBack("Sorry, there was an error while adding the new student. Please try again.");
				}
			}else{
				// Try to drop the student from the course.
				if(dbHandler.dropStudentFromCourse(newStudentAndCourseDetails[0], newStudentAndCourseDetails[1])){
					// Successfully dropped the student from the course.
					consoleManager.showMessageAndForUserToGoBack("Student successfully dropped.");
				}else{
					// Error while dropping the student from the course.
					consoleManager.showMessageAndForUserToGoBack("Sorry, there was an error while dropping the student. Please try again.");
				}
			}
		}
	}
	
	protected static void viewReport(String courseId){
		// See report of a particular student.
		// Ask the student ID:
		String studentId = consoleManager.askForStringInput("Please Enter Student Id: ");
		
		// Display the report of student.
		StudentReport studentReport = dbHandler.getStudentReport(studentId);
		if(studentReport == null){
			// No report for such student.
			consoleManager.showMessageAndForUserToGoBack("No report found for student with ID: " + studentId);
		}else{
			consoleManager.showStudentReport(studentReport, courseId);
		}
	}
}
