package ua.schoolconsoleapp.dao;

import org.springframework.stereotype.Repository;
import ua.schoolconsoleapp.models.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JPAStudentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(Student student) {
        entityManager.persist(student);
    }

    public void update(Student student) {
        entityManager.merge(student);
    }

    public void delete(int id) {
        Student student = entityManager.find(Student.class, id);
        if (student != null) {
            entityManager.remove(student);
        }
    }

    public Optional<Student> read(int id) {
        return Optional.ofNullable(entityManager.find(Student.class, id));
    }

    public List<Student> getAll() {
        return entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    public List<Student> getStudentsByCourseName(String courseName) {
        return entityManager.createQuery(
                "SELECT s FROM Student s JOIN s.courses c WHERE c.name = :courseName", Student.class)
                .setParameter("courseName", courseName)
                .getResultList();
    }
    public Optional<Student> findByNameAndLastName(String firstName, String lastName) {
        try {
            return entityManager.createQuery(
                    "SELECT s FROM Student s WHERE s.firstName = :firstName AND s.lastName = :lastName", Student.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .getResultStream()
                    .findFirst();  
        } catch (Exception e) {
            return Optional.empty(); 
        }
    }
}