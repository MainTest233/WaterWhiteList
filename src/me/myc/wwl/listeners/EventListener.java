package me.myc.wwl.listeners;

import me.myc.wwl.Main;

import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventListener implements Listener {

    private Main plugin = Main.getInstance();

    @EventHandler
    public void onLogin(LoginEvent Ev,CommandSender sender) {
        if (this.plugin.getConfig().getBoolean("enabled")) {
            if (!Ev.isCancelled()) {
                List<String> id = this.plugin.getConfig().getStringList("UUID");
                String kickMessage = this.plugin.getConfig().getString("kick_message");
                kickMessage = ChatColor.translateAlternateColorCodes('&', kickMessage);
                if(!id.contains(Ev.getConnection().getUniqueId())){
                    Ev.setCancelled(true);
                    Ev.setCancelReason(kickMessage);
                }
            }
        }
    }
}
