class ProfessorHandler {
	private static ConsoleManager consoleManager;
	
	private static String fName, lName, eId;
	
	private static DBHandler dbHandler;
	
	public static void execute(){
		consoleManager = ConsoleManager.getConsoleCreator();
		dbHandler = DBHandler.getDBHandler();
		
		int choice;
		
		String[] profDetails = new String[]{"Gautam", "Verma", "200158973"};
		
		fName = profDetails[0];
		lName = profDetails[1];
		eId   = profDetails[2];
		
		while(true){
			consoleManager.createProfessorHomeScreen();
			choice = consoleManager.askForInput("Please enter your choice: ");
			
			if (choice == 1){
				// View Profile
				viewProfile();
			}else if(choice == 2){
				// View/Add Courses
				
			}else if(choice == 3){
				// Enroll/Drop A Student
				
			}else if(choice == 4){
				// Search/Add questions to Question Bank
				
			}else if(choice == 5){
				// Logout
				
			}else{
				consoleManager.displayInvalidChoiceError();
			}
		}
	}
	
	private static void viewProfile(){
		int choice;
		consoleManager.showProfOrTAProfile(fName, lName, eId);
		while(true){
			choice = consoleManager.askForInput(null);
			if (choice != 0){
				consoleManager.displayInvalidChoiceError();
			}
		}
	}
}
