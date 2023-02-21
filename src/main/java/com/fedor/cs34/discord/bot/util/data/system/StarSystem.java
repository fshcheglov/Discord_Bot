package com.fedor.cs34.discord.bot.util.data.system;

import com.fedor.cs34.discord.bot.util.data.nation.Nation;

public class StarSystem {
    public int id;
    public Coordinates coordinates;
    public String name;
    public Nation owner;

    public StarSystem(Coordinates coordinates, String name, Nation owner, int id) {
        this.coordinates = coordinates;
        this.name = name;
        this.owner = owner;
        this.id = id;
    }
}
