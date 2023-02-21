package com.fedor.cs34.discord.bot.util.data.nation;

//TODO Make EconomicType abstract and be extended by all existing economic types.
public class EconomicType {
    private final int id;
    private final String name;

    public EconomicType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "EconomicType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
