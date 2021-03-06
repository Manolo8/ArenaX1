package br.com.tinycraft.arenax1.arena;

import br.com.tinycraft.arenax1.ArenaX1;

import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;

/**
 *
 * @author AB
 */
public class ArenaManager
{

    private final ArenaX1 plugin;
    private final List<Arena> arenas;
    private Location arenaLobby;

    public ArenaManager(ArenaX1 plugin, List<Arena> arenas, Location lobby)
    {
        this.plugin = plugin;
        this.arenas = arenas;
        this.arenaLobby = lobby;
    }

    public boolean containsArena(String name)
    {
        Iterator<Arena> i = arenas.iterator();

        while (i.hasNext())
        {
            Arena arena = i.next();

            if (arena.getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    public Location getArenaLobby()
    {
        return arenaLobby;
    }
    
    public void setArenaLobby(Location lobby)
    {
        this.arenaLobby = lobby;
    }

    public List<Arena> getArenas()
    {
        return this.arenas;
    }

    public Arena getArena(String name)
    {
        Iterator<Arena> i = arenas.iterator();

        while (i.hasNext())
        {
            Arena arena = i.next();

            if (arena.getName().equalsIgnoreCase(name))
            {
                return arena;
            }
        }
        return null;
    }

    public Arena getAvailableArena()
    {
        Iterator<Arena> i = arenas.iterator();

        while (i.hasNext())
        {
            Arena arena = i.next();

            if (!arena.isOcurring() && arena.isCompleted())
            {
                return arena;
            }
        }

        return null;
    }

    public boolean createArena(String name, String world)
    {
        if (containsArena(name))
        {
            return false;
        }

        this.arenas.add(new Arena(name, world, null, null));
        return true;
    }

    public boolean removeArena(String name)
    {

        Iterator<Arena> i = arenas.iterator();

        while (i.hasNext())
        {
            Arena arena = i.next();

            if (arena.getName().equalsIgnoreCase(name))
            {
                i.remove();
                return true;
            }
        }
        return false;
    }

}
