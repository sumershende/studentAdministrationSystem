class Course {
	private final String courseId, courseName, startDate, endDate;
	private final String[] TAs;

	public Course(String courseId, String courseName, String startDate, String endDate, String[] TAs){
		this.courseId = courseId;
		this.courseName = courseName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.TAs = TAs;
	}
	
	public String getCourseName(){
		return courseName;
	}
	
	public String getCourseId() {
		return courseId;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String[] getTAs() {
		return TAs;
	}
}
