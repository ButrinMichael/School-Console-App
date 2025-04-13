package ua.schoolconsoleapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.schoolconsoleapp.models.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer>{
	
	@Query("SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.firstName = :firstName AND s.lastName = :lastName")
	Optional<Student> findWithCoursesByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
	
    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.name = :courseName")
    List<Student> findStudentsByCourseName(@Param("courseName") String courseName);
    
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses")
    List<Student> findAllWithCourses();
}
