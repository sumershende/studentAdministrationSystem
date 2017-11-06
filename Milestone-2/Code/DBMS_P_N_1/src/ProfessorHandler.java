import java.util.HashSet;
import java.util.List;

class ProfessorHandler extends TAHandler{
	
	private ProfessorHandler(){
		
	}
	
	public static boolean execute(){
		consoleManager = ConsoleManager.getConsoleManager();
		dbHandler = DBHandler.getDBHandler();
		
		int choice;
		
		while(true){
			consoleManager.showProfessorHomeScreen();
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			
			if (choice == 1){
				// View Profile
				viewProfile();
			}else if(choice == 2){
				// View/Add Courses
				viewOrAddCourses();
			}else if(choice == 3){
				// Enroll/Drop A Student
				String prompt = "1. Enroll a student\n2. Drop a student\nPress 0 to go back.";
				int subChoice = -1;
				while (subChoice != 0){
					switch(consoleManager.askForIntInput(prompt)){
					case 0:
						// Cancel operation.
						subChoice = 0;
						break;
					case 1:
						// Enroll a student
						Boolean wasEnrolled = enrollOrDropStudent(true, null);
						if(wasEnrolled != null && wasEnrolled){
							// New student, enrolled.
							
						}
						break;
					case 2:
						enrollOrDropStudent(false, null);
						break;
					default:
						consoleManager.showInvalidChoiceError();
					}
				}
			}else if(choice == 4){
				// Search/Add questions to Question Bank
				searchOrAddQB();
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
		consoleManager.showProfProfile();
		consoleManager.showMessageAndWaitForUserToGoBack(null);
	}
	
	private static void viewOrAddCourses(){
		int choice;
		while(true){
			displayTaughtCourses();
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
						viewOrEditExercises(courseId);
						choice = 1;
						break;
					case 2:
						// Add exercise
						addExercise(courseId);
						choice = 1;
						break;
					case 3:
						// Add TA
						Boolean wasTAAdded = addTA(courseId);
						if(wasTAAdded != null && wasTAAdded){
							course.setTAs(dbHandler.getTAsInCourse(courseId));
						}
						choice = 1;
						break;
					case 4:
						// Enroll a student
						Boolean wasEnrolled = enrollOrDropStudent(true, courseId);
						if(wasEnrolled != null && wasEnrolled){
							// New student, enrolled.
							course.setEnrolledStudents(dbHandler.getStudentsEnrolledInCourse(courseId));
						}
						choice = 1;
						break;
					case 5:
						// Drop a student
						Boolean wasDropped = enrollOrDropStudent(false, courseId);
						if(wasDropped != null && wasDropped){
							course.setEnrolledStudents(dbHandler.getStudentsEnrolledInCourse(courseId));
						}
						choice = 1;
						break;
					case 6:
						// View report
						viewReport(courseId);
						choice = 1;
						break;
					case 7:
						// Add topic
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
			}else if(choice == 2){
				// Add a new course.
				choice = 0;
				Course newCourse = consoleManager.askNewCourseDetails();
				if (newCourse == null){
					// User cancelled the operation.
					
				}else{
					// Add this new course.
					if(dbHandler.addNewCourse(newCourse)){
						// Successfully added the course
						consoleManager.showMessageAndWaitForUserToGoBack("New course successfully added.");
					}else{
						// Error adding the new course.
						consoleManager.showMessageAndWaitForUserToGoBack("Sorry, there was an error while adding the new course. Please try again.");
					}
				}
			}else{
				consoleManager.showInvalidChoiceError();
			}
		}
	}
	
	private static void viewOrEditExercises(String courseId){
		List<Exercise> exercisesInThisCourse = dbHandler.getExercisesForCourse(courseId);
		HashSet<Integer> exercisesId = new HashSet<>();
		if(exercisesInThisCourse != null){
			for(Exercise exercise : exercisesInThisCourse){
				exercisesId.add(exercise.getId());
			}
		}else{
			consoleManager.showExercisesDetailsForCourse(courseId, exercisesInThisCourse);
			return;
		}
		while(true){
			consoleManager.showExercisesDetailsForCourse(courseId, exercisesInThisCourse);
			int exerciseToEditId = consoleManager.askForIntInput("To edit an exercise, please enter its ID or press 0 to go back: ");
			if(exerciseToEditId == 0){
				// Go back
				return;
			}else if(exercisesId.contains(exerciseToEditId)){
				// Edit the exercise.
				Exercise exerciseToEdit = dbHandler.getExercise(exerciseToEditId);
				consoleManager.showExerciseDetailsToProfessor(exerciseToEdit);
				List<Question> questionsInExercise = dbHandler.getQuestionsInExercise(exerciseToEditId);
				HashSet<Integer> qIdsInExercise = exerciseToEdit.getQIds();
				while(true){
					// Display questions in this exercise.
					int choice = consoleManager.askToAddOrRemoveQuestionFromExercise();
					consoleManager.showQuestions(questionsInExercise, "::Questions currently in the exercise::");
					if(choice == 1){
						// Check if more questions can be added.
						if(qIdsInExercise.size() < exerciseToEdit.getNumQuestions()){
							// Can be added
							int newQId = consoleManager.askNewQuestionToBeAddedExercise(qIdsInExercise);
							if(newQId != 0){
								if(!qIdsInExercise.contains(newQId)){
									// Valid new Q ID
									if(dbHandler.addQuestionToExercise(newQId, exerciseToEditId)){
										// Successfully added the question to the exercise.
										consoleManager.showMessageAndWaitForUserToGoBack("Successfully added the question to the exercise. Press 0 to continue.");
									}else{
										// Error while adding the question to the exercise.
										consoleManager.showMessageAndWaitForUserToGoBack("Error while adding the question to the exercise. Press 0 to continue.");
									}
								}else{
									// Question already present in the exercise!
									consoleManager.showInvalidChoiceError("Question already present in the exercise!");
								}
							}
						}else{
							// No more question can be added.
							consoleManager.showInvalidChoiceError("No more questions can be added to this exercise! Please remove a question to add another.");
						}
					}else if(choice == 2){
						// Remove question
						if(qIdsInExercise.size() != 0){
							// Can be removed
							int qId = consoleManager.askNewQuestionToBeAddedExercise(qIdsInExercise);
							if(qId != 0){
								if(qIdsInExercise.contains(qId)){
									// Valid new Q ID
									if(dbHandler.removeQuestionFromExercise(qId, exerciseToEditId)){
										// Successfully removed the question from the exercise.
										consoleManager.showMessageAndWaitForUserToGoBack("Successfully removed the question from the exercise. Press 0 to continue.");
									}else{
										// Error while removing the question from the exercise.
										consoleManager.showMessageAndWaitForUserToGoBack("Error while removing the question from the exercise. Press 0 to continue.");
									}
								}else{
									// Question does not exist in the exercise!
									consoleManager.showInvalidChoiceError("Question does not exist in the exercise!");
								}
							}
						}else{
							// No more questions left to be removed!
							consoleManager.showInvalidChoiceError("Exercise is empty! No question can be removed.");
						}
					}else if(choice == 0){
						// Go back
						break;
					}else{
						consoleManager.showInvalidChoiceError();
					}
				}
			}else{
				consoleManager.showInvalidChoiceError("Please select a valid exercise ID!");
			}
		}
	}
	
	private static void displayTaughtCourses(){
		// Shows the subjects taught by the professor.
		List<String[]> taughtCourses = dbHandler.getTaughtCoursesByProfessor();
		consoleManager.showCourses(taughtCourses, "Courses taught by you:");
	}
	
	private static Boolean addTA(String courseId){
		String newTAId = consoleManager.askTAId(courseId);
		if (newTAId != null){
			// Try to assign this TA
			int wasAssigned = dbHandler.assignTAToCourse(newTAId, courseId);
			
			switch(wasAssigned){
			case 0:
				consoleManager.showMessageAndWaitForUserToGoBack("TA already added to the course.");
				return false;
			case 1:
				consoleManager.showMessageAndWaitForUserToGoBack("TA successfully added to the course.");
				return true;
			// Constraint Violations
			case 20010:
				consoleManager.showMessageAndWaitForUserToGoBack("ERROR: Student already enrolled in the course.");
				return false;
			case 20011:
				consoleManager.showMessageAndWaitForUserToGoBack("ERROR: Student does not possess a Grad level standing.");
				return false;
			default:
				
				return false;
			}
		}else{
			return null;
		}
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
			consoleManager.showMessageAndWaitForUserToGoBack("Error while adding topic to the course. Please check if the topic ID is valid or if it already added.");
			return false;
		}
	}
	
	private static void addExercise(String courseId){
		String prompt = "Enter exercise type (Random or Adaptive) or press 0 to go back.";
		while(true){
			String choice = consoleManager.askForStringInput(prompt);
			if(choice.equals("0")){
				// Go back
				break;
			}
			if(!choice.equals("Random") && !choice.equals("Adaptive")){
				consoleManager.showInvalidChoiceError();
				continue;
			}
			Exercise newExercise = null;
			if(choice.equals("Random")){
				// Add random exercise
				 newExercise = consoleManager.askDetailsAboutNewExercise(ExerciseMode.Random, courseId);
			}else if(choice.equals("Adaptive")){
				// Add adaptive Exercise
				newExercise = consoleManager.askDetailsAboutNewExercise(ExerciseMode.Adaptive, courseId);
			}
			if(newExercise != null) {
				if(dbHandler.addExerciseToCourse(newExercise, courseId)){
					consoleManager.showMessageAndWaitForUserToGoBack("Exercise successfully added.");
				}else{
					consoleManager.showMessageAndWaitForUserToGoBack("Error while adding the exercise.");
				}}
			
			break;
		}
	}
	
	private static void searchOrAddQB(){
		
		List<String[]> courses = dbHandler.getTaughtCoursesByProfessor();
		for(String[] course : courses){
			List<Question> questions = dbHandler.getQuestionsForCourse(course[1]);
			consoleManager.showQuestions(questions, "> Questions in Course: " + course[0]);
		}
		int choice = -1;
		while(choice != 0){
			consoleManager.showOptionsAtViewQB();
			choice = consoleManager.askForIntInput("Please enter your choice: ");
			switch (choice) {
			case 0:
				break;
			case 1:
				// Search QB
				int subChoice = consoleManager.askQuestionSearchType();
				switch (subChoice) {
				case 0:
					// Go back.
					choice = 0;
					break;
				case 1:
					// Search by question ID.
					int qId = consoleManager.askForIntInput("Please enter the question ID or 0 to cancel search: ");
					if(qId == 0) continue;
					List<Question> questions = dbHandler.searchQuestionsWithQuestionId(qId);
					consoleManager.showQuestions(questions, ">> Your search with Question ID '" + qId + "' returned the following results: ");
					break;
				case 2:
					// Search by topic.
					int topicId = consoleManager.askForIntInput("Please enter the topic ID or 0 to cancel search: ");
					if(topicId == 0) continue;
					questions = dbHandler.searchQuestionsWithTopicId(topicId);
					consoleManager.showQuestions(questions, ">> Your search with Topic ID '" + topicId + "' returned the following results: ");
					break;
				default:
					break;
				}
				break;
			case 2:
				// Add questions to QB
				addQuestionToQB();
				break;
			default:
				consoleManager.showInvalidChoiceError();
				break;
			}
		}
	}
	
	private static void addQuestionToQB(){
		Question newQuestion = consoleManager.askNewQuestionAndItsAnswerDetails();
		if(newQuestion != null){
			// Professor successfully entered a valid question.
			if(dbHandler.addQuestionToQuestionBank(newQuestion)){
				// Successfully added the question to the question bank.
				consoleManager.showMessageAndWaitForUserToGoBack("Successfully added the question to the question bank.");
			}else{
				consoleManager.showMessageAndWaitForUserToGoBack("Error while adding the question to the question bank. Please try again.");
			}
		}
	}
}
