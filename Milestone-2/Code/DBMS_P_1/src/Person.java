
class Person {
	private final String name;
	private final int id;
	
	public String getName() {
		return name;
	}

/*	public String getLastName() {
		return lastName;
	}
*/
	public int getId() {
		return id;
	}

	public Person(int id){
		this(null, id);
	}
	
	public Person(String name, int id) {
		this.name = name;
		this.id = id;
	}
}
