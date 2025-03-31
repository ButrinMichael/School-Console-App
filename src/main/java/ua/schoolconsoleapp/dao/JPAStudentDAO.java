package ua.schoolconsoleapp.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ua.schoolconsoleapp.models.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JPAStudentDAO implements Dao<Student>{

	private static final Logger logger = LoggerFactory.getLogger(JPAStudentDAO.class);
	
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void create(Student student) {
        logger.info("Creating student: {} {}", student.getFirstName(), student.getLastName());
        entityManager.persist(student);
        logger.info("Student created with ID: {}", student.getId());
    }

    @Override
    public void update(Student student) {
        logger.info("Updating student with ID: {}", student.getId());
        entityManager.merge(student);
        logger.info("Student updated: {}", student);
    }

    @Override
    public void delete(int id) {
        logger.info("Deleting student with ID: {}", id);
        Student student = entityManager.find(Student.class, id);
        if (student != null) {
            entityManager.remove(student);
            logger.info("Student with ID {} deleted.", id);
        } else {
            logger.warn("Student with ID {} not found for deletion.", id);
        }
    }

    @Override
    public Optional<Student> read(int id) {
        logger.info("Reading student with ID: {}", id);
        Student student = entityManager.find(Student.class, id);
        if (student != null) {
            logger.info("Student found: {}", student);
        } else {
            logger.warn("Student with ID {} not found.", id);
        }
        return Optional.ofNullable(student);
    }

    @Override
    public List<Student> getAll() {
        logger.info("Fetching all students");
        List<Student> students = entityManager
                .createQuery("SELECT s FROM Student s", Student.class)
                .getResultList();
        logger.info("Found {} students", students.size());
        return students;
    }

    public List<Student> getAllWithCourses() {
        TypedQuery<Student> query = entityManager.createQuery(
            "SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses", Student.class);
        return query.getResultList();
    }
    
    public List<Student> getStudentsByCourseName(String courseName) {
        logger.info("Fetching students for course: {}", courseName);
        List<Student> students = entityManager
                .createQuery("SELECT s FROM Student s JOIN s.courses c WHERE c.name = :courseName", Student.class)
                .setParameter("courseName", courseName)
                .getResultList();
        logger.info("Found {} students for course '{}'", students.size(), courseName);
        return students;
    }
    
    public Optional<Student> findByNameAndLastName(String firstName, String lastName) {
        logger.info("Searching for student by name: {} {}", firstName, lastName);
        try {
            Optional<Student> student = entityManager
                    .createQuery("SELECT s FROM Student s WHERE s.firstName = :firstName AND s.lastName = :lastName", Student.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .getResultStream()
                    .findFirst();
            if (student.isPresent()) {
                logger.info("Student found: {}", student.get());
            } else {
                logger.warn("No student found with name: {} {}", firstName, lastName);
            }
            return student;
        } catch (Exception e) {
            logger.error("Error while searching for student {} {}: {}", firstName, lastName, e.getMessage(), e);
            return Optional.empty();
        }
    }
}