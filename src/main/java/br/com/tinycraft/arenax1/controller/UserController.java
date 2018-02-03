package br.com.tinycraft.arenax1.controller;

import br.com.tinycraft.arenax1.entity.User;
import br.com.tinycraft.arenax1.entity.itf.ArenaStatus;
import br.com.tinycraft.arenax1.exception.DataBaseException;
import br.com.tinycraft.arenax1.service.UserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class UserController implements Runnable {

    private final List<User> logged;
    private final List<User> ranking;
    private final UserService userService;
    private final Logger logger;

    public UserController(UserService userService, Logger logger) {
        this.userService = userService;
        this.logger = logger;
        this.logged = new ArrayList<>();
        this.ranking = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerJoin(player);
        }

        updateRanking();
    }

    public void playerJoin(Player player) {
        User user;
        try {
            user = userService.findOrCreate(player.getUniqueId());
        } catch (DataBaseException e) {
            logger.warning("Could not load " + player.getName() + " from database");
            user = new User();
            user.setUuid(player.getUniqueId());
            user.setLastName(player.getName());
        }
        user.setLastName(player.getName());
        logged.add(user);
    }

    public void playerLeave(Player player) {
        User user = findOne(player.getUniqueId());

        if (user == null) {
            logger.warning("Something goes wrong? " + player.getName());
            return;
        }

        try {
            userService.save(user);
        } catch (DataBaseException e) {
            logger.warning("Could not save user " + user.getLastName());
        }

        logged.remove(user);
    }

    public void updateByArenaStatus(ArenaStatus arenaStatus) {
        if (arenaStatus.isTie()) return;

        User winner = findOne(arenaStatus.getWinner().getUniqueId());
        User loser = findOne(arenaStatus.getLoser().getUniqueId());

        winner.setWins(winner.getWins() + 1);
        loser.setLoses(loser.getLoses() + 1);
    }

    public User findOne(UUID uuid) {
        for (User user : logged) {
            if (user.getUuid().equals(uuid)) return user;
        }

        return null;
    }

    public void updateUsers() {
        for (User user : logged)
            if (user.isSave())
                try {
                    userService.save(user);
                } catch (DataBaseException e) {
                    e.printStackTrace();
                }
    }

    private void updateRanking() {
        this.ranking.clear();
        this.ranking.addAll(userService.ranking());
    }

    public List<User> getRanking() {
        return ranking;
    }

    public UserService getUserService() {
        return userService;
    }

    @Override
    public void run() {
        updateUsers();
        updateRanking();
    }
}
