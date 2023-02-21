package com.fedor.cs34.discord.bot.jda;


import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class JDAObjects {
    public static MessageChannel getMessageChannel(String name, long id, Callback<Message> messageCallback) {
        MessageChannel channel = mock(MessageChannel.class, withSettings()
                .extraInterfaces(TextChannel.class, NewsChannel.class, MessageChannelUnion.class));
        when(channel.getName()).thenAnswer(invocation -> name);
        when(channel.getIdLong()).thenAnswer(invocation -> id);
        when(channel.getId()).thenAnswer(invocation -> String.valueOf(id));

        when(channel.sendMessage(any(CharSequence.class)))
                .thenAnswer(invocation -> getMessageCreateAction(messageCallback,
                        getMessage(invocation.getArgument(0), channel)));

        when(channel.sendMessage(any(MessageCreateData.class)))
                .thenAnswer(invocation -> getMessageCreateAction(messageCallback,
                        invocation.getArgument(0)));

        when(channel.sendMessageEmbeds(any(MessageEmbed.class), any(MessageEmbed[].class)))
                .thenAnswer(invocation -> {
                    List<MessageEmbed> embeds = invocation.getArguments().length == 1 ? new ArrayList<>() :
                            Arrays.asList(invocation.getArgument(1));
                    embeds.add(invocation.getArgument(0));
                    return getMessageCreateAction(messageCallback, getMessage(null, embeds, channel));
                });

        when(channel.sendMessageEmbeds(anyList()))
                .thenAnswer(invocation -> getMessageCreateAction(messageCallback,
                        getMessage(null, invocation.getArgument(0), channel)));

        return channel;
    }

    public static MessageCreateAction getMessageCreateAction(Callback<Message> messageCallback, Message message) {
        MessageCreateAction messageAction = mock(MessageCreateAction.class);
        Mockito.doAnswer(invocation -> {
            messageCallback.callback(message);
            return null;
        }).when(messageAction).queue();
        return messageAction;
    }

    /**
     * Get a mocked {@link Message}.
     *
     * @param content the content of the message. This is the raw, displayed and stripped content.
     * @param channel the {@link MessageChannel} the message would be sent in.
     * @return a mocked {@link Message}.
     */
    public static Message getMessage(String content, MessageChannel channel) {
        return getMessage(content, new ArrayList<>(), channel);
    }

    /**
     * Get a mocked {@link Message}.
     *
     * @param content the content of the message. This is the raw, displayed and stripped content.
     * @param embeds  a list of {@link MessageEmbed}s that this message contains.
     * @param channel the {@link MessageChannel} the message would be sent in.
     * @return a mocked {@link Message}.
     */
    public static Message getMessage(String content, List<MessageEmbed> embeds, MessageChannel channel) {
        Message message = mock(Message.class);
        when(message.getContentRaw()).thenAnswer(invocation -> content);
        when(message.getContentDisplay()).thenAnswer(invocation -> content);
        when(message.getContentStripped()).thenAnswer(invocation -> content);
        when(message.getChannel()).thenAnswer(invocation -> channel);
        when(message.getEmbeds()).thenAnswer(invocation -> embeds);
        when(message.getMentions()).thenAnswer(invocation -> {
            Matcher matcher = Pattern.compile("<@(\\d+)>").matcher(content);
            if (matcher.find()) {
                Mentions mockMention = mock(Mentions.class);
                when(mockMention.getUsers()).thenAnswer(invocation2 -> {
                    var userList = new ArrayList<User>();
                    var mockUser = mock(User.class);
                    when(mockUser.getId()).thenAnswer(invocation3 -> matcher.group().replaceAll("\\D", ""));
                    userList.add(mockUser);
                    return userList;
                });
                return mockMention;
            } else {
                throw new IllegalStateException("Expected Ping: " + content);
            }
        });
        return message;
    }


    /**
     * Get a mocked {@link MessageReceivedEvent}.
     *
     * @param channel the channel the message would be sent in.
     * @param message the message that would be sent.
     * @param member  the member that would send this message.
     * @return a mocked {@link MessageReceivedEvent}.
     */
    public static MessageReceivedEvent getMessageReceivedEvent(Guild guild, MessageChannel channel, Message message, Member member) {
        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        when(event.getGuild()).thenAnswer(invocation -> guild);
        when(event.getChannel()).thenAnswer(invocation -> channel);
        when(event.getMessage()).thenAnswer(invocation -> message);
        when(event.getMember()).thenAnswer(invocation -> member);
        when(event.getAuthor()).thenAnswer(invocation -> member.getUser());
        return event;
    }

    /**
     * Get a mocked {@link Member}. To get a mocked {@link User} use {@link Member#getUser()}.
     *
     * @param name          the name of the user.
     * @param discriminator the discriminator of the user.
     * @return a mocked {@link Member}.
     */
    public static Member getMember(String name, String discriminator, String id) {
        Member member = mock(Member.class);
        when(member.getEffectiveName()).thenAnswer(invocation -> name);
        when(member.getNickname()).thenAnswer(invocation -> name);
        when(member.getId()).thenAnswer(invocation -> id);

        User user = mock(User.class);
        when(user.getName()).thenAnswer(invocation -> name);
        when(user.getDiscriminator()).thenAnswer(invocation -> discriminator);
        when(user.getAsTag()).thenAnswer(invocation -> name + "#" + discriminator);
        when(user.getId()).thenAnswer(invocation -> id);
        when(user.getAsMention()).thenAnswer(invocation -> "<@" + id + ">");

        when(member.getUser()).thenAnswer(invocation -> user);

        return member;
    }

    public static Guild getGuild(String id, MessageChannel channel) {
        Guild guild = mock(Guild.class);
        when(guild.getId()).thenAnswer(invocation -> id);
        when(guild.getTextChannelById(channel.getId())).thenAnswer(invocation -> id);
        return guild;
    }


}
