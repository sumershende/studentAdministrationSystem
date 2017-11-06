import java.util.List;

class StudentHWAttempt {
	// Fields required for overview: 
	private final double score, maxScore;
	private final String submissionDateTime;
	
	// Fields required for details:
	private final List<Question> questions;
	private final List<Boolean> wasCorrectlyAnswered;
	private final int pointsPerCorrectAnswer, pointsPerIncorrectAnswer; 
	private final boolean hasDeadlinePassed;
	
	private int exerciseId;
	
	public StudentHWAttempt(double score, String submissionDateTime, int exerciseId, double maxScore){
		this(score, submissionDateTime, null, null, maxScore, -1, -1, false, exerciseId);
	}
	
	public StudentHWAttempt(double score, String submissionDateTime, double maxScore, int pointsPerCorrectAnswer, int pointsPerIncorrectAnswer, boolean isDeadlinePassed, int exerciseId){
		this(score, submissionDateTime, null, null, maxScore, pointsPerCorrectAnswer, pointsPerIncorrectAnswer, isDeadlinePassed, exerciseId);
	}
	
	public StudentHWAttempt(double score, String submissionDateTime, List<Question> questions, List<Boolean> wasCorrectlyAnswered, double maxScore, int pointsPerCorrectAnswer, int pointsPerIncorrectAnswer, boolean isDeadlinePassed, int exerciseId){
		this.score = score;
		this.submissionDateTime = submissionDateTime;
		this.questions = questions;
		this.wasCorrectlyAnswered = wasCorrectlyAnswered;
		this.maxScore = maxScore;
		this.pointsPerCorrectAnswer = pointsPerCorrectAnswer;
		this.pointsPerIncorrectAnswer = pointsPerIncorrectAnswer;
		this.hasDeadlinePassed = isDeadlinePassed;
		this.exerciseId = exerciseId;
	}

	public double getScore() {
		return score;
	}

	public String getSubmissionDateTime() {
		return submissionDateTime;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public List<Boolean> getWasCorrectlyAnswered() {
		return wasCorrectlyAnswered;
	}

	public int getPointsPerCorrectAnswer() {
		return pointsPerCorrectAnswer;
	}

	public int getExerciseId() {
		return exerciseId;
	}

	public boolean hasDeadlinePassed() {
		return hasDeadlinePassed;
	}

	public int getPointsPerIncorrectAnswer() {
		return pointsPerIncorrectAnswer;
	}

	public double getMaxScore() {
		return maxScore;
	}
}
