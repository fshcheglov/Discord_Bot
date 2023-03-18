package com.fedor.cs34.discord.bot;

import net.dv8tion.jda.api.JDA;

import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TimerUpdates {
    private final DataAccess dataAccess;
    private final Timer timer;
    private final JDA jda;
    private final Date date;
    private Integer ingameYear;

    public TimerUpdates(DataAccess dataAccess, JDA jda) {
        this.dataAccess = dataAccess;
        this.timer = new Timer();
        this.jda = jda;
        this.date = new Date();
    }

    void startTimer() {
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateDate();
            }
        }, date, 1000);
    }

    private void updateDate() {
        // These are the bot test channel IDs. Replace with proper ones when bot is out of testing.
        ingameYear++;
        Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById(906409603150020618L)).
                getTextChannelById(1023813558951223296L)).sendMessage(Integer.toString(ingameYear)).queue();


    }


}
