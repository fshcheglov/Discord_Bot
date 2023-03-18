package com.fedor.cs34.discord.bot.util.data.system;

public class Coordinates {
    public int x;
    public int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        var coordinates = (Coordinates) obj;
        return coordinates.x == x && coordinates.y == y;
    }
}
