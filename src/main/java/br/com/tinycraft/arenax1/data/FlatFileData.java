package br.com.tinycraft.arenax1.data;

import br.com.tinycraft.arenax1.arena.Arena;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author AB
 */
public class FlatFileData implements Data
{

    private final FileConfiguration config;
    private final File arenas;

    public FlatFileData(File dataFolder)
    {
        arenas = new File(dataFolder, "arenas.yml");
        config = YamlConfiguration.loadConfiguration(arenas);
    }

    @Override
    public void saveArena(Arena arena)
    {
        if (!arena.isCompleted())
        {
            return;
        }
        config.set("Arenas." + arena.getName() + ".world", arena.getWorld());
        Location f = arena.getPos1();
        config.set("Arenas." + arena.getName() + ".pos1", Math.round(f.getX())
                + " " + Math.round(f.getY())
                + " " + Math.round(f.getZ())
                + " " + Math.round(f.getYaw())
                + " " + Math.round(f.getPitch()));
        f = arena.getPos2();
        config.set("Arenas." + arena.getName() + ".pos2", Math.round(f.getX())
                + " " + Math.round(f.getY())
                + " " + Math.round(f.getZ())
                + " " + Math.round(f.getYaw())
                + " " + Math.round(f.getPitch()));
    }

    @Override
    public void saveAllArena(List<Arena> arenas)
    {
        config.set("Arenas", null);
        for (Arena arena : arenas)
        {
            saveArena(arena);
        }
    }

    @Override
    public Arena loadArena(String name)
    {
        String world = config.getString("Arenas." + name + ".world");
        String pos1[] = config.getString("Arenas." + name + ".pos1").split(" ");
        String pos2[] = config.getString("Arenas." + name + ".pos2").split(" ");

        Location ps1 = new Location(Bukkit.getWorld(world),
                Double.parseDouble(pos1[0]),
                Double.parseDouble(pos1[1]),
                Double.parseDouble(pos1[2]),
                Float.parseFloat(pos1[3]),
                Float.parseFloat(pos1[4]));
        Location ps2 = new Location(Bukkit.getWorld(world),
                Double.parseDouble(pos2[0]),
                Double.parseDouble(pos2[1]),
                Double.parseDouble(pos2[2]),
                Float.parseFloat(pos2[3]),
                Float.parseFloat(pos2[4]));

        Arena arena = new Arena(name, world, ps1, ps2);

        return arena;
    }

    @Override
    public List<Arena> loadAllArena()
    {
        List<Arena> arenas = new ArrayList();

        if (config.isConfigurationSection("Arenas"))
        {
            Set<String> names = config.getConfigurationSection("Arenas").getKeys(false);

            for (String string : names)
            {
                try
                {
                    arenas.add(loadArena(string));
                } catch (Exception e)
                {
                    Bukkit.getLogger().warning("Error on load arena " + string);
                }
            }
        }
        return arenas;
    }

    @Override
    public void saveToBase() throws Exception
    {
        config.save(arenas);
    }

    @Override
    public Location loadLobby()
    {
        if (config.contains("ArenaLobby"))
        {
            String lobby[] = config.getString("ArenaLobby").split(" ");
            return new Location(Bukkit.getWorld(lobby[5]),
                    Double.parseDouble(lobby[0]),
                    Double.parseDouble(lobby[1]),
                    Double.parseDouble(lobby[2]),
                    Float.parseFloat(lobby[3]),
                    Float.parseFloat(lobby[4]));
        } else
        {
            return null;
        }
    }

    @Override
    public void saveLobby(Location lobby)
    {
        if (lobby != null)
        {
            config.set("ArenaLobby", Math.round(lobby.getX())
                    + " " + Math.round(lobby.getY())
                    + " " + Math.round(lobby.getZ())
                    + " " + Math.round(lobby.getYaw())
                    + " " + Math.round(lobby.getPitch())
                    + " " + lobby.getWorld().getName());
        }
    }
}
