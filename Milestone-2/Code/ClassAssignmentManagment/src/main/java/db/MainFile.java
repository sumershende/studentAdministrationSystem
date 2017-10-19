package db;

import java.sql.SQLException;
import java.util.Scanner;

public class MainFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBHandler dbHandler = DBHandler.getDBHandler();
		
		// Create a valid connection:
		try{
			dbHandler.createConnection();
			
			ConsoleCreator consoleCreator = new ConsoleCreator();
			
			Scanner sc = new Scanner(System.in);
			
			int choice = 3;
			while(true){
				consoleCreator.createStartConsole();
				choice = sc.nextInt();
				if (choice == 2){
					System.out.println("Thank you for using the project! Exiting...");
					break;
				}
				else if (choice == 1){
					// Login
					String userName = new String(System.console().readPassword("UserName: "));
					String password = new String(System.console().readPassword("Password: "));
					
					if(dbHandler.login()){
						
					}else{
						System.out.println("Invalid username/login.");
					}
				}
				else{
					System.out.println("Please select a valid option.");
				}
			}
			
		}catch(SQLException sqlExcpt){
			sqlExcpt.printStackTrace();
		}catch (IdentifierNotSetException e) {
			e.printStackTrace();
		}		
	}
}


