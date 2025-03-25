package ua.schoolconsoleapp.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "courses", schema = "school")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_id")
	private int id;

	@Column(name = "course_name", nullable = false, unique = true)
	private String name;

	@Column(name = "course_description")
	private String description;

	@ManyToMany(mappedBy = "courses")
	private Set<Student> students = new HashSet<>();

	public Set<Student> getStudents() {
		return students;
	}

	public Course() {
	}

	public Course(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Course(int courseId, String courseName) {
		this.id = courseId;
		this.name = courseName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addStudent(Student student) {
		students.add(student);
		student.getCourses().add(this);
	}

	public void removeStudent(Student student) {
		students.remove(student);
		student.getCourses().remove(this);
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		return Objects.equals(description, other.description) && id == other.id && Objects.equals(name, other.name);
	}

}
