package me.myc.wwl.listeners;

import com.alibaba.fastjson.JSONObject;
import me.myc.wwl.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class CommandListener extends Command {
    private final Main plugin = Main.getInstance();

    public CommandListener() {
        super("wwl");
    }

    public String GetPlayerOnlineUUID(String pname) {
        boolean debug=this.plugin.getConfig().getBoolean("debug");
        String temp = null;
        String tUUID;
        try {
            System.out.println("开始获取玩家数据，如果出现异常，请先判断玩家名是否正确并且该玩家确实拥有正版！");
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + pname);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStream input = httpUrlConn.getInputStream();
            InputStreamReader read = new InputStreamReader(input, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(read);
            String data = br.readLine();
            temp = data;
            br.close();
            read.close();
            input.close();
            httpUrlConn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(temp);
        tUUID = jsonObject.getString("id");
        if(debug){
            System.out.println("&b水滴&e白名单&8>>"+"玩家名:"+pname);
            System.out.println("&b水滴&e白名单&8>>"+"获取到的字段:"+temp);
            System.out.println("&b水滴&e白名单&8>>"+"解析出的值"+tUUID);
        }
        return tUUID;
    }

    public String GetPlayerOfflineUUID(String pname) {
        boolean debug=this.plugin.getConfig().getBoolean("debug");
        String tUUID;
        tUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pname).getBytes(StandardCharsets.UTF_8)).toString();
        if(debug){
            if(debug){
                System.out.println("&b水滴&e白名单&8>>"+"玩家名:"+pname);
                System.out.println("&b水滴&e白名单&8>>"+"生成的UUID:"+tUUID);
            }
        }
        return tUUID;
    }

    public String PlayerUUIDBroker(String tUUID) {
        boolean debug=this.plugin.getConfig().getBoolean("debug");
        String pUUID;
        StringBuilder sb = new StringBuilder(tUUID);
        sb.insert(8, '-');
        sb.insert(13, '-');
        sb.insert(18, '-');
        sb.insert(23, '-');
        pUUID = sb.toString();
        if(debug){
            System.out.println("&b水滴&e白名单&8>>"+"传入的MojangID:"+tUUID);
            System.out.println("&b水滴&e白名单&8>>"+"解析出的玩家UUID:"+pUUID);
        }
        return pUUID;
    }

    public void execute(CommandSender sender, String[] args) {
        boolean success=false;
        boolean debug=this.plugin.getConfig().getBoolean("debug");
        if (!sender.hasPermission("waterwhitelist.admin")) {
            sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c你没有权限执行此命令"));
        } else if (!args[0].equals("add") && !args[0].equals("whitelist") && !args[0].equals("del") && !args[0].equals("list") && !args[0].equals("km") && !args[0].equals("help") || args[0].isEmpty()) {
            sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c未知命令，请输入/wwl获取帮助"));
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(this.getString("\n&a&l-------&b水滴&e白名单&a-------\n" +
                        "&b水滴&e白名单&8>>&r食用方法:\n" +
                        "&b水滴&e白名单&8>>&r/wwl whitelist <on/off> 开/关白名单\n" +
                        "&b水滴&e白名单&8>>&r/wwl add <ID> <mode> 添加白名单\n" +
                        "&b水滴&e白名单&8>>&r/wwl del <ID> <mode> 删除白名单\n" +
                        "&b水滴&e白名单&8>>&r/wwl list 显示白名单（所有的）\n" +
                        "&b水滴&e白名单&8>>&r/wwl km <set/get> 设置/获取踢出玩家时的提示"));
            } else if (args[0].equalsIgnoreCase("whitelist")) {
                if (args[1].equals("on")) {
                    this.plugin.getConfig().set("enabled", true);
                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6白名单已经启用!"));
                    plugin.saveConfig();
                } else if (args[1].equals("off")) {
                    this.plugin.getConfig().set("enabled", false);
                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6白名单已经禁用!"));
                    plugin.saveConfig();
                }
            } else {
                List online;
                List offline;
                List id;
                if (args[0].equalsIgnoreCase("add")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    id = this.plugin.getConfig().getStringList("UUID");
                        if(debug){
                            System.out.println("&b水滴&e白名单&8>>"+"玩家名:"+args[1]);
                            System.out.println("&b水滴&e白名单&8>>"+"玩家模式:"+args[2]);
                            System.out.println("&b水滴&e白名单&8>>"+"玩家UUID:"+PlayerUUIDBroker(GetPlayerOnlineUUID(args[1])));
                        }
                        if (args[2].equals("online")) {
                            online.add(args[1]);
                            id.add(PlayerUUIDBroker(GetPlayerOnlineUUID(args[1])));
                        } else if (args[2].equals("offline")) {
                            offline.add(args[1]);
                            id.add(GetPlayerOfflineUUID(args[1]));
                        }
                        this.plugin.getConfig().set("online", online);
                        this.plugin.getConfig().set("offline", offline);
                        this.plugin.getConfig().set("UUID", id);
                        sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6用户已添加!"));
                        plugin.saveConfig();
                } else if (args[0].equalsIgnoreCase("del")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    id = this.plugin.getConfig().getStringList("UUID");
                    if(debug){
                        System.out.println("&b水滴&e白名单&8>>"+"玩家名:"+args[1]);
                        System.out.println("&b水滴&e白名单&8>>"+"玩家模式:"+args[2]);
                        System.out.println("&b水滴&e白名单&8>>"+"玩家UUID:"+PlayerUUIDBroker(GetPlayerOnlineUUID(args[1])));
                    }
                    if (!online.contains(args[1]) && !offline.contains(args[1])) {
                        sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c用户不在任何一个白名单列表内！请检查您的命令！"));
                    } else if (args[2].equals("online")) {
                        id.remove(PlayerUUIDBroker(GetPlayerOnlineUUID(args[1])));
                        online.remove(args[1]);
                        success=true;
                    } else if (args[2].equals("offline")) {
                        id.remove(GetPlayerOfflineUUID(args[1]));
                        offline.remove(args[1]);
                        success=true;
                    } else if (success) {
                        this.plugin.getConfig().set("online", online);
                        this.plugin.getConfig().set("offline", offline);
                        this.plugin.getConfig().set("UUID", id);
                        plugin.saveConfig();
                        sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6用户已经删除!"));
                    } else {
                        sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c出现异常，用户未能成功删除，可能是该玩家的UUID不存在于白名单中，请手动获取该玩家的UUID并在config.yml中删除，然后反馈给作者"));
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&r正版白名单: " + online));
                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&r离线白名单: " + offline));
                } else if (args[0].equalsIgnoreCase("km")) {
                    if (!args[1].equals("set") && !args[1].equals("get")) {
                        sender.sendMessage(this.getString("&a&l-------&b水滴&e白名单&a-------\n" +
                                "&b水滴&e白名单&8>>&r食用方法:\n" +
                                "&b水滴&e白名单&8>>&r/wwl km <set/get> [踢出提示]\n" +
                                "&b水滴&e白名单&8>>&r用于设置/获取白名单内没有该玩家时踢出的提示\n" +
                                "&b水滴&e白名单&8>>&rset 设置踢出提示\n" +
                                "&b水滴&e白名单&8>>&rget 获取当前的踢出提示"));
                    }
                } else {
                    String kick_message;
                    if (args[1].equalsIgnoreCase("set")) {
                            kick_message = args[2];
                            this.plugin.getConfig().set("kick_message", kick_message);
                            sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6已成功设置提示!"));
                    } else if (args[1].equalsIgnoreCase("get")) {
                        kick_message = this.plugin.getConfig().getString("kick_message");
                        sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6提示信息:&r " + kick_message));
                    }
                }
            }
        }
    }

    private TextComponent getString(String label) {
        label = ChatColor.translateAlternateColorCodes('&', label);
        return new TextComponent(label);
    }
}