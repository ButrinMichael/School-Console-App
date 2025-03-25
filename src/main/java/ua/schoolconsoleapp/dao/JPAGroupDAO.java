package ua.schoolconsoleapp.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ua.schoolconsoleapp.models.Group;
import ua.schoolconsoleapp.models.Student;

import java.util.List;
import java.util.Optional;

@Repository
public class JPAGroupDAO implements Dao<Group> {

	private static final Logger logger = LoggerFactory.getLogger(JPAGroupDAO.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public void create(Group group) {
		logger.info("Creating group with name: {}", group.getName());
		entityManager.persist(group);
		logger.info("Group created: {}", group);

	}

	@Override
	@Transactional
	public void update(Group group) {
		logger.info("Updating group with ID {}: {}", group.getId(), group);
		entityManager.merge(group);
		logger.info("Group updated: {}", group);
	}

	@Override
	@Transactional
	public void delete(int id) {
		logger.info("Deleting group with ID: {}", id);
		Group group = entityManager.find(Group.class, id);
		if (group != null) {
			List<Student> students = group.getStudents();
			if (students != null) {
				for (Student student : students) {
					student.setGroup(null);
				}
			}
			entityManager.remove(group);
			logger.info("Group deleted: {}", group);
		} else {
			logger.warn("Group with ID {} not found, cannot delete", id);
		}
	}

	@Override
	public Optional<Group> read(int id) {
		logger.info("Reading group with ID: {}", id);
		Group group = entityManager.find(Group.class, id);
		if (group != null) {
			logger.info("Group found: {}", group);
		} else {
			logger.warn("Group with ID {} not found", id);
		}
		return Optional.ofNullable(group);
	}

	public List<Group> getAll() {
		logger.info("Fetching all groups");
		List<Group> groups = entityManager.createQuery("SELECT g FROM Group g", Group.class).getResultList();
		logger.info("Found {} groups", groups.size());
		return groups;
	}

}
