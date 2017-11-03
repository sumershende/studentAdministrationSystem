import java.sql.*;

class DbTestConnection {
	public static void main(String args[]){  
		try{  
			//step1 load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			
			//step2 create  the connection object  
			Connection con = (Connection)DriverManager.getConnection(  
					"jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01", "gverma", "200158973");  
			
			//step3 create the statement object  
			Statement stmt=con.createStatement();  
			
			//step4 execute query
//			ResultSet rs=stmt.executeQuery("select * from users");  
//			while(rs.next())  
//				System.out.println(rs.getString(1)+" "+rs.getString(2)+"  "+rs.getString(3) + " " + rs.getInt(4));
			
			
//			ResultSet rs=stmt.executeQuery("select * from roles");  
//			while(rs.next())  
//				System.out.println(rs.getString(1)+" "+rs.getInt(2));
			
			ResultSet rs=stmt.executeQuery("select * from courses");  
			while(rs.next())  
				System.out.println(rs.getString(1)+" "+rs.getString(2));
			
			//step5 close the connection object  
			con.close();  
		  
		}catch(Exception e){ 
			e.printStackTrace();
		}  		  
	} 
}
