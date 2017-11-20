import java.util.List;

class TAHandler {
	protected static ConsoleManager consoleManager;
	
	protected static DBHandler dbHandler;
	
	protected TAHandler(){
		
	}
	
	public static boolean execute(){
		consoleManager = ConsoleManager.getConsoleManager();
		dbHandler = DBHandler.getDBHandler();
		
		int choice;
		
		while(true){
			consoleManager.showTAHomeScreen();
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			
			if (choice == 1){
				// View Profile
				viewProfile();
			}else if(choice == 2){
				// View courses of which the person is TA.
				displayTACourses();
			}else if(choice == 3){
				// Enroll/Drop A Student
				String prompt = "1. Enroll a student\n2. Drop a student\nPress 0 to go back.";
				int subChoice = -1;
				while (subChoice != 0){
					switch(consoleManager.askForIntInput(prompt)){
					case 0:
						subChoice = 0;
						break;
					case 1:
						enrollOrDropStudent(true, null);
						break;
					case 2:
						enrollOrDropStudent(false, null);
						break;
					default:
						consoleManager.showInvalidChoiceError();
					}
				}				
			}else if(choice == 4){
				// Switch to student account.
				return true;
			}else if(choice == 5){
				// Logout
				if(dbHandler.logout()) break;
				else consoleManager.showMessageAndWaitForUserToGoBack("Error logging out.");
			}else{
				consoleManager.showInvalidChoiceError();
			}
		}
		return false;
	}
	
	private static void viewProfile(){
		consoleManager.showTAProfile();
		consoleManager.showMessageAndWaitForUserToGoBack(null);
	}
	
	private static void displayTACourses(){
		List<String[]> TACourses = dbHandler.getTACourses();
		
		int choice;
		while(true){
			consoleManager.showCourses(TACourses, "Courses of which you are TA:");
			choice = consoleManager.askForUserChoiceAfterShowingCourses(dbHandler.isProfessor());
			if (choice == 0){
				break;
			}else if(choice == 1){
				// View a particular course.
				String courseId;
				Course course = null;
				while(true){
					courseId = consoleManager.askForStringInput("Please enter the course ID or 0 to cancel: ");
					if(courseId.equals("0")) break;
					course = dbHandler.getCourseInfo(courseId);
					if(course == null){
						consoleManager.showInvalidChoiceError("Please enter a valid course ID!");
					}else break;
				}
				if(courseId.equals("0") || course == null) continue;
				
				while(choice != 0){
					consoleManager.showCourseDetails(course);
					choice = consoleManager.askForUserChoiceAfterShowingCourseDetails(dbHandler.isProfessor());
					switch (choice) {
					case 0:
						// Return
						break;
					case 1:
						// View Exercises
						viewExercises(courseId);
						choice = 1;
						break;
					case 2:
						// Enroll a student
						Boolean wasEnrolled = enrollOrDropStudent(true, courseId);
						if(wasEnrolled != null && wasEnrolled){
							// New student, enrolled.
							course.setEnrolledStudents(dbHandler.getStudentsEnrolledInCourse(courseId));
						}
						choice = 1;
						break;
					case 3:
						// Drop a student
						Boolean wasDropped = enrollOrDropStudent(false, courseId);
						if(wasDropped != null && wasDropped){
							course.setEnrolledStudents(dbHandler.getStudentsEnrolledInCourse(courseId));
						}
						choice = 1;
						break;
					case 4:
						// View report
						viewReport(courseId);
						choice = 1;
						break;
					case 5:
						if(addTopic(courseId)){
							course = dbHandler.getCourseInfo(courseId);
						}
						choice = 1;
						break;
					default:
						consoleManager.showInvalidChoiceError();
						break;
					}
				}
			}else{
				consoleManager.showInvalidChoiceError();
			}
		}
	}
	
	private static void viewExercises(String courseId){
		List<Exercise> exercisesInThisCourse = dbHandler.getExercisesForCourse(courseId);
		consoleManager.showExercisesDetailsForCourse(courseId, exercisesInThisCourse);
		consoleManager.showMessageAndWaitForUserToGoBack("Please enter 0 to go back.");
	}
	
	private static boolean addTopic(String courseId){
		int topicId = consoleManager.askForIntInput("Please enter the topic ID or press 0 to cancel: ");
		if(topicId == 0) return false;
		Boolean result = dbHandler.addTopicToCourse(topicId, courseId);
		if(result == null){
			consoleManager.showMessageAndWaitForUserToGoBack("Topic already present in the course!");
			return false;
		}
		else if(result){
			consoleManager.showMessageAndWaitForUserToGoBack("Topic successfully added to the course.");
			return true;
		}else{
			consoleManager.showMessageAndWaitForUserToGoBack("Error while adding topic to the course. Please check if the topic ID is valid or if it is already added.");
			return false;
		}
	}
	
	protected static Boolean enrollOrDropStudent(boolean isEnroll, String courseId){
		String[] newStudentAndCourseDetails = consoleManager.askForNewStudentDetails(courseId);
		if(newStudentAndCourseDetails == null){
			// User cancelled the operation.
			return null;
		}else{
			if(isEnroll){
				// Try to add the student to the course.
				int wasEnrolled = dbHandler.addNewStudentToCourse(newStudentAndCourseDetails[0], newStudentAndCourseDetails[1]);
				System.out.println(wasEnrolled);
				if(wasEnrolled == 20010){
					consoleManager.showMessageAndWaitForUserToGoBack("ERROR: Student cannot be enrolled in class as the Student is the TA.");
				}
				if(wasEnrolled==20000){
					consoleManager.showMessageAndWaitForUserToGoBack("ERROR: Max Class size limit reached.");
				}
				if(wasEnrolled == 0){
					// Already in the course.
					consoleManager.showMessageAndWaitForUserToGoBack("The student is already enrolled in the course.");
				}else if(wasEnrolled==1){
					// Successfully added the student to the course.
					consoleManager.showMessageAndWaitForUserToGoBack("The new student was successfully added.");
					return true;
				}else{
					// Error while adding the student to the course.
					consoleManager.showMessageAndWaitForUserToGoBack("Sorry, there was an error while adding the new student. Please try again.");
				}
				return false;
			}else{
				// Try to drop the student from the course.
				Boolean wasDropped = dbHandler.dropStudentFromCourse(newStudentAndCourseDetails[0], newStudentAndCourseDetails[1]);
				if(wasDropped == null){
					// Already not in the course.
					consoleManager.showMessageAndWaitForUserToGoBack("Student is not enrolled in the course!");
				}else if(wasDropped){
					// Successfully dropped the student from the course.
					consoleManager.showMessageAndWaitForUserToGoBack("Student successfully dropped from the course.");
				}else{
					// Error while dropping the student from the course.
					consoleManager.showMessageAndWaitForUserToGoBack("Sorry, there was an error while dropping the student. Please try again.");
				}
				return wasDropped;
			}
		}
	}
	
	protected static void viewReport(String courseId){
		List<StudentReport> studentReports = dbHandler.getStudentReports(courseId);
		if(studentReports == null){
			// No report for this course.
			consoleManager.showMessageAndWaitForUserToGoBack("No report found for course with ID: " + courseId);
		}else{
			consoleManager.showReportForCourse(studentReports, courseId);
		}
	}
}
