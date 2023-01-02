package com.fedor.cs34.discord.bot.data.system;

import com.fedor.cs34.discord.bot.data.nation.Nation;

import java.util.ArrayList;

public class Star {
    public int id;
    public  StarType type;
    public String name;
    public Nation owner;
    public  StarSystem system;
    public int resources;
    public  ArrayList<Planet> planets;

    public Star(StarType type, String name, StarSystem system, int resources, int id) {
        this.type = type;
        this.name = name;
        this.system = system;
        this.resources = resources;
        this.id = id;
        this.owner = system.owner;
    }
}
