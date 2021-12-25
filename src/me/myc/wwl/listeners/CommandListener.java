package me.myc.wwl.listeners;

import me.myc.wwl.Main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import com.alibaba.fastjson.JSONObject;

public class CommandListener extends Command {
    public static class A {
        public static String pname;
        public static String pmode;
        public static String pUUID;
        public static String tUUID;
        public static String temp;
        public static int respone;
        public static String WhitelistMode;
    }

    private final Main plugin = Main.getInstance();

    public CommandListener() {
        super("wwl");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("whitelist.admin")) {
            sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c你没有权限执行此命令"));
        } else {
            if (args.length == 0) {
                sender.sendMessage(this.getString("\n&a&l-------&b水滴&e白名单&a-------\n" +
                        "&b水滴&e白名单&8>>&r食用方法:\n" +
                        "&b水滴&e白名单&8>>&rwhitelist <on/off> 开/关白名单\n" +
                        "&b水滴&e白名单&8>>&radd <ID> <mode> 添加白名单\n" +
                        "&b水滴&e白名单&8>>&rdel <ID> <mode> 删除白名单\n" +
                        "&b水滴&e白名单&8>>&rlist 显示白名单（所有的）\n" +
                        "&b水滴&e白名单&8>>&rkm <set/get> 设置/获取踢出玩家时的提示"));
            } else if (args[0].equalsIgnoreCase("whitelist")) {
                    A.WhitelistMode=args[1];
                    if(A.WhitelistMode.equals("on")){
                    this.plugin.getConfig().set("enabled", true);
                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6白名单已经启用!"));
                } else if(A.WhitelistMode.equals("off")) {
                    this.plugin.getConfig().set("enabled", false);
                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6白名单已经禁用!"));
                }
            } else {
                List online;
                List offline;
                List id;
                if (args[0].equalsIgnoreCase("add")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    id = this.plugin.getConfig().getStringList("UUID");
                    if (args.length >= 2) {
                        A.pname = args[1];
                        A.pmode = args[2];
                        if (A.pmode.equals("online")){
                            GetPlayerOnlineUUID();
                            if (A.respone == 200) {
                                online.add(A.pname);
                                id.add(A.pUUID);
                            } else if (A.respone == 204) {
                                sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c错误的玩家名！无法添加！请确认玩家名是否正确并且正确拥有正版"));
                            } else {
                                sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c出现未知错误！无法添加玩家！请反馈给开发者！"));
                            }
                        } else if(A.pmode.equals("offline")) {
                            GetPlayerOfflineUUID();
                            offline.add(A.pname);
                            id.add(A.pUUID);
                        } else {
                            sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c未知的模式，请检查您的命令！（支持的模式:online/offline)"));
                        }
                        this.plugin.getConfig().set("online", online);
                        this.plugin.getConfig().set("offline", offline);
                        this.plugin.getConfig().set("UUID", id);
                        sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6用户已添加!"));
                    } else {
                        sender.sendMessage(this.getString("&a&l-------&b水滴&e白名单&a-------\n" +
                                "&b水滴&e白名单&8>>&r食用方法:\n" +
                                "&b水滴&e白名单&8>>&r/whitelist add <ID> <mode>\n" +
                                "&b水滴&e白名单&8>>&rID 玩家的游戏名\n" +
                                "&b水滴&e白名单&8>>&rmode 玩家的模式（online（正版）/offline（盗版））"));
                    }
                } else if (args[0].equalsIgnoreCase("del")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    id = this.plugin.getConfig().getStringList("UUID");
                    if (args.length >= 2) {
                        A.pname = args[1];
                        A.pmode = args[2];
                        if (!online.contains(A.pname) && !offline.contains(A.pname)) {
                            sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c用户不在任何一个白名单列表内！请检查您的命令！"));
                        } else {
                            if (args[2].equalsIgnoreCase("online")) {
                                GetPlayerOnlineUUID();
                                if(A.respone==200){
                                    id.remove(A.pUUID);
                                    offline.remove(A.pname);
                                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6用户已经删除!"));
                                }else if(A.respone==204){
                                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c无法从Mojang服务器获取玩家数据！"));
                                }else{
                                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c出现未知错误！无法删除玩家！请反馈给开发者！"));
                                }
                            } else if (args[2].equalsIgnoreCase("offline")) {
                                GetPlayerOfflineUUID();
                                sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6用户已经删除!"));
                            } else {
                                sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c未知的模式，请仔细检查您的命令！（支持的模式:online/offline)"));
                            }
                        }
                    } else {
                        sender.sendMessage(this.getString("&a&l-------&b水滴&e白名单&a-------\n" +
                                "&b水滴&e白名单&8>>&r食用方法:\n" +
                                "&b水滴&e白名单&8>>&r/whitelist del <ID> <mode>\n" +
                                "&b水滴&e白名单&8>>&rID 玩家的游戏名\n" +
                                "&b水滴&e白名单&8>>&rmode 玩家的模式（online（正版）/offline（盗版））"));
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&r正版白名单: " + online));
                    sender.sendMessage(this.getString("&b水滴&e白名单&8>>&r离线白名单: " + offline));
                } else if (args[0].equalsIgnoreCase("km")) {
                    if (args.length == 1) {
                        sender.sendMessage(this.getString("&a&l-------&b水滴&e白名单&a-------\n" +
                                "&b水滴&e白名单&8>>&r食用方法:\n" +
                                "&b水滴&e白名单&8>>&r/whitelist km <set/get> [踢出提示]\n" +
                                "&b水滴&e白名单&8>>&r用于设置/获取白名单内没有该玩家时踢出的提示\n" +
                                "&b水滴&e白名单&8>>&rset 设置踢出提示\n" +
                                "&b水滴&e白名单&8>>&rget 获取当前的踢出提示"));
                    }
                    } else {
                        String kick_message;
                        if (args[1].equalsIgnoreCase("set")) {
                            if (args.length == 2) {
                                sender.sendMessage(this.getString("&a&l-------&b水滴&e白名单&a-------\n" +
                                        "&b水滴&e白名单&8>>&r食用方法:\n" +
                                        "&b水滴&e白名单&8>>&r/whitelist km set <踢出提示>\n" +
                                        "&b水滴&e白名单&8>>&r用于设置白名单内没有该玩家时踢出的提示"));
                            } else {
                                kick_message = args[2];

                                for (int i = 3; i < args.length; ++i) {
                                    kick_message = kick_message + " " + args[i];
                                }

                                this.plugin.getConfig().set("kick_message", kick_message);
                                sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6已成功设置提示!"));
                            }
                        } else if (args[1].equalsIgnoreCase("get")) {
                            kick_message = this.plugin.getConfig().getString("kick_message");
                            sender.sendMessage(this.getString("&b水滴&e白名单&8>>&6提示信息:&r " + kick_message));
                        }
                    }
                }
            }
        if (args[0].equalsIgnoreCase(" ")){
                sender.sendMessage(this.getString("&b水滴&e白名单&8>>&c未知的命令，请使用/wwl以获取帮助"));
        }
    }

    public static void GetPlayerOnlineUUID() {
        new Thread(() -> {
            try {
                URL u = new URL("https://api.mojang.com/users/profiles/minecraft/"+A.pname);
                try {
                    HttpURLConnection uConnection = (HttpURLConnection) u.openConnection();
                    try {
                        uConnection.connect();
                        A.respone = uConnection.getResponseCode();
                        InputStream is = uConnection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        StringBuilder sb = new StringBuilder();
                        while(br.read() != -1){
                            sb.append(br.readLine());
                        }
                        String content = sb.toString();
                        content = new String(content.getBytes("UTF-8"));
                        A.temp=content+"}";
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("错误:无法连接至Mojang正版验证服务器");
                        System.out.println("请检查您的网络");
                    }
                } catch (IOException e) {
                    System.out.println("错误:内部错误，请反馈给作者");
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                System.out.println("错误:URL构建错误，请反馈给作者");
                e.printStackTrace();
            }
            JSONObject jsonObject = JSONObject.parseObject(A.temp);
            A.tUUID = jsonObject.getString("id");
            PlayerUUIDBroker();
        }).start();
    }
    public static void GetPlayerOfflineUUID(){
        A.tUUID=UUID.nameUUIDFromBytes(("OfflinePlayer:" + A.pname).getBytes(StandardCharsets.UTF_8)).toString();
        PlayerUUIDBroker();
    }
    public static void PlayerUUIDBroker(){
        StringBuilder sb = new StringBuilder(A.tUUID);
        sb.insert(8, '-');
        sb.insert(13, '-');
        sb.insert(18, '-');
        sb.insert(23, '-');
        A.pUUID=sb.toString();
    }

    private TextComponent getString(String label) {
        label = ChatColor.translateAlternateColorCodes('&', label);
        return new TextComponent(label);
    }
}