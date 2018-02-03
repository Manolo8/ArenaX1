package br.com.tinycraft.arenax1.repository;

import br.com.tinycraft.arenax1.entity.User;
import br.com.tinycraft.arenax1.exception.DataBaseException;

import java.util.List;
import java.util.UUID;

public interface UserDao {

    List<User> findAll() throws DataBaseException;

    User findOne(UUID uuid) throws DataBaseException;

    void create(User user) throws DataBaseException;

    void save(User user) throws DataBaseException;

    void delete(UUID uuid) throws DataBaseException;

    List<User> ranking();
}
