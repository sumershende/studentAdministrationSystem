

import java.util.Scanner;

class ConsoleManager {
	
	private static ConsoleManager consoleManager;
	
	private Scanner sc;
	
	private DBHandler dbHandler;
	
	private ConsoleManager(){
		// Make it singleton
		sc = new Scanner(System.in);
		dbHandler = DBHandler.getDBHandler();
	}
	
	public static ConsoleManager getConsoleCreator(){
		if(consoleManager == null) consoleManager = new ConsoleManager();
		return consoleManager;
	}
	
	public void createStartConsole(){
		clearScreen();
		System.out.println("1. Login");
		System.out.println("2. Exit");
	}
	
	
	// Method stack for professor
	public void createProfessorHomeScreen(){
		clearScreen();
		System.out.println("1. View Profile");
		System.out.println("2. View/Add Courses");
		System.out.println("3. Enroll/Drop A Student");
		System.out.println("4. Search/Add questions to Question Bank");
		System.out.println("5. Logout");
	}
	
	public void showProfOrTAProfile(String fName, String lName, String eId){
		clearScreen();
		System.out.println("Press 0 to Go Back");
		System.out.println("1. First Name: " + fName);
		System.out.println("2. Last Name: " + lName);
		System.out.println("3. Employee Id: " + eId);
	}
	
	
	// Method stack for TA
	public void createTAHomeScreen(){
		clearScreen();
		System.out.println("1. View Profile");
		System.out.println("2. View/Add Courses");
		System.out.println("3. Enroll/Drop A Student");
		System.out.println("4. Logout");			
	}
	
	
	// Method stack for Student
	public void createStudentHomeScreen(){
		clearScreen();
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
		}
		
		choice = sc.nextInt();
		
		return choice;
	}
	
	public String askForStringInput(String message){
		String choice;
		
		if (message != null){
			System.out.println(message);
		}
		
		choice = sc.nextLine();
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
	
	public void displayInvalidChoiceError(){
		System.out.println("Please select a valid option!");
	}
	
	public void displayCourses(String[][] courses, String message, boolean isProfessor){
		clearScreen();
		System.out.println(message);
		for(String[] course : courses){
			System.out.println(course[0] + " : " + course[1]);
		}
		System.out.println("\n1. View course details.");
		if (isProfessor){
			System.out.println("2. Add new course.");
		}
		System.out.println("Press 0 to go back to previous screen.");
	}
	
	public void displayCourseDetails(String courseId, boolean isProf){
		Course course = dbHandler.getCourseInfo(courseId);
		System.out.println("\n1. Course Name: " + course.getCourseName());
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
		System.out.println("Press 0 to go back to previous screen.");
	}
}
