import java.sql.Date;
import java.util.List;

enum CourseLevel{
	Grad, UnderGrad;
}

class Course {
	private final String courseId, courseName;
	private final Date startDate, endDate;
	private final List<Person> TAs;
	private final List<Topic> topics;
	private final List<Person> enrolledStudents;
	
	private final CourseLevel courseLevel;
	
	private final int maxStudentsAllowed;
	
	public Course(String courseId, String courseName, Date startDate, Date endDate, 
			List<Person> TAs, List<Topic> topics, List<Person> studentsEnrolled, 
			CourseLevel courseLevel, int maxStudentsAllowed){
		this.courseId = courseId;
		this.courseName = courseName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.TAs = TAs;
		this.topics = topics;
		this.enrolledStudents = studentsEnrolled;
		this.courseLevel = courseLevel;
		this.maxStudentsAllowed = maxStudentsAllowed;
	}
	
	public String getCourseName(){
		return courseName;
	}
	
	public String getCourseId() {
		return courseId;
	}

	public List<Person> getEnrolledStudents() {
		return enrolledStudents;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public int getMaxStudentsAllowed() {
		return maxStudentsAllowed;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public List<Person> getTAs() {
		return TAs;
	}
	
	public CourseLevel getCourseLevel() {
		return courseLevel;
	}

	public boolean hasTAs(){
		return TAs != null && TAs.size() != 0;
	}
	
	public boolean hasTopics(){
		return topics != null && topics.size() != 0;
	}
}
