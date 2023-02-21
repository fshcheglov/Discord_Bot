package com.fedor.cs34.discord.bot.util.data.nation;

public class Species {
    private final String name;
    private int id;

    public Species(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
