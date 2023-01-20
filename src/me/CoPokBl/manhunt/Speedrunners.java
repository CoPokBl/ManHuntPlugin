package me.CoPokBl.manhunt;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Speedrunners implements Listener {

    private final ArrayList<String> dead = new ArrayList<>();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (Main.plugin.getConfig().getStringList("hunters").contains(e.getPlayer().getName())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
                dead.add(e.getPlayer().getName());

                if (dead.size() == Bukkit.getOnlinePlayers().size() - Main.plugin.getConfig().getStringList("hunters").size()) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lHUNTERS WIN!!!"));

                    // Show title
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (Main.plugin.getConfig().getStringList("hunters").contains(player.getName())) {
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&6&lYou Win!"), ChatColor.translateAlternateColorCodes('&', "&kYou have hunted down all the speedrunners"), 10, 70, 20);
                        } else {
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lYou Lose :("), ChatColor.translateAlternateColorCodes('&', "&kYou were hunted down"), 10, 70, 20);
                        }
                    }
                }
            }
        }.runTaskLater(Main.plugin, 10L);

        if (!Main.plugin.getConfig().getBoolean("speedrunnersRespawn")) return;

        final int[] secondsPast = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                secondsPast[0] += 1;
                e.getPlayer().sendMessage(ChatColor.GOLD + "Respawning in " + (Main.plugin.getConfig().getInt("speedrunnerRespawnTime")-secondsPast[0]) + " seconds...");
                if (secondsPast[0] == Main.plugin.getConfig().getInt("speedrunnerRespawnTime")) {
                    e.getPlayer().setGameMode(GameMode.SURVIVAL);
                    boolean didTp = false;
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        if (Main.plugin.getConfig().getStringList("hunters").contains(pl.getName())) continue;
                        if (dead.contains(pl.getName())) continue;
                        e.getPlayer().teleport(pl.getLocation());
                        didTp = true;
                        break;
                    }
                    if (!didTp) {
                        Location location;

                        // if they have a bed spawn then use that
                        if (e.getPlayer().getBedSpawnLocation() != null) {
                            location = e.getPlayer().getBedSpawnLocation();
                        } else {
                            // otherwise use the world spawn
                            location = e.getPlayer().getWorld().getSpawnLocation();
                        }

                        e.getPlayer().teleport(location);
                        e.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
                    }
                    dead.remove(e.getPlayer().getName());
                    cancel();
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 20L);
    }

}
