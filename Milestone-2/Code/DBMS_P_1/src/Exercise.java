import java.util.HashSet;

enum ExerciseMode{
	Adaptive, Random;
}

enum ScroingPolicy{
	Latest, Maximum, Average;
}

class Exercise {
	
	private ExerciseMode exerciseMode;
	
	private final String startDate, endDate, name;
	private int numQuestions, numRetries, id, topicId;

	private final ScroingPolicy scroingPolicy;
	
	private final HashSet<Integer> qIds;

	private final int pointsAwardedPerCorrectAnswer, pointsDeductedPerIncorrectAnswer;
	
	public Exercise(ExerciseMode exerciseMode, ScroingPolicy scroingPolicy, String name, 
			String startDate, String endDate, int numQuestions, int numRetries, int id, 
			HashSet<Integer> qIds, int pointsAwardedPerCorrectAnswer, 
			int pointsDeductedPerIncorrectAnswer, int topicId){
		this.exerciseMode = exerciseMode;
		this.scroingPolicy = scroingPolicy;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.numQuestions = numQuestions;
		this.numRetries = numRetries;
		this.id = id;
		this.qIds = qIds;
		this.pointsAwardedPerCorrectAnswer = pointsAwardedPerCorrectAnswer;
		this.pointsDeductedPerIncorrectAnswer = pointsDeductedPerIncorrectAnswer;
		this.topicId = topicId;
	}

	public ExerciseMode getExerciseMode() {
		return exerciseMode;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getName() {
		return name;
	}

	public int getNumQuestions() {
		return numQuestions;
	}

	public HashSet<Integer> getqIds() {
		return qIds;
	}

	public int getPointsAwardedPerCorrectAnswer() {
		return pointsAwardedPerCorrectAnswer;
	}

	public int getPointsDeductedPerIncorrectAnswer() {
		return pointsDeductedPerIncorrectAnswer;
	}

	public int getTopicId() {
		return topicId;
	}

	public int getNumRetries() {
		return numRetries;
	}

	public ScroingPolicy getScroingPolicy() {
		return scroingPolicy;
	}

	public HashSet<Integer> getQIds() {
		return qIds;
	}

	public int getId() {
		return id;
	}	
}