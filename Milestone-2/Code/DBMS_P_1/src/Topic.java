
class Topic {
	private final int topicId;
	private final String topicName;
	
	public Topic(int topicId, String topicName){
		this.topicId = topicId;
		this.topicName = topicName;
	}

	public int getTopicId() {
		return topicId;
	}

	public String getTopicName() {
		return topicName;
	}
}
