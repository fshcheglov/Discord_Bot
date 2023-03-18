package com.fedor.cs34.discord.bot.util.data.nation;

public class NationModifier {
    public int id;
    public String name;
    public boolean isFlatModifier;
    public ModifierType type;
    public double value;
    public int duration;
    public Nation nation;

    public NationModifier(int id, String name, boolean isFlatModifier, ModifierType type, double value, int duration, Nation nation) {
        this.id = id;
        this.name = name;
        this.isFlatModifier = isFlatModifier;
        this.type = type;
        this.value = value;
        this.duration = duration;
        this.nation = nation;
    }
}






