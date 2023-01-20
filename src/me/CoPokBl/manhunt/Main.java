package me.CoPokBl.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new Hunters(), this);
        Bukkit.getPluginManager().registerEvents(new Speedrunners(), this);
    }

    @Override
    public void onDisable() {

    }
}
