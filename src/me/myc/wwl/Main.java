package me.myc.wwl;

import me.myc.wwl.libs.YamlConfig;
import me.myc.wwl.listeners.CommandListener;
import me.myc.wwl.listeners.EventListener;

import java.io.IOException;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class Main extends Plugin {
    private YamlConfig yamlConfig;
    private CommandListener commands;
    private EventListener events;
    private static Main instance;

    public Main() {
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
        try {
            this.yamlConfig.saveConfig();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public static Plugin getPluginInstance() {
        return instance;
    }

    public static Main getInstance() {
        return instance;
    }

    public Configuration getConfig() {
        return this.yamlConfig.getConfig();
    }
}
