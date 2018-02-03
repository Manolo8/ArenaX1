package br.com.tinycraft.arenax1.entity.itf;

import org.bukkit.entity.Player;

public interface ArenaStatus {

    Player getWinner();

    Player getLoser();

    boolean isTie();
}
