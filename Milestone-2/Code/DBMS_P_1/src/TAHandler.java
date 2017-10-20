class TAHandler {
	protected static ConsoleManager consoleManager;
	
	protected static String fName, lName, eId;
	
	protected static DBHandler dbHandler;
	
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
				// View courses of which the person is TA.
				displayTACourses();
			}else if(choice == 3){
				// Enroll/Drop A Student
				
			}else if(choice == 4){
				// Logout
				break;
			}else{
				consoleManager.displayInvalidChoiceError();
			}
		}
	}
	
	protected static void viewProfile(){
		int choice;
		consoleManager.showProfOrTAProfile(fName, lName, eId);
		while(true){
			choice = consoleManager.askForIntInput(null);
			if (choice != 0){
				consoleManager.displayInvalidChoiceError();
			}else{
				break;
			}
		}
	}
	
	private static void displayTACourses(){
		String[][] TACourses = dbHandler.getTACourses();
		
		int choice;
		while(true){
			consoleManager.displayCourses(TACourses, "Courses of which you are TA:", dbHandler.isProfessor());
			choice = consoleManager.askForIntInput("Please enter your choice:");
			if (choice == 0){
				break;
			}else if(choice == 1){
				// View a particular course.
				String courseId = consoleManager.askForStringInput("Please enter the course ID:");
				consoleManager.displayCourseDetails(courseId, dbHandler.isProfessor());
				while(true){
					choice = consoleManager.askForIntInput("Please enter your choice:");
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
						// Enroll a student
						enrollStudent();
						choice = 1;
						break;
					case 8:
						// Drop a student
						dropStudent();
						choice = 1;
						break;
					case 9:
						// View report
						viewReport();
						choice = 1;
						break;
					default:
						consoleManager.displayInvalidChoiceError();
						break;
					}
				}
			}else{
				consoleManager.displayInvalidChoiceError();
			}
		}
	}
	
	protected static void viewExercises(){
		
	}
	
	protected static void addExercises(){
		
	}
	
	protected static void enrollStudent(){
		
	}
	
	protected static void dropStudent(){
		
	}
	
	protected static void viewReport(){
		
	}
}
