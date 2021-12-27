package me.myc.wwl.listeners;

import me.myc.wwl.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class EventListener implements Listener {

    private final Main plugin = Main.getInstance();

    @EventHandler
    public void onLogin(LoginEvent Ev, CommandSender sender) {
        Boolean debug = false;
        if (this.plugin.getConfig().getBoolean("enabled")) {
            if (!Ev.isCancelled()) {
                List<String> id = this.plugin.getConfig().getStringList("UUID");
                String kickMessage = this.plugin.getConfig().getString("kick_message");
                kickMessage = ChatColor.translateAlternateColorCodes('&', kickMessage);
                if(debug){
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>"+"�����:"+Ev.getConnection().getName()));
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>"+"���IP:"+Ev.getConnection().getSocketAddress()));
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>"+"���ģʽ:"+Ev.getConnection().isOnlineMode()));
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>"+"��Ұ汾:"+Ev.getConnection().getVersion()));
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>"+"���UUID:"+Ev.getConnection().getUniqueId()));
                }
                if (!id.contains(Ev.getConnection().getUniqueId())) {
                    Ev.setCancelled(true);
                    Ev.setCancelReason(kickMessage);
                }
            }
        }
    }

    private TextComponent getString(String label) {
        label = ChatColor.translateAlternateColorCodes('&', label);
        return new TextComponent(label);
    }
}