package com.fedor.cs34.discord.bot;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class BotListener implements EventListener {
    private final DataAccess dataAccess;
    private final HashMap<String, BotListenerDataStore> userData = new HashMap<>();

    public BotListener(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        if (!(genericEvent instanceof MessageReceivedEvent event) || event.getAuthor().isBot()) {
            return;
        }

        var userID = event.getAuthor().getId();
        if (!userData.containsKey(userID)) {
            userData.put(userID, new BotListenerDataStore(State.DEFAULT, null));
        }

        var message = event.getMessage().getContentStripped();
        if (userData.get(userID).state == State.DEFAULT && message.startsWith("!")) {
            if (message.equals("!EmpireList")) {
                // Should be setAllowedMentions(Collections.EMPTY_SET). However, this makes the tests fail and requires a lot of effort to implement into mocking.
                // So, until later - it remains commented.
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
                userData.replace(userID, new BotListenerDataStore(State.CREATEPLANET, userData.get(userID).capital));
            }

            if (message.startsWith("!GenerateMap")) {
                File outputFile = new File("image.jpg");
                try {
                    ImageIO.write(Bot.handleMapCreation(dataAccess), "jpg", outputFile);
                    event.getChannel().sendMessage("Galaxy map generated.").addFiles(FileUpload.fromData(outputFile));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // This is used to mantain the same map consistently. When the bot is able to distinguish between users (admin/non admin) commands will be restricted accordingly.
            if (message.startsWith("!ShowMap")) {
                File outputFile = new File("image.jpg");
                try {
                    ImageIO.write(Bot.handleDisplayMap(dataAccess), "jpg", outputFile);
                    event.getChannel().sendMessage("").addFiles(FileUpload.fromData(outputFile));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (userData.get(userID).state == State.CREATEPLANET) {
            try {
                var planet = Bot.handleCreatePlanet(dataAccess, message);
                if (planet == null) {
                    event.getChannel().sendMessage("**Sorry, you didn't follow the template.**").queue();
                    userData.replace(userID, new BotListenerDataStore(State.DEFAULT, null));
                } else {
                    event.getChannel().sendMessage("Capital planet successfully created.\n").queue();
                    event.getChannel().sendMessage(Bot.inputNationTemplate()).queue();
                    userData.replace(userID, new BotListenerDataStore(State.CREATENATION, planet));
                }
            } catch (Exception e) {
                event.getChannel().sendMessage("**Sorry, you didn't follow the template.**").queue();
                userData.replace(userID, new BotListenerDataStore(State.DEFAULT, userData.get(userID).capital));
            }
        } else if (userData.get(userID).state == State.CREATENATION) {
            try {
                var nation = Bot.handleCreateNation(dataAccess, message, userID, userData.get(userID).capital);
                if (!nation) {
                    event.getChannel().sendMessage("**Sorry, you didn't follow the template.**").queue();
                    userData.replace(userID, new BotListenerDataStore(State.DEFAULT, null));

                } else {
                    event.getChannel().sendMessage("**Nation Registered**").queue();
                    userData.replace(userID, new BotListenerDataStore(State.DEFAULT, userData.get(userID).capital));
                }

            } catch (Exception e) {
                event.getChannel().sendMessage("**Sorry, you didn't follow the template.**").queue();
                userData.replace(userID, new BotListenerDataStore(State.DEFAULT, userData.get(userID).capital));
            }
        }

    }
}

enum State {
    DEFAULT, CREATEPLANET, CREATENATION
}