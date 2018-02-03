package br.com.tinycraft.arenax1.data;

import br.com.tinycraft.arenax1.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author AB
 */
public class FlatFileData implements Data {

    private final FileConfiguration config;
    private final File arenas;

    public FlatFileData(File dataFolder) {
        arenas = new File(dataFolder, "arenas.yml");
        config = YamlConfiguration.loadConfiguration(arenas);
    }

    @Override
    public void saveArena(Arena arena) {
        config.set("Arenas." + arena.getName() + ".world", arena.getWorld());
        config.set("Arenas." + arena.getName() + ".pos1", saveFromLocation(arena.getPos1()));
        config.set("Arenas." + arena.getName() + ".pos2", saveFromLocation(arena.getPos2()));
        config.set("Arenas." + arena.getName() + ".lobby", saveFromLocation(arena.getLobby()));
        config.set("Arenas." + arena.getName() + ".box", saveFromLocation(arena.getBox()));
    }

    @Override
    public void saveAllArena(List<Arena> arenas) {
        config.set("Arenas", null);
        for (Arena arena : arenas) {
            saveArena(arena);
        }
    }

    @Override
    public Arena loadArena(String name) {
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
    public List<Arena> loadAllArena() {
        List<Arena> arenas = new ArrayList<>();

        if (config.isConfigurationSection("Arenas")) {
            Set<String> names = config.getConfigurationSection("Arenas").getKeys(false);

            for (String string : names) {
                try {
                    arenas.add(loadArena(string));
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Error on load arena {0}", string);
                }
            }
        }
        return arenas;
    }

    @Override
    public void saveToBase() throws Exception {
        config.save(arenas);
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
