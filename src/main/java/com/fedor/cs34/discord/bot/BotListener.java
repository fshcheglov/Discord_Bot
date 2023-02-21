package com.fedor.cs34.discord.bot;

import com.fedor.cs34.discord.bot.util.data.system.Planet;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;

public class BotListener implements EventListener {
    private final DataAccess dataAccess;
    State state = State.DEFAULT;
    Planet planet;

    public BotListener(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        if (!(genericEvent instanceof MessageReceivedEvent event)) {
            return;
        }
        var message = event.getMessage().getContentStripped();
        if (state == State.DEFAULT) {
            if (message.equals("!EmpireList")) {
                event.getChannel().sendMessage(Bot.handleEmpireList(dataAccess).toString()).queue();
            }
            if (message.startsWith("!EmpireInfo")) {
                var mentions = event.getMessage().getMentions().getUsers();
                if (mentions.size() > 0) {
                    var id = mentions.get(0).getId();
                    try {
                        var nation = dataAccess.nationDAO.getByOwnerId(id);
                        event.getChannel().sendMessage(nation.getNationInfo()).queue();
                    } catch (Exception e) {
                        event.getChannel().sendMessage("**No Owned Nations!**").queue();
                    }
                }
            }

            if (message.startsWith("!CreateNation")) {
                event.getChannel().sendMessage(Bot.inputCapitalTemplate()).queue();
                state = State.CREATEPLANET;
            }
        } else if (state == State.CREATEPLANET) {
            try {
                var planet = Bot.handleCreatePlanet(dataAccess, message);
                if (planet == null) {
                    event.getChannel().sendMessage("**Sorry, you didn't follow the template.**").queue();
                } else {
                    event.getChannel().sendMessage("Capital planet successfully created.\n").queue();
                }
            } catch (Exception e) {
                event.getChannel().sendMessage("**Sorry, you didn't follow the template.**").queue();
            }
            state = State.CREATENATION;
        } else if (state == State.CREATENATION) {
            try {
                var nation = Bot.handleCreateNation(dataAccess, message, event.getAuthor().getId(), planet);
                if (!nation) {
                    event.getChannel().sendMessage("**Sorry, you didn't follow the template.**").queue();
                } else {
                    event.getChannel().sendMessage("**Nation Registered**").queue();
                }
            } catch (Exception e) {
                event.getChannel().sendMessage("**Sorry, you didn't follow the template.**").queue();
            }
        }

    }
}

enum State {
    DEFAULT, CREATEPLANET, CREATENATION
}