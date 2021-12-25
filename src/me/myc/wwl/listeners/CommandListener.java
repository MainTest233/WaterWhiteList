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
            sender.sendMessage(this.getString("&bˮ��&e������&8>>&c��û��Ȩ��ִ�д�����"));
        } else {
            if (args.length == 0) {
                sender.sendMessage(this.getString("\n&a&l-------&bˮ��&e������&a-------\n" +
                        "&bˮ��&e������&8>>&rʳ�÷���:\n" +
                        "&bˮ��&e������&8>>&rwhitelist <on/off> ��/�ذ�����\n" +
                        "&bˮ��&e������&8>>&radd <ID> <mode> ��Ӱ�����\n" +
                        "&bˮ��&e������&8>>&rdel <ID> <mode> ɾ��������\n" +
                        "&bˮ��&e������&8>>&rlist ��ʾ�����������еģ�\n" +
                        "&bˮ��&e������&8>>&rkm <set/get> ����/��ȡ�߳����ʱ����ʾ"));
            } else if (args[0].equalsIgnoreCase("whitelist")) {
                    A.WhitelistMode=args[1];
                    if(A.WhitelistMode.equals("on")){
                    this.plugin.getConfig().set("enabled", true);
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�������Ѿ�����!"));
                } else if(A.WhitelistMode.equals("off")) {
                    this.plugin.getConfig().set("enabled", false);
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�������Ѿ�����!"));
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
                                sender.sendMessage(this.getString("&bˮ��&e������&8>>&c�������������޷���ӣ���ȷ��������Ƿ���ȷ������ȷӵ������"));
                            } else {
                                sender.sendMessage(this.getString("&bˮ��&e������&8>>&c����δ֪�����޷������ң��뷴���������ߣ�"));
                            }
                        } else if(A.pmode.equals("offline")) {
                            GetPlayerOfflineUUID();
                            offline.add(A.pname);
                            id.add(A.pUUID);
                        } else {
                            sender.sendMessage(this.getString("&bˮ��&e������&8>>&cδ֪��ģʽ���������������֧�ֵ�ģʽ:online/offline)"));
                        }
                        this.plugin.getConfig().set("online", online);
                        this.plugin.getConfig().set("offline", offline);
                        this.plugin.getConfig().set("UUID", id);
                        sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�û������!"));
                    } else {
                        sender.sendMessage(this.getString("&a&l-------&bˮ��&e������&a-------\n" +
                                "&bˮ��&e������&8>>&rʳ�÷���:\n" +
                                "&bˮ��&e������&8>>&r/whitelist add <ID> <mode>\n" +
                                "&bˮ��&e������&8>>&rID ��ҵ���Ϸ��\n" +
                                "&bˮ��&e������&8>>&rmode ��ҵ�ģʽ��online�����棩/offline�����棩��"));
                    }
                } else if (args[0].equalsIgnoreCase("del")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    id = this.plugin.getConfig().getStringList("UUID");
                    if (args.length >= 2) {
                        A.pname = args[1];
                        A.pmode = args[2];
                        if (!online.contains(A.pname) && !offline.contains(A.pname)) {
                            sender.sendMessage(this.getString("&bˮ��&e������&8>>&c�û������κ�һ���������б��ڣ������������"));
                        } else {
                            if (args[2].equalsIgnoreCase("online")) {
                                GetPlayerOnlineUUID();
                                if(A.respone==200){
                                    id.remove(A.pUUID);
                                    offline.remove(A.pname);
                                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�û��Ѿ�ɾ��!"));
                                }else if(A.respone==204){
                                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&c�޷���Mojang��������ȡ������ݣ�"));
                                }else{
                                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&c����δ֪�����޷�ɾ����ң��뷴���������ߣ�"));
                                }
                            } else if (args[2].equalsIgnoreCase("offline")) {
                                GetPlayerOfflineUUID();
                                sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�û��Ѿ�ɾ��!"));
                            } else {
                                sender.sendMessage(this.getString("&bˮ��&e������&8>>&cδ֪��ģʽ������ϸ������������֧�ֵ�ģʽ:online/offline)"));
                            }
                        }
                    } else {
                        sender.sendMessage(this.getString("&a&l-------&bˮ��&e������&a-------\n" +
                                "&bˮ��&e������&8>>&rʳ�÷���:\n" +
                                "&bˮ��&e������&8>>&r/whitelist del <ID> <mode>\n" +
                                "&bˮ��&e������&8>>&rID ��ҵ���Ϸ��\n" +
                                "&bˮ��&e������&8>>&rmode ��ҵ�ģʽ��online�����棩/offline�����棩��"));
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    online = this.plugin.getConfig().getStringList("online");
                    offline = this.plugin.getConfig().getStringList("offline");
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&r���������: " + online));
                    sender.sendMessage(this.getString("&bˮ��&e������&8>>&r���߰�����: " + offline));
                } else if (args[0].equalsIgnoreCase("km")) {
                    if (args.length == 1) {
                        sender.sendMessage(this.getString("&a&l-------&bˮ��&e������&a-------\n" +
                                "&bˮ��&e������&8>>&rʳ�÷���:\n" +
                                "&bˮ��&e������&8>>&r/whitelist km <set/get> [�߳���ʾ]\n" +
                                "&bˮ��&e������&8>>&r��������/��ȡ��������û�и����ʱ�߳�����ʾ\n" +
                                "&bˮ��&e������&8>>&rset �����߳���ʾ\n" +
                                "&bˮ��&e������&8>>&rget ��ȡ��ǰ���߳���ʾ"));
                    }
                    } else {
                        String kick_message;
                        if (args[1].equalsIgnoreCase("set")) {
                            if (args.length == 2) {
                                sender.sendMessage(this.getString("&a&l-------&bˮ��&e������&a-------\n" +
                                        "&bˮ��&e������&8>>&rʳ�÷���:\n" +
                                        "&bˮ��&e������&8>>&r/whitelist km set <�߳���ʾ>\n" +
                                        "&bˮ��&e������&8>>&r�������ð�������û�и����ʱ�߳�����ʾ"));
                            } else {
                                kick_message = args[2];

                                for (int i = 3; i < args.length; ++i) {
                                    kick_message = kick_message + " " + args[i];
                                }

                                this.plugin.getConfig().set("kick_message", kick_message);
                                sender.sendMessage(this.getString("&bˮ��&e������&8>>&6�ѳɹ�������ʾ!"));
                            }
                        } else if (args[1].equalsIgnoreCase("get")) {
                            kick_message = this.plugin.getConfig().getString("kick_message");
                            sender.sendMessage(this.getString("&bˮ��&e������&8>>&6��ʾ��Ϣ:&r " + kick_message));
                        }
                    }
                }
            }
        if (args[0].equalsIgnoreCase(" ")){
                sender.sendMessage(this.getString("&bˮ��&e������&8>>&cδ֪�������ʹ��/wwl�Ի�ȡ����"));
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
                        System.out.println("����:�޷�������Mojang������֤������");
                        System.out.println("������������");
                    }
                } catch (IOException e) {
                    System.out.println("����:�ڲ������뷴��������");
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                System.out.println("����:URL���������뷴��������");
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