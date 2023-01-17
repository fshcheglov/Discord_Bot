package com.fedor.cs34.discord.bot.data.system;

import com.fedor.cs34.discord.bot.data.nation.Nation;

import javax.annotation.Nullable;

public class Planet {
    public int id;
    public String name;
    public String type;
    public int resources;
    public int population = 0;
    public int development = 0;
    public int size;
    public Star star;
    public boolean isHabitable;

    public Planet(String name, String planetType, int resources, int population, int development, int size, Star star, boolean isHabitable, int id) {
        this.star = star;
        this.name = name;
        this.type = planetType;
        this.resources = resources;
        this.population = population;
        this.development = development;
        this.size = size;
        this.id = id;
        this.isHabitable = isHabitable;
    }
}
