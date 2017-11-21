import java.util.List;


class StudentReport {
	private int studentId;
	private String name;
	private List<Integer[]> scoresPerHW;
	private List<Integer> scorePerPolicy;
	
	public StudentReport(String name, List<Integer[]> scoresPerHW){
		this.setName(name);
		this.scoresPerHW = scoresPerHW;
	}
	
	public StudentReport() {
		// TODO Auto-generated constructor stub
	}

	public void setScoresPerHW(List<Integer[]> scoresPerHW) {
		this.scoresPerHW = scoresPerHW;
	}

	public List<Integer[]> getScoresPerHW() {
		return scoresPerHW;
	}

	public void setScorePerPolicy(List<Integer> s) {
		this.scorePerPolicy = s;
	}
	
	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
	
	public List<Integer> getScoresPerPolicy(){
		return this.scorePerPolicy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
