class ProfessorHandler extends TAHandler{
	
	public static void execute(){
		consoleManager = ConsoleManager.getConsoleCreator();
		dbHandler = DBHandler.getDBHandler();
		
		int choice;
		
		fName = dbHandler.getLoggedInUserFirstName();
		lName = dbHandler.getLoggedInUserLastName();
		eId   = dbHandler.getLoggedInUserId();
		
		while(true){
			consoleManager.createProfessorHomeScreen();
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			
			if (choice == 1){
				// View Profile
				viewProfile();
			}else if(choice == 2){
				// View/Add Courses
				viewOrAddCourses();
			}else if(choice == 3){
				// Enroll/Drop A Student
				
			}else if(choice == 4){
				// Search/Add questions to Question Bank
				
			}else if(choice == 5){
				// Logout
				break;
			}else{
				consoleManager.displayInvalidChoiceError();
			}
		}
	}
	
	private static void viewOrAddCourses(){
		int choice;
		while(true){
			displayTaughtCourses();
			choice = consoleManager.askForIntInput("Please enter your choice:");
			if (choice == 0){
				break;
			}else if(choice == 1){
				// View a particular course.
				String courseId = consoleManager.askForStringInput("Please enter the course ID:");
				consoleManager.displayCourseDetails(courseId, dbHandler.isProfessor());
				while(choice != 0){
					choice = consoleManager.askForIntInput("Please enter your choice: ");
					switch (choice) {
					case 0:
						// Return
						break;
					case 4:
						// View Exercises
						viewExercises();
						choice = 1;
						break;
					case 5:
						// Add exercise
						addExercises();
						choice = 1;
						break;
					case 7:
						// Add TA
						addTA();
						choice = 1;
						break;
					case 8:
						// Enroll a student
						enrollStudent();
						choice = 1;
						break;
					case 9:
						// Drop a student
						dropStudent();
						choice = 1;
						break;
					case 10:
						// View report
						viewReport();
						choice = 1;
						break;
					default:
						consoleManager.displayInvalidChoiceError();
						break;
					}
				}
			}else if(choice == 2){
				// Add a new course.
				
			}else{
				consoleManager.displayInvalidChoiceError();
			}
		}
	}
	
	private static void displayTaughtCourses(){
		// Shows the subjects taught by the professor.
		String [][] taughtCourses = dbHandler.getTaughtCoursesByProfessor();
		consoleManager.displayCourses(taughtCourses, "Courses taught by you:", dbHandler.isProfessor());
	}
	
	private static void addTA(){
		
	}	
}
