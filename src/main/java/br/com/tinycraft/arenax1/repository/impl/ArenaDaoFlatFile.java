package br.com.tinycraft.arenax1.repository.impl;

import br.com.tinycraft.arenax1.entity.Arena;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import br.com.tinycraft.arenax1.repository.ArenaDao;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author AB
 */
public class ArenaDaoFlatFile implements ArenaDao {

    private final FileConfiguration config;
    private final File arenas;

    public ArenaDaoFlatFile(File dataFolder) {
        arenas = new File(dataFolder, "arenas.yml");
        config = YamlConfiguration.loadConfiguration(arenas);
    }

    @Override
    public List<Arena> findAll() throws DataBaseException {
        List<Arena> arenas = new ArrayList<>();

        if (config.isConfigurationSection("Arenas")) {
            Set<String> names = config.getConfigurationSection("Arenas").getKeys(false);

            for (String string : names) {
                try {
                    arenas.add(findOne(string));
                } catch (Exception e) {
                    throw new DataBaseException("Error on load arena " + string);
                }
            }
        }
        return arenas;
    }

    @Override
    public Arena findOne(String name) throws DataBaseException {
        String world = config.getString("Arenas." + name + ".world");
        String pos1Data = config.getString("Arenas." + name + ".pos1");
        String pos2Data = config.getString("Arenas." + name + ".pos2");
        String lobbyData = config.getString("Arenas." + name + ".lobby");
        String boxData = config.getString("Arenas." + name + ".box");

        Location pos1 = loadFromString(pos1Data, world);
        Location pos2 = loadFromString(pos2Data, world);
        Location lobby = loadFromString(lobbyData, world);
        Location box = loadFromString(boxData, world);

        return new Arena(name, world, pos1, pos2, lobby, box);
    }

    @Override
    public Arena create() throws DataBaseException {
        return new Arena();
    }

    @Override
    public void save(Arena arena) throws DataBaseException {
        config.set("Arenas." + arena.getName() + ".world", arena.getWorld());
        config.set("Arenas." + arena.getName() + ".pos1", saveFromLocation(arena.getPos1()));
        config.set("Arenas." + arena.getName() + ".pos2", saveFromLocation(arena.getPos2()));
        config.set("Arenas." + arena.getName() + ".lobby", saveFromLocation(arena.getLobby()));
        config.set("Arenas." + arena.getName() + ".box", saveFromLocation(arena.getBox()));
    }

    @Override
    public void save(List<Arena> arenas) throws DataBaseException {
        for (Arena arena : arenas) {
            save(arena);
        }
    }

    @Override
    public void delete(String name) throws DataBaseException {
        config.set("Arenas." + name, null);
    }

    @Override
    public void close() throws DataBaseException {
        try {
            config.save(arenas);
        } catch (IOException e) {
            throw new DataBaseException("Could not save arenas to the database");
        }
    }

    private String saveFromLocation(Location f) {
        if (f == null) return null;
        return Math.round(f.getX())
                + " " + Math.round(f.getY())
                + " " + Math.round(f.getZ())
                + " " + Math.round(f.getYaw())
                + " " + Math.round(f.getPitch());
    }

    private Location loadFromString(String string, String world) {
        if (string == null || string.equals("null")) return null;
        String loc[] = string.split(" ");
        return new Location(Bukkit.getWorld(world),
                Double.parseDouble(loc[0]),
                Double.parseDouble(loc[1]),
                Double.parseDouble(loc[2]),
                Float.parseFloat(loc[3]),
                Float.parseFloat(loc[4]));
    }

}
