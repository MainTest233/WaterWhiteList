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
            System.out.println("��ʼ��ȡ������ݣ���������쳣�������ж�������Ƿ���ȷ���Ҹ����ȷʵӵ�����棡");
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
            System.out.println("&bˮ��&e������&8>>"+"�����:"+pname);
            System.out.println("&bˮ��&e������&8>>"+"��ȡ�����ֶ�:"+temp);
            System.out.println("&bˮ��&e������&8>>"+"��������ֵ"+tUUID);
        }
        return tUUID;
    }

    public String GetPlayerOfflineUUID(String pname) {
        boolean debug=this.plugin.getConfig().getBoolean("debug");
        String tUUID;
        tUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pname).getBytes(StandardCharsets.UTF_8)).toString();
        if(debug){
            if(debug){
                System.out.println("&bˮ��&e������&8>>"+"�����:"+pname);
                System.out.println("&bˮ��&e������&8>>"+"���ɵ�UUID:"+tUUID);
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
            System.out.println("&bˮ��&e������&8>>"+"�����MojangID:"+tUUID);
            System.out.println("&bˮ��&e������&8>>"+"�����������UUID:"+pUUID);
        }
        return pUUID;
    }

    public void execute(CommandSender sender, String[] args) {
        boolean success=false;
        boolean debug=this.plugin.getConfig().getBoolean("debug");
        if (!sender.hasPermission("waterwhitelist.admin")) {
            sender.sendMessage(this.getString("&bˮ��&e������&8>>&c��û��Ȩ��ִ�д�����"));
        } else if (!args[0].equals("add") && !args[0].equals("whitelist") && !args[0].equals("del") && !args[0].equals("list") && !args[0].equals("km") && !args[0].equals("help") || args[0].isEmpty()) {
            sender.sendMessage(this.getString("&bˮ��&e������&8>>&cδ֪���������/wwl��ȡ����"));
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(this.getString("\n&a&l-------&bˮ��&e������&a-------\n" +
                        "&bˮ��&e������&8>>&rʳ�÷���:\n" +
                        "&bˮ��&e������&8>>&r/wwl whitelist <on/off> ��/�ذ�����\n" +
                        "&bˮ��&e������&8>>&r/wwl add <ID> <mode> ��Ӱ�����\n" +
                        "&bˮ��&e������&8>>&r/wwl del <ID> <mode> ɾ��������\n" +
                        "&bˮ��&e������&8>>&r/wwl list ��ʾ�����������еģ�\n" +
                        "&bˮ��&e������&8>>&r/wwl km <set/get> ����/��ȡ�߳����ʱ����ʾ"));
            } else if (args[0].equalsIgnoreCase("whitelist")) {
                if (args[1].equals("on")) {
                    this.plugin.getConfig().set("enabled", true);
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�������Ѿ�����!"));
                    plugin.saveConfig();
                } else if (args[1].equals("off")) {
                    this.plugin.getConfig().set("enabled", false);
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�������Ѿ�����!"));
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
                            System.out.println("&bˮ��&e������&8>>"+"�����:"+args[1]);
                            System.out.println("&bˮ��&e������&8>>"+"���ģʽ:"+args[2]);
                            System.out.println("&bˮ��&e������&8>>"+"���UUID:"+PlayerUUIDBroker(GetPlayerOnlineUUID(args[1])));
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
                        sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�û������!"));
                        plugin.saveConfig();
                } else if (args[0].equalsIgnoreCase("del")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    id = this.plugin.getConfig().getStringList("UUID");
                    if(debug){
                        System.out.println("&bˮ��&e������&8>>"+"�����:"+args[1]);
                        System.out.println("&bˮ��&e������&8>>"+"���ģʽ:"+args[2]);
                        System.out.println("&bˮ��&e������&8>>"+"���UUID:"+PlayerUUIDBroker(GetPlayerOnlineUUID(args[1])));
                    }
                    if (!online.contains(args[1]) && !offline.contains(args[1])) {
                        sender.sendMessage(this.getString("&bˮ��&e������&8>>&c�û������κ�һ���������б��ڣ������������"));
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
                        sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�û��Ѿ�ɾ��!"));
                    } else {
                        sender.sendMessage(this.getString("&bˮ��&e������&8>>&c�����쳣���û�δ�ܳɹ�ɾ���������Ǹ���ҵ�UUID�������ڰ������У����ֶ���ȡ����ҵ�UUID����config.yml��ɾ����Ȼ����������"));
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&r���������: " + online));
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&r���߰�����: " + offline));
                } else if (args[0].equalsIgnoreCase("km")) {
                    if (!args[1].equals("set") && !args[1].equals("get")) {
                        sender.sendMessage(this.getString("&a&l-------&bˮ��&e������&a-------\n" +
                                "&bˮ��&e������&8>>&rʳ�÷���:\n" +
                                "&bˮ��&e������&8>>&r/wwl km <set/get> [�߳���ʾ]\n" +
                                "&bˮ��&e������&8>>&r��������/��ȡ��������û�и����ʱ�߳�����ʾ\n" +
                                "&bˮ��&e������&8>>&rset �����߳���ʾ\n" +
                                "&bˮ��&e������&8>>&rget ��ȡ��ǰ���߳���ʾ"));
                    }
                } else {
                    String kick_message;
                    if (args[1].equalsIgnoreCase("set")) {
                            kick_message = args[2];
                            this.plugin.getConfig().set("kick_message", kick_message);
                            sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�ѳɹ�������ʾ!"));
                    } else if (args[1].equalsIgnoreCase("get")) {
                        kick_message = this.plugin.getConfig().getString("kick_message");
                        sender.sendMessage(this.getString("&bˮ��&e������&8>>&6��ʾ��Ϣ:&r " + kick_message));
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