package com.fedor.cs34.discord.bot;

import com.fedor.cs34.discord.bot.data.nation.*;
import com.fedor.cs34.discord.bot.data.system.Planet;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Bot {
    public static void main(String[] args) throws Exception {
        String jdbcURL = "jdbc:h2:C:/Users/Fedor3/Documents/database/H2/interstellar";
        String username = "Vrell";
        String password = "1234";
        Connection connection = DriverManager.getConnection(jdbcURL, username, password);

        connection.setAutoCommit(false);

        CreateDatabases.startDatabase(connection, "interstellar_database.sql");

        var access = new DataAccess(connection);

        EventWaiter waiter = new EventWaiter();
        JDABuilder jda = JDABuilder.createDefault("MTAyMzgxOTQ1NjkwMTgyODY1OQ.Gvm8oI.xBh8hWE0tYCCmu9wbVP_NUH8d4CiWTXzKPSXBQ");
        jda.addEventListeners(waiter);
        jda.addEventListeners((EventListener) event -> {
            if (event instanceof MessageReceivedEvent) {
                if (((MessageReceivedEvent) event).getAuthor().isBot()) {
                    return;
                }
                var mentionedUsers = ((MessageReceivedEvent) event).getMessage().getMentions().getUsers();
                String message = ((MessageReceivedEvent) event).getMessage().getContentStripped();
                if (message.equals("!createNation")) {
                    final Planet[] capital = {null};
                    event.getJDA().getGuildById("906409603150020618").getTextChannelById(((MessageReceivedEvent) event).getChannel().getId()).sendMessage(inputCapitalTemplate()).queue();
                    waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(((MessageReceivedEvent) event).getAuthor()) && e.getChannel().equals(((MessageReceivedEvent) event).getChannel()), e -> {
                        //Create Planet
                        var templateMessage = e.getMessage();
                        System.out.println(templateMessage.getContentStripped());
                        try {
                            capital[0] = handleCreatePlanet(access, templateMessage.getContentStripped(), 0, true);
                            if (capital[0] == null) {
                                templateMessage.reply("**Sorry, you didn't follow the template.**").queue();
                            } else {
                                templateMessage.reply("Capital planet successfully created.\n").queue();
                                // Create Nation
                                event.getJDA().getGuildById("906409603150020618").getTextChannelById(((MessageReceivedEvent) event).getChannel().getId()).sendMessage(inputNationTemplate()).queue();
                                waiter.waitForEvent(MessageReceivedEvent.class, f -> f.getAuthor().equals(((MessageReceivedEvent) event).getAuthor()) && f.getChannel().equals(((MessageReceivedEvent) event).getChannel()), f -> {
                                    var templateMessage2 = f.getMessage();
                                    System.out.println(templateMessage2.getContentStripped());
                                    try {
                                        var id = templateMessage2.getAuthor().getId();
                                        if (!handleCreateNation(access, templateMessage2.getContentStripped(), id, capital[0])) {
                                            templateMessage2.reply("**Sorry, you didn't follow the template.**").queue();
                                        } else {
                                            templateMessage2.reply("**Nation Registered**").queue();
                                        }
                                    } catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }, 5, TimeUnit.MINUTES, () -> ((MessageReceivedEvent) event).getMessage().reply("Sorry, you took too long.").queue());

                            }
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }, 10, TimeUnit.MINUTES, () -> ((MessageReceivedEvent) event).getMessage().reply("Sorry, you took too long.").queue());


                }

                if (message.equals("!EmpireList")) {
                    List<Nation> nations;
                    try {
                        nations = access.nationDAO.getAll();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    for (var nation : nations) {
                        event.getJDA().getGuildById("906409603150020618").getTextChannelById(((MessageReceivedEvent) event).getChannel().getId()).sendMessage(nation.returnNameAndOwner()).queue();
                    }
                }

                if (message.contains("!EmpireInfo")) {
                    if (mentionedUsers.size() > 0) {
                        var id = mentionedUsers.get(0).getId();
                        try {
                            var nations = access.nationDAO.getAll();
                            var ownedNations = new ArrayList<Nation>();
                            int nationCount = 0;
                            for (var nation : nations) {
                                if (nation.ownerID.equals(id)) {
                                    ownedNations.add(nation);
                                    nationCount++;
                                }
                            }
                            if (nationCount == 0) {
                                event.getJDA().getGuildById("906409603150020618").getTextChannelById(((MessageReceivedEvent) event).getChannel().getId()).sendMessage("**No empires found by this owner**").queue();
                            }
                            if (nationCount == 1) {
                                event.getJDA().getGuildById("906409603150020618").getTextChannelById(((MessageReceivedEvent) event).getChannel().getId()).sendMessage(nations.get(0).getNationInfo()).queue();
                            }
                            if (nationCount > 1) {
                                event.getJDA().getGuildById("906409603150020618").getTextChannelById(((MessageReceivedEvent) event).getChannel().getId()).sendMessage("Multiple empires found. Please enter the name of the one you wish to view").queue();
                                for (var nation : ownedNations) {
                                    event.getJDA().getGuildById("906409603150020618").getTextChannelById(((MessageReceivedEvent) event).getChannel().getId()).sendMessage(nation.returnNameAndOwner()).queue();
                                }
                                waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(((MessageReceivedEvent) event).getAuthor()) && e.getChannel().equals(((MessageReceivedEvent) event).getChannel()), e -> {
                                    var responseMessage = e.getMessage().getContentStripped();
                                    try {
                                        int nationNameCount = 0;
                                        for (var nationName : ownedNations) {
                                            if (responseMessage.equals(nationName.nationName)) {
                                                event.getJDA().getGuildById("906409603150020618").getTextChannelById(((MessageReceivedEvent) event).getChannel().getId()).sendMessage(nationName.getNationInfo()).queue();
                                                nationNameCount++;
                                            }
                                        }
                                        if (nationNameCount == 0) {
                                            e.getMessage().reply("**No nation with that name found!**").queue();
                                        }
                                    } catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }, 5, TimeUnit.MINUTES, () -> ((MessageReceivedEvent) event).getMessage().reply("Sorry, you took too long.").queue());
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (message.equals("!ResetDatabases")) {
                    // Command for testing. Remove in final version.
                    try {
                        CreateDatabases.startDatabase(connection, "drop_databases.sql");
                        CreateDatabases.startDatabase(connection, "interstellar_database.sql");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        jda.build();
    }

    public static String inputNationTemplate() {

        return "Please input nation details according to the following template:\n" +
                """
                        Nation Name:
                        Leader Name:
                        Species:

                        Government Type:
                        Economic System:

                        Resource Points:
                        Economic Points:
                        Manpower Points:

                        Population:

                        Stability:
                        Centralization:
                        Approval:
                        """;
    }

    public static String inputCapitalTemplate() {
        return "First, input capital planet details according to the following template:\n" +
                """
                        Planet Name:
                        Planet Type:
                                        
                        Planet Resources:
                        Planet Population:
                        Planet Development:

                        Planet Size:
                        Planet X-Coordinate:
                        Planet Y-Coordinate:
                        """;
    }

    public static Planet handleCreatePlanet(DataAccess dataAccess, String input, int nationID, boolean isPlayerCapital) throws Exception {
        var lines = input.split("\n");
        String planetName = null;
        String planetType = null;
        int planetResources = 0;
        int planetPopulation = 0;
        int planetDevelopment = 0;
        int planetSize = 0;
        int x = -1;
        int y = -1;

        for (var line : lines) {
            var index = line.indexOf(':');
            if (index >= 0) {
                var name = line.substring(0, index).trim();
                var value = line.substring(index + 1).trim();
                switch (name) {
                    case "Planet Name":
                        planetName = value;
                        break;
                    case "Planet Type":
                        planetType = value;
                        break;
                    case "Planet Resources":
                        try {
                            planetResources = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return null;
                        }
                        break;
                    case "Planet Population":
                        try {
                            planetPopulation = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return null;
                        }
                        break;
                    case "Planet Development":
                        try {
                            planetDevelopment = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return null;
                        }
                        break;
                    case "Planet Size":
                        try {
                            planetSize = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return null;
                        }
                        break;
                    case "Planet X-Coordinate":
                        try {
                            x = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return null;
                        }
                    case "Planet Y-Coordinate":
                        try {
                            y = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return null;
                        }
                }
            }
        }
        Planet planet;
        if (!isPlayerCapital) {
            planet = new Planet(planetName, planetType, planetResources, planetPopulation, planetDevelopment,
                    planetSize, dataAccess.starDAO.random(x, y, nationID), true, 0);
        } else {
            planet = new Planet(planetName, planetType, planetResources, planetPopulation, planetDevelopment,
                    planetSize, dataAccess.starDAO.random(x, y), true, 0);
        }
        dataAccess.planetDAO.insert(planet);
        if (x >= 0 && y >= 0 && planetName != null && planetType != null) {
            return planet;
        } else {
            dataAccess.connection.rollback();
            return null;
        }
    }

    public static boolean handleCreateNation(DataAccess dataAccess, String input, String ownerID, Planet capital) throws Exception {
        var lines = input.split("\n");
        String nationName = null;
        Species species = null;
        Leader leader = null;
        Government government = null;
        EconomicType economicType = null;
        Development development = new Development();
        int population = 0;
        double stability = 0;
        double centralization = 0;
        double approval = 0;

        for (var line : lines) {
            var index = line.indexOf(':');
            if (index >= 0) {
                var name = line.substring(0, index).trim();
                var value = line.substring(index + 1).trim();
                switch (name) {
                    case "Nation Name":
                        nationName = value;
                        break;
                    case "Leader Name":
                        leader = new Leader(0, value);
                        dataAccess.leaderDAO.insert(leader);
                        break;
                    case "Species":
                        species = new Species(0, value);
                        dataAccess.speciesDAO.insert(species);
                        break;
                    case "Government Type":
                        government = dataAccess.governmentDAO.getByName(value);
                        break;
                    case "Economic System":
                        economicType = dataAccess.economicDAO.getByName(value);
                        break;
                    case "Resource Points":
                        try {
                            development.resourcePoints = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return false;
                        }
                        break;
                    case "Economic Points":
                        try {
                            development.economicPoints = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return false;
                        }
                        break;
                    case "Manpower Points":
                        try {
                            development.manpowerPoints = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return false;
                        }
                        break;
                    case "Population":
                        try {
                            population = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return false;
                        }
                        break;
                    case "Stability":
                        try {
                            stability = Double.parseDouble(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return false;
                        }
                        break;
                    case "Centralization":
                        try {
                            centralization = Double.parseDouble(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return false;
                        }
                        break;
                    case "Approval":
                        try {
                            approval = Double.parseDouble(value);
                        } catch (NumberFormatException e) {
                            dataAccess.connection.rollback();
                            return false;
                        }
                        break;
                }
            }
        }

        Nation nation = new Nation(nationName, leader, government, economicType, species, population, stability, centralization, approval, capital, 0, ownerID);
        capital.star.system.owner = nation;
        nation.development = development;
        dataAccess.nationDAO.insert(nation);
        if (nationName != null && leader != null && government != null && economicType != null && species != null) {
            dataAccess.connection.commit();
            return true;
        } else {
            dataAccess.connection.rollback();
            return false;
        }
    }
}


