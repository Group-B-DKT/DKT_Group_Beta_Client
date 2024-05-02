package com.example.dkt_group_beta.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Player {
    @Getter
    private String username;
    @Getter
    private String id;
    @Getter
    @Setter
    private boolean isConnected;
    @Getter
    private int gameId;
    @Getter
    private double playerMoney;



    public Player(String username, String id) {
        this.username = username;
        this.id = id;
        this.gameId = -1;
        this.setConnected(false);
    }


    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public int getGameId() {
        return gameId;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
