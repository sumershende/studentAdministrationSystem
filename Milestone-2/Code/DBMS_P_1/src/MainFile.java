import java.sql.SQLException;
import java.util.Scanner;

public class MainFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBHandler dbHandler = DBHandler.getDBHandler();
		
		// Create a valid connection:
		try{
			dbHandler.createConnection();
			
			ConsoleManager consoleManager = ConsoleManager.getConsoleManager();
			
			Scanner sc = new Scanner(System.in);
			
			int choice = 3;
			while(true){
				consoleManager.showStartConsole();
				choice = consoleManager.askForIntInput("Please enter your choice: ");
				consoleManager.clearScreen();
				
				if (choice == 2){
					consoleManager.showExitMessage();
					break;
				}
				else if (choice == 1){
					// Login
					String userId = consoleManager.askForStringInput("Enter Username: ");
					String password = consoleManager.askForStringInput("Enter password: ");
					
					consoleManager.clearScreen();
					
					UserType userType = dbHandler.login(userId, password);
					
					if(userType == UserType.InvalidUser){
						System.out.println("Invalid username/login.");
					}else if(userType == UserType.Professor){
						// Professor
						ProfessorHandler.execute();
					}else if(userType == UserType.TA){
						// TA
						// Ask the user whether he wants to login as TA or student.
						boolean switchAccount = TAHandler.execute();
						while(true){
							if(switchAccount){
								if(dbHandler.getLoggedInUserType() == UserType.TA){
									// Switch to student account.
									dbHandler.changeTAToStudent();
									switchAccount = StudentHandler.execute(true);
								}else{
									// Switch to TA account.
									dbHandler.changeStudentToTA();
									switchAccount = TAHandler.execute();
								}
							}else break;
						}
					}else if(userType == UserType.Student){
						// Student
						StudentHandler.execute(false);
					}
				}
				else{
					consoleManager.showInvalidChoiceError();
				}
			}
			sc.close();
			
		}catch(SQLException sqlExcpt){
			sqlExcpt.printStackTrace();
		}finally{
			dbHandler.closeConnection();
		}
	}
}


