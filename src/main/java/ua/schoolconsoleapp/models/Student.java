package ua.schoolconsoleapp.models;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
//	private int id;
//	private Integer groupId;
//	private String firstName;
//	private String lastName;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	public Student() {
	}

	public Student(int id, Group group, String firstName, String lastName) {
		this.id = id;
		this.group = group;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Student(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getGroupId() {
	    return group != null ? group.getId() : null;
	}
	
	@Override
	public String toString() {
		return "Student [id=" + id + ", group=" + (group != null ? group.getName() : "None") + ", firstName="
				+ firstName + ", lastName=" + lastName + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, id, group);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		return id == other.id && Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(group, other.group);
	}

}
