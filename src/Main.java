import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Main {
	    
	class Person{
		private String name;
		private int age;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		
		public Person() {
			this.name = "";
			this.age = 0;
		}
		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}
		@Override
		public String toString() {
			return "Person [name=" + name + ", age=" + age + "]";
		}
	
		
		
	}
	
		public Main() {
			
	        try {
	        	
	        	List<Person> Persons=List.of(new Person("Ali", 10),new Person("Abdessamad", 19),new Person("Hamza", 20));
				XmlApi<Person> xmlapi = new XmlApi(Person.class);
	        	
				xmlapi.save(Persons, "persons.xml");
				
				
				List<Person> Persons2 =xmlapi.load(Person.class, "persons.xml");
				for(Person p:Persons) {
					p.toString();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		 public static void main(String[] args) {
			new Main();
		}
}
