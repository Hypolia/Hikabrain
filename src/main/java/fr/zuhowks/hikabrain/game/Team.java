package fr.zuhowks.hikabrain.game;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public enum Team {
    BLUE("Blue", ChatColor.BLUE),
    RED("Red", ChatColor.RED),;

    private String name;
    private List<UUID> members;
    private int point;
    private ChatColor color;

    Team(String name, ChatColor color) {
        this.name = name;
        this.members = new ArrayList<>();
        this.point = 0;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
