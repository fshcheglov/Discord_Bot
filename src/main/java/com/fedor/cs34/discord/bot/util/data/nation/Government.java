package com.fedor.cs34.discord.bot.util.data.nation;

public class Government {
    private final int id;
    private final String name;

    public Government(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
