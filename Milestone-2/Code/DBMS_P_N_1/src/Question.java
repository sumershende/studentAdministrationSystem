import java.util.List;

enum QuestionType{
	Fixed, Parameterized;
}

class Question {

	// Fields shown to student for the attempt before deadline:
	private String hint, text;
	// Fields shown to student for the attempt after deadline:
	private String detailedSolution;
	
	
	// Fields only present while attempting a question:
	private List<String> options;
	private int correctChoice;
	
	
	// Fields not shown to student:
	private String topicName;
	private int difficultyLevel, topicId, id, combinationNumber;
	
	private QuestionType questionType;
	
	private String[][] parameterValues, correctAnswers;
	
	private String[] incorrectAnswers;
	
	public Question(String text, String hint, int id){
		this(text, hint, null, -1, -1, id, null, null, null, null, null, null, 
				-1, -1);
	}
	
	public Question(String text, String hint, String detailedSolution){
		this(text, hint, null, -1, -1, -1, null, detailedSolution, null, null, 
				null, null, -1, -1);
	}
	
	public Question(List<String> options, short correctChoice){
		this(null, null, null, -1, -1, -1, null, null, null, null, null, options, 
				correctChoice, -1);
	}
	
	public Question(String text, String hint, String topicName, int difficultyLevel, 
			int topicId, int id, QuestionType questionType, String detailedSolution, 
			String[][] parameterValues, String[][] correctAnswers, 
			String[] incorrectAnswers){
		this(text, hint, topicName, difficultyLevel, topicId, 
				id, questionType, detailedSolution, parameterValues, 
				correctAnswers, incorrectAnswers, null, -1, -1);
	}
	
	public Question(String text, String hint, String topicName, int difficultyLevel, 
			int topicId, int id, QuestionType questionType, String detailedSolution, 
			String[][] parameterValues, String[][] correctAnswers, 
			String[] incorrectAnswers, List<String> options, int correctChoice, int combinationNumber) {
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
		this.correctAnswers = correctAnswers;
		this.incorrectAnswers = incorrectAnswers;
		this.options = options;
		this.correctChoice = correctChoice;
		this.combinationNumber = combinationNumber;
	}
	
	public Question() {
		// TODO Auto-generated constructor stub
	}
	public void print() {
		System.out.println(""+text+difficultyLevel+topicId+id+hint+topicName+questionType+detailedSolution+parameterValues+correctAnswers+this.incorrectAnswers+this.options+this.correctChoice);
	}
	public void setText(String text) {
		this.text = text;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public void setDetailedSolution(String detailedSolution) {
		this.detailedSolution = detailedSolution;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public void setCorrectChoice(int correctChoice) {
		this.correctChoice = correctChoice;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public void setDifficultyLevel(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public void setParameterValues(String[][] parameterValues) {
		this.parameterValues = parameterValues;
	}

	public void setCorrectAnswers(String[][] correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public void setIncorrectAnswers(String[] incorrectAnswers) {
		this.incorrectAnswers = incorrectAnswers;
	}

	public String getText() {
		return text;
	}

	public int getCombinationNumber() {
		return combinationNumber;
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

	public List<String> getOptions() {
		return options;
	}

	public int getCorrectChoice() {
		return correctChoice;
	}

	public String[][] getCorrectAnswers() {
		return correctAnswers;
	}

	public String[][] getAnswers() {
		return correctAnswers;
	}

	public boolean hasHint(){
		return hint != null;
	}
}
