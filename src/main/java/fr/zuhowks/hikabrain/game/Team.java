package fr.zuhowks.hikabrain.game;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum Team {
    BLUE("Blue"),
    RED("Red");

    private String name;
    private List<Player> members;
    private int point;

    Team(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.point = 0;
    }

    public String getName() {
        return name;
    }

    public List<Player> getMembers() {
        return members;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
