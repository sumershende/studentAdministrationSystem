import java.sql.SQLException;
import java.util.Scanner;

public class MainFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBHandler dbHandler = DBHandler.getDBHandler();
		
		// Create a valid connection:
		try{
			dbHandler.createConnection();
			
			ConsoleManager consoleManager = ConsoleManager.getConsoleCreator();
			
			Scanner sc = new Scanner(System.in);
			
			int choice = 3;
			while(true){
				consoleManager.createStartConsole();
				choice = consoleManager.askForInput("Please enter your choice: ");
				consoleManager.clearScreen();
				
				if (choice == 2){
					System.out.println("Thank you for using the project!");
					break;
				}
				else if (choice == 1){
					// Login
					String userName = new String(System.console().readPassword("UserName: "));
					String password = new String(System.console().readPassword("Password: "));
					
					consoleManager.clearScreen();
					
					int userIs = dbHandler.login(userName, password);
					
					if(userIs == 0){
						System.out.println("Invalid username/login.");
					}else if(userIs == 1){
						// Professor
						ProfessorHandler.execute();
					}else if(userIs == 2){
						// TA
					}else if(userIs == 3){
						// Student
					}
				}
				else{
					consoleManager.displayInvalidChoiceError();
				}
			}
			sc.close();
			
		}catch(SQLException sqlExcpt){
			sqlExcpt.printStackTrace();
		}catch (IdentifierNotSetException e) {
			e.printStackTrace();
		}		
	}
}


