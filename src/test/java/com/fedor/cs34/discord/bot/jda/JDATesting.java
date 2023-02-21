package com.fedor.cs34.discord.bot.jda;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;


public class JDATesting {
    public static Message testMessageReceivedEvent(EventListener listener, String input, Member member) throws InterruptedException {
        Callback<Message> messageCallback = new Callback<>();

        MessageChannel channel = JDAObjects.getMessageChannel("test-chanel", 0L, messageCallback);
        Message message = JDAObjects.getMessage(input, new ArrayList<>(), channel);
        Guild guild = JDAObjects.getGuild("0000", channel);
        MessageReceivedEvent event = JDAObjects.getMessageReceivedEvent(guild, channel, message,
                member);

        listener.onEvent(event);
        return messageCallback.await();
    }
}
