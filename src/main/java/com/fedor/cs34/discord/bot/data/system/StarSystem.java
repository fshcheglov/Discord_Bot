package com.fedor.cs34.discord.bot.data.system;

import com.fedor.cs34.discord.bot.data.nation.Nation;

import java.util.ArrayList;

public class StarSystem {
    public int id;
    public ArrayList<Star> stars;
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
