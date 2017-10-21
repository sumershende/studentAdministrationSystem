enum QuestionType{
	Fixed, Parameterized;
}

class Question {
	
	private final String text, hint, topicName, detailedSolution;
	private final int difficultyLevel, topicId, id;
	
	private final QuestionType questionType;
	
	private final String[][] parameterValues;
	
	private final String[] incorrectAnswers, answers;
	
	public Question(String text, String hint, String topicName, int difficultyLevel, int topicId, int id, QuestionType questionType, String detailedSolution, String[][] parameterValues, String[] answers, String[] incorrectAnswers) {
		// TODO Auto-generated constructor stub
		this.text = text;
		this.difficultyLevel = difficultyLevel;
		this.topicId = topicId;
		this.id = id;
		this.hint = hint;
		this.topicName = topicName;
		this.questionType = questionType;
		this.detailedSolution = detailedSolution;
		this.parameterValues = parameterValues;
		this.answers = answers;
		this.incorrectAnswers = incorrectAnswers;
	}

	public String getText() {
		return text;
	}

	public String getHint() {
		return hint;
	}

	public String getTopicName() {
		return topicName;
	}

	public int getDifficultyLevel() {
		return difficultyLevel;
	}

	public int getTopicId() {
		return topicId;
	}

	public int getId() {
		return id;
	}
	
	public QuestionType getQuestionType() {
		return questionType;
	}

	public String getDetailedSolution() {
		return detailedSolution;
	}

	public String[][] getParameterValues() {
		return parameterValues;
	}

	public String[] getIncorrectAnswers() {
		return incorrectAnswers;
	}

	public String[] getAnswers() {
		return answers;
	}

	public boolean hasHint(){
		return hint != null;
	}
}
