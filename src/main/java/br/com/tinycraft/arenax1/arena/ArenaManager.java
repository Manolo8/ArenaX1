package br.com.tinycraft.arenax1.arena;

import java.util.Iterator;
import java.util.List;

/**
 * @author AB
 */
public class ArenaManager {

    private final List<Arena> arenas;

    public ArenaManager(List<Arena> arenas) {
        this.arenas = arenas;
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
                return true;
            }
        }

        return false;
    }
}
