package br.com.tinycraft.arenax1.repository;

import br.com.tinycraft.arenax1.entity.Arena;
import br.com.tinycraft.arenax1.exception.DataBaseException;

import java.util.List;

public interface ArenaDao {

    List<Arena> findAll() throws DataBaseException;

    Arena findOne(String name) throws DataBaseException;

    Arena create() throws DataBaseException;

    void save(Arena arena) throws DataBaseException;

    void save(List<Arena> arenas) throws DataBaseException;

    void delete(String name) throws DataBaseException;

    void close() throws DataBaseException;
}
