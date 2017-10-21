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
					System.out.println("Thank you for using the project!");
					break;
				}
				else if (choice == 1){
					// Login
					System.out.print("Username: ");
					String userName = sc.nextLine();
					System.out.print("Password: ");
					String password = sc.nextLine();
					
					consoleManager.clearScreen();
					
					short userType = dbHandler.login(userName, password);
					
					if(userType == 0){
						System.out.println("Invalid username/login.");
					}else if(userType == 1){
						// Professor
						ProfessorHandler.execute();
					}else if(userType == 2){
						// TA
						TAHandler.execute();
					}else if(userType == 3){
						// Student
						StudentHandler.execute();
					}
				}
				else{
					consoleManager.showInvalidChoiceError();
				}
			}
			sc.close();
			
		}catch(SQLException sqlExcpt){
			sqlExcpt.printStackTrace();
		}
	}
}


