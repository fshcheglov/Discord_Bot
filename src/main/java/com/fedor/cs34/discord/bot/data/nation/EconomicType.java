package com.fedor.cs34.discord.bot.data.nation;

//TODO Make EconomicType abstract and be extended by all existing economic types.
public class EconomicType {
    String name;
    public int id;
    public EconomicType(String economicTypeName, int id) {
        this.name = economicTypeName;
        this.id = id;
    }
}
