package ua.schoolconsoleapp;

import java.util.Objects;

public class Student {
	private int id;
	private Integer groupId;
	private String firstName;
	private String lastName;

	public Student(int id, Integer groupId, String firstName, String lastName) {
		this.id = id;
		this.groupId = groupId;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Student(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return groupId;
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

	@Override
	public String toString() {
		return "Student [id=" + id + ", groupId=" + groupId + ", firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, groupId, id, lastName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		return Objects.equals(firstName, other.firstName) && groupId == other.groupId && id == other.id
				&& Objects.equals(lastName, other.lastName);
	}

}
