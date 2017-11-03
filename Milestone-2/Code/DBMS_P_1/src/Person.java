
class Person {
	private final String name;
	private final String id;
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}

	public Person(String id){
		this(null, id);
	}
	
	public Person(String name, String id) {
		this.name = name;
		this.id = id;
	}
}
