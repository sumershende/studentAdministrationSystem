import java.util.List;

class StudentHWAttempt {
	// Fields required for overview: 
	private final int score, maxScore;
	private final String submissionDateTime;
	
	// Fields required for details:
	private final List<Question> questions;
	private final List<Boolean> wasCorrectlyAnswered;
	private final int pointsPerCorrectAnswer, pointsPerIncorrectAnswer; 
	private final boolean hasDeadlinePassed;
	
	public StudentHWAttempt(int score, String submissionDateTime, int maxScore, int pointsPerCorrectAnswer, int pointsPerIncorrectAnswer, boolean isDeadlinePassed){
		this(score, submissionDateTime, null, null, maxScore, pointsPerCorrectAnswer, pointsPerIncorrectAnswer, isDeadlinePassed);
	}
	
	public StudentHWAttempt(int score, String submissionDateTime, List<Question> questions, List<Boolean> wasCorrectlyAnswered, int maxScore, int pointsPerCorrectAnswer, int pointsPerIncorrectAnswer, boolean isDeadlinePassed){
		this.score = score;
		this.submissionDateTime = submissionDateTime;
		this.questions = questions;
		this.wasCorrectlyAnswered = wasCorrectlyAnswered;
		this.maxScore = maxScore;
		this.pointsPerCorrectAnswer = pointsPerCorrectAnswer;
		this.pointsPerIncorrectAnswer = pointsPerIncorrectAnswer;
		this.hasDeadlinePassed = isDeadlinePassed;
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
