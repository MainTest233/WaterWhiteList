package me.myc.wwl;

import me.myc.wwl.libs.YamlConfig;
import me.myc.wwl.listeners.CommandListener;
import me.myc.wwl.listeners.EventListener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;


public class Main extends Plugin {
    private static Main instance;
    private YamlConfig yamlConfig;
    private CommandListener commands;
    private EventListener events;

    public static Plugin getPluginInstance() {
        return instance;
    }

    public static Main getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        this.yamlConfig = new YamlConfig("config.yml", instance);
        this.yamlConfig.saveDefaultConfig();
        this.commands = new CommandListener();
        this.events = new EventListener();
        this.getProxy().getPluginManager().registerListener(this, this.events);
        this.getProxy().getPluginManager().registerCommand(this, this.commands);
    }

    public void onDisable() {
        saveConfig();
    }

    public void saveConfig() {
        try {
            this.yamlConfig.saveConfig();
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return this.yamlConfig.getConfig();
    }
}
