package ua.schoolconsoleapp.models;

import java.util.Objects;
import jakarta.persistence.*;

@Entity  
@Table(name = "courses")
public class Course {
//	private int id;
//	private String name;
//	private String description;
	
	@Id  // Первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Автоинкремент
    private int id;

    @Column(name = "course_name", nullable = false, unique = true)  // Имя колонки в БД
    private String name;

    @Column(name = "course_description")  // Имя колонки в БД
    private String description;

    public Course() {}	
	

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
