package ua.schoolconsoleapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.schoolconsoleapp.models.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    Optional<Group> findByName(String name);

    @Query("SELECT g FROM Group g JOIN g.students s GROUP BY g.id HAVING COUNT(s) <= :maxStudents")
    List<Group> findGroupsWithLessOrEqualStudents(long maxStudents);
}
