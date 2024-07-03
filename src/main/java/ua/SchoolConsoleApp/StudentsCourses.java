package ua.SchoolConsoleApp;

import java.util.Objects;

public class StudentsCourses {
	private int studID;
	private int courseID;

	
	public StudentsCourses(int studID, int courseID) {
		this.studID = studID;
		this.courseID = courseID;
	}

	public int getStudID() {
		return studID;
	}

	public void setStudID(int studID) {
		this.studID = studID;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(courseID, studID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentsCourses other = (StudentsCourses) obj;
		return courseID == other.courseID && studID == other.studID;
	}

	@Override
	public String toString() {
		return "StudentsCourses [studID=" + studID + ", courseID=" + courseID + "]";
	}
}
