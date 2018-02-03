package br.com.tinycraft.arenax1.service;


import br.com.tinycraft.arenax1.entity.User;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import br.com.tinycraft.arenax1.repository.UserDao;

import java.util.List;
import java.util.UUID;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAll() throws DataBaseException {
        return userDao.findAll();
    }

    public User findOrCreate(UUID uuid) throws DataBaseException {
        User user = userDao.findOne(uuid);
        if (user == null) {
            user = new User();
            user.setUuid(uuid);
            save(user);
        }
        return user;
    }

    public void save(User user) throws DataBaseException {
        userDao.save(user);
    }

    public void delete(UUID uuid) throws DataBaseException {
        userDao.delete(uuid);
    }

    public List<User> ranking() {
        return userDao.ranking();
    }
}
