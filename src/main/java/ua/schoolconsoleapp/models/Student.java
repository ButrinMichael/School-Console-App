package ua.schoolconsoleapp.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "students", schema = "school")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	@ManyToMany
	@JoinTable(name = "students_courses", schema = "school", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))

	private Set<Course> courses = new HashSet<>();

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

	public void addCourse(Course course) {
		courses.add(course);
		course.getStudents().add(this);
	}

	public void removeCourse(Course course) {
		courses.remove(course);
		course.getStudents().remove(this);
	}

	public Group getGroup() {
		return group;
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

	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
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
	    return Objects.hash(id, firstName, lastName);
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    Student student = (Student) o;
	    return id == student.id &&
	           Objects.equals(firstName, student.firstName) &&
	           Objects.equals(lastName, student.lastName);
	}

}
