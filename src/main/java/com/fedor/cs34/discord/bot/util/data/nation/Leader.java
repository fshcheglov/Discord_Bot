package com.fedor.cs34.discord.bot.util.data.nation;

public class Leader {
    private int id;
    private final String name;

    public Leader(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        var leader = (Leader) obj;
        return getName().equals(name) && leader.getId() == id;
    }
}
