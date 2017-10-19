import java.sql.*;

public class DbTestConnection {
	public static void main(String args[]){  
		try{  
			//step1 load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			
			//step2 create  the connection object  
			Connection con=DriverManager.getConnection(  
			"jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01","gverma","200158973");  
			
			System.out.println("Connection succesfully established.");
			
//			//step3 create the statement object  
//			Statement stmt=con.createStatement();  
//			
//			//step4 execute query  
//			ResultSet rs=stmt.executeQuery("select * from emp");  
//			while(rs.next())  
//			System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));  
			
			//step5 close the connection object  
			con.close();
			System.out.println("Connection succesfully closed.");
		  
		}catch(Exception e){ 
			System.out.println(e);
		}  		  
	} 
}