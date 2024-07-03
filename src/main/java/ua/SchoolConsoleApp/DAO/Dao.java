package ua.SchoolConsoleApp.DAO;

import java.sql.SQLException;
import java.util.List;

public interface Dao<Entity> {
    void create(Entity entity) throws SQLException;
    void update(Entity entity) throws SQLException;
    void delete(int id) throws SQLException;
    Entity read(int id) throws SQLException;
    List<Entity> getAll() throws SQLException;
}
