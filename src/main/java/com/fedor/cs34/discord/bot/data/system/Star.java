package com.fedor.cs34.discord.bot.data.system;

public class Star {
    public int id;
    public StarType type;
    public String name;
    public StarSystem system;
    public int resources;

    public Star(StarType type, String name, StarSystem system, int resources, int id) {
        this.type = type;
        this.name = name;
        this.system = system;
        this.resources = resources;
        this.id = id;
    }
}
