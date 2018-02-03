package br.com.tinycraft.arenax1.service;

import br.com.tinycraft.arenax1.entity.Arena;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import br.com.tinycraft.arenax1.repository.ArenaDao;

import java.util.List;
import java.util.logging.Logger;

public class ArenaService {

    private final ArenaDao arenaDao;

    public ArenaService(ArenaDao arenaDao) {
        this.arenaDao = arenaDao;
    }

    public List<Arena> findAll() throws DataBaseException {
        return arenaDao.findAll();
    }

    public Arena findOne(String name) throws DataBaseException {
        return arenaDao.findOne(name);
    }

    public void save(Arena arena) throws DataBaseException {
        arenaDao.save(arena);
    }

    public void save(List<Arena> arenas) throws DataBaseException {
        arenaDao.save(arenas);
    }


    public void delete(String name) throws DataBaseException {
        arenaDao.delete(name);
    }

    public void saveToBase() throws DataBaseException {
        arenaDao.close();
    }
}