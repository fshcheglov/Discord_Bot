package com.fedor.cs34.discord.bot;

import com.fedor.cs34.discord.bot.util.data.system.Planet;

public class BotListenerDataStore {
    State state;
    Planet capital;

    BotListenerDataStore(State state, Planet capital){
        this.state = state;
        this.capital = capital;
    }
}
