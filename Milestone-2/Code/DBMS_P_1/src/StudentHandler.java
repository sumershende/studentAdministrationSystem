
class StudentHandler {
	DBHandler dbHandler;
	ConsoleManager consoleManager;
	
	private StudentHandler(){
		// Make it singleton
		dbHandler = DBHandler.getDBHandler();
		consoleManager = ConsoleManager.getConsoleManager();
	}
	
	public static void execute(){
		
	}
}