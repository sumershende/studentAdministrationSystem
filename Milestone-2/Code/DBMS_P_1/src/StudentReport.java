
class StudentReport {
	private final String firstName, lastName;

	private final String[][] scoresPerHW;
	
	public StudentReport(String firstName, String lastName, String[][] scoresPerHW){
		this.firstName = firstName;
		this.lastName = lastName;
		this.scoresPerHW = scoresPerHW;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String[][] getScoresPerHW() {
		return scoresPerHW;
	}
}
