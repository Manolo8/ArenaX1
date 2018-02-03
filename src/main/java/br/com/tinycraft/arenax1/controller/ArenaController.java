package br.com.tinycraft.arenax1.controller;

import br.com.tinycraft.arenax1.entity.Arena;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import br.com.tinycraft.arenax1.service.ArenaService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author AB
 */
public class ArenaController {

    private List<Arena> arenas;
    private ArenaService arenaService;
    private Logger logger;

    public ArenaController(ArenaService arenaService, Logger logger) {
        try {
            this.arenaService = arenaService;
            this.arenas = arenaService.findAll();
            this.logger = logger;
        } catch (DataBaseException e) {
            this.arenas = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private boolean containsArena(String name) {
        return getArena(name) != null;
    }

    public List<Arena> getArenas() {
        return this.arenas;
    }

    public Arena getArena(String name) {
        for (Arena arena : arenas) if (arena.getName().equalsIgnoreCase(name)) return arena;
        return null;
    }

    public Arena getAvailableArena() {
        for (Arena arena : arenas) if (arena.isReady()) return arena;
        return null;
    }

    public boolean createArena(String name, String world) {
        if (containsArena(name)) return false;

        this.arenas.add(new Arena(name, world, null, null, null, null));

        return true;
    }

    public boolean removeArena(String name) {
        Iterator<Arena> i = arenas.iterator();

        while (i.hasNext()) {
            Arena arena = i.next();

            if (arena.getName().equalsIgnoreCase(name)) {
                i.remove();
                try {
                    arenaService.delete(name);
                } catch (DataBaseException e) {
                    logger.info("Error " + e.getMessage());
                }
                return true;
            }
        }

        return false;
    }

    public void saveAllArena() {
        try {
            arenaService.save(arenas);
        } catch (DataBaseException e) {
            logger.info("Error " + e.getMessage());
        }
    }
}
