package br.com.tinycraft.arenax1.data;

import br.com.tinycraft.arenax1.arena.Arena;
import java.util.List;

/**
 *
 * @author Willian
 */
public interface Data
{

    Arena loadArena(String arena);

    List<Arena> loadAllArena();

    void saveArena(Arena arena);

    void saveAllArena(List<Arena> arenas);
    
    void saveToBase() throws Exception;
}
