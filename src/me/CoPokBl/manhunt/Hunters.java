package me.CoPokBl.manhunt;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hunters implements Listener {

    private final ArrayList<String> dead = new ArrayList<>();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!Main.plugin.getConfig().getStringList("hunters").contains(e.getPlayer().getName())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
                dead.add(e.getPlayer().getName());
            }
        }.runTaskLater(Main.plugin, 10L);

        final int[] secondsPast = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                secondsPast[0] += 1;
                e.getPlayer().sendMessage(ChatColor.GOLD + "Respawning in " + (Main.plugin.getConfig().getInt("hunterRespawnTime")-secondsPast[0]) + " seconds...");
                if (secondsPast[0] == Main.plugin.getConfig().getInt("hunterRespawnTime")) {
                    e.getPlayer().setGameMode(GameMode.SURVIVAL);
                    boolean didTp = false;
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        if (!Main.plugin.getConfig().getStringList("hunters").contains(pl.getName())) continue;
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

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!Main.plugin.getConfig().getStringList("hunters").contains(e.getPlayer().getName())) return;
        if (e.getItem() == null) return;
        if (!e.getItem().getType().equals(Material.COMPASS)) return;

        Object[] people = Bukkit.getOnlinePlayers().toArray();
        List<String> hunters = new ArrayList<>();
        for (Object p : people) {
            if (Main.plugin.getConfig().getStringList("hunters").contains(((Player)p).getName())) {
                continue;
            }
            hunters.add(((Player)p).getName());
        }

        List<Player> available = new ArrayList<>();
        for (String p : hunters) {
            if (dead.contains(p)) continue;
            Player player = Bukkit.getPlayer(p);
            if (player == null) continue;
            if (player.getWorld() != e.getPlayer().getWorld()) continue;
            available.add(player);
        }
        if (available.size() == 0) {
            e.getPlayer().sendMessage(ChatColor.RED + "No players are available to track!");
            return;
        }

        Random ran = new Random();
        Player target = available.get(ran.nextInt(hunters.size()));

        e.getPlayer().setCompassTarget(target.getLocation());
        e.getPlayer().sendMessage(ChatColor.GREEN + "Set compass target to " + target.getName());
    }

}
