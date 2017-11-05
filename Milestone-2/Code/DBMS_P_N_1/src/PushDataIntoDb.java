/*
 * Author: Sumer @sshende
 * Need the csv files from which this is imported
 * This adds sample data
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class PushDataIntoDb {

    public static void main(String[] args) {
    	
    	try{  
			//step1 load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			Connection con=DriverManager.getConnection("jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01","gverma","200158973");  
//			insertHasTa("Roles","Roles",con); //Inserting roles
//			insertUsers("User","Users",con); //Insert into Users table
//			insertStudents("Students","Students",con);	//insert into Students
//			insertProfessor("Professor","Professor",con);	//insert into professor
//			insertProfessor("Master_Topic","Master_Topics",con);	//insert into Master_Topic
//			insertCourses("Courses","Courses",con);	//inserting course
//			insertHasTa2("HasTa","HasTa",con);	//insert HasTA
//			insertHasTa("Topics","Topics",con);	//insert topics
//			insertHasTa2("Enrolled_In","Enrolled_In",con);	//insert topics
//			insertQuestions("Questions","Questions",con);
//			insertExercises("Exercises","Exercises",con);
//			insertQuestion_In_Ex("Questions_In_Ex","Questions_In_Ex",con); //insert questionsInEx
//			insertParam_Questions("Param_Questions","Param_Questions",con);	//insert param_questions
//			insertParam_Inc_Questions("Param_Inc_Questions","Param_Inc_Questions",con); //insert Param_inc_questions
//			insertParam_Inc_Questions("Param_Answers","Param_Answers",con);	//insert param_answers
//			insertAssign_Attempt("Assign_Attempt","Assign_Attempt",con);	//insert assign_attempt
//			insertHas_Solved("Has_Solved","Has_Solved",con);
//			insertFixedQuestions("Fixed_Questions","Fixed_Questions",con); // Insert fixed_questions
//			insertFixedQuestions("Fixed_Inc_Answers","Fixed_Inc_Answers",con); // Insert Incorrect answers for fixed questions
//			insertFixedQuestions("Fixed_Inc_Answers2","Fixed_Inc_Answers",con); // Insert Incorrect answers for fixed questions2
//			insertFixedQuestions("Fixed_Inc_Answers3","Fixed_Inc_Answers",con); // Insert Incorrect answers for fixed questions3
			
			con.close();  
		  
		}catch(Exception e){ 
			System.out.println(e);
		}
    }

    static void insertStudents(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+",'"+data[1]+"',"+data[2];
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertProfessor(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+",'"+data[1]+"'";
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertCourses(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values="'"+data[0]+"','"+data[1]+"',TO_DATE('"+data[2]+"','MM-DD-YYYY'),"+"TO_DATE('"+data[3]+"','MM-DD-YYYY'),"+data[4]+","+data[5]+","+data[6];
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertHasTa(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values="'"+data[0]+"',"+data[1];
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertHasTa2(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values="'"+data[0]+"',"+data[1];
    			System.out.println("INSERT INTO "+tablename+"(C_ID,ST_ID) VALUES ("+values+")");
    			stmt.executeQuery("INSERT INTO "+tablename+"(C_ID,ST_ID) VALUES ("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertUsers(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values="'"+data[0]+"','"+data[1]+"','"+data[2]+"',"+data[3];
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertQuestions(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+","+data[1]+",'"+data[2]+"','"+data[3]+"','"+data[4]+"',"+data[5];
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertExercises(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+",'"+data[1]+"','"+data[2]+"',TO_DATE('"+data[3]+"','MM-DD-YYYY'),"+"TO_DATE('"+data[4]+"','MM-DD-YYYY'),"+data[5]+","+data[6]+",'"+data[7]+"',"+data[8]+","+data[9]+","+data[10];
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertQuestion_In_Ex(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+","+data[1];
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertFixedQuestions(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+",'"+data[1]+"'";
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertParam_Questions(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+","+data[1]+","+data[2]+",'"+data[3]+"'";
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertParam_Inc_Questions(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+","+data[1]+",'"+data[2]+"'";
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertAssign_Attempt(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
    			String values=data[0]+","+data[1]+","+data[2]+","+data[3]+","+data[4]+","+data[5];
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static void insertHas_Solved(String filename, String tablename, Connection con){

        String csvFile = "C:/Users/Sumer/Desktop/My Files/NCSU Assignments/DBMS/Project 1/Project/Data/"+filename+".csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean first = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	
            	if(first) {
            		first=false;
            		continue;
            	}
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Statement stmt=con.createStatement();  
                String values=data[0]+","+data[1]+","+data[2]+",TO_DATE('"+data[3]+"','MM-DD-YYYY')";
    			System.out.println(values);
    			stmt.executeQuery("INSERT INTO "+tablename+" VALUES("+values+")");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){ 
			System.out.println("SQL Error\n"+e);
		}finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
      
}
