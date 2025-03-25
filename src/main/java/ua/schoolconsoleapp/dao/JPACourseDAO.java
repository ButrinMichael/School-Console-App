package ua.schoolconsoleapp.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ua.schoolconsoleapp.models.Course;
import ua.schoolconsoleapp.models.Student;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public class JPACourseDAO implements Dao<Course> {
	
	private static final Logger logger = LoggerFactory.getLogger(JPACourseDAO.class);
	@PersistenceContext
    private EntityManager em;

    @Override
    public void create(Course course) {
        logger.info("Creating course: {}", course.getName());
        em.persist(course);
    }

    @Override
    public Optional<Course> read(int id) {
        Course course = em.find(Course.class, id);
        return Optional.ofNullable(course);
    }

    @Override
    public void update(Course course) {
        logger.info("Updating course with ID: {}", course.getId());
        em.merge(course);
    }

    @Override
    public void delete(int id) {
        Course course = em.find(Course.class, id);
        if (course != null) {
            for (Student student : course.getStudents()) {
                student.getCourses().remove(course);
            }
            em.remove(course);
        }
    }

    @Override
    public List<Course> getAll() {
        return em.createQuery("SELECT c FROM Course c", Course.class).getResultList();
    }

    public Optional<Course> findByName(String name) {
        return em.createQuery("SELECT c FROM Course c WHERE c.name = :name", Course.class)
                 .setParameter("name", name)
                 .getResultStream()
                 .findFirst();
    }

    public List<Course> getCoursesByStudentId(int studentId) {
        return em.createQuery("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId", Course.class)
                 .setParameter("studentId", studentId)
                 .getResultList();
    }

    public void assignCourseToStudent(int courseId, int studentId) {
        Course course = em.find(Course.class, courseId);
        Student student = em.find(Student.class, studentId);
        if (course != null && student != null) {
            student.getCourses().add(course);
            course.getStudents().add(student);
        }
    }

    public boolean isStudentEnrolled(int studentId, int courseId) {
        Long count = em.createQuery(
            "SELECT COUNT(c) FROM Course c JOIN c.students s WHERE c.id = :courseId AND s.id = :studentId", Long.class)
            .setParameter("courseId", courseId)
            .setParameter("studentId", studentId)
            .getSingleResult();
        return count > 0;
    }
}