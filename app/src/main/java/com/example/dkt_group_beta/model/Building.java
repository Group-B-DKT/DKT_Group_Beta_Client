package com.example.dkt_group_beta.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public abstract class Building {
    private int price;
    private Player owner;
    private int position;
    private Field field;

    protected Building(int price, Player owner, int position, Field field){
        this.price = price;
        this.owner = owner;
        this.position = position;
        this.field = field;
    }

    public int getPrice(){
        return price;
    }

    public int getPosition(){
        return  position;
    }
    public Field getField() {
        return field;
    }
    public void setField(Field field){
        this.field = field;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
    public Player getOwner(){
        return owner;
    }
}