package com.fedor.cs34.discord.bot;

import com.fedor.cs34.discord.bot.util.data.nation.*;
import com.fedor.cs34.discord.bot.util.data.system.Coordinates;
import com.fedor.cs34.discord.bot.util.data.system.Planet;
import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Bot {
    public static void main(String[] args) throws Exception {
        var arguments = CommandLineArguments.parse(args);
        String jdbcURL = "jdbc:h2:" + arguments.databasePath;
        String username = "Vrell";
        String password = "1234";
        Connection connection = DriverManager.getConnection(jdbcURL, username, password);

        connection.setAutoCommit(false);

//        CreateDatabases.startDatabase(connection, "interstellar_database.sql");

        var dataAccess = new DataAccess(connection);

        var listener = new BotListener(dataAccess);
        JDABuilder jda = JDABuilder.createDefault(arguments.discordToken);
        jda.addEventListeners(listener);
        jda.build();
    }

    @NotNull
    public static StringBuffer handleEmpireList(DataAccess dataAccess) {
        List<Nation> nations;
        try {
            nations = dataAccess.nationDAO.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        var builder = new StringBuffer();
        for (var nation : nations) {
            builder.append(nation.returnNameAndOwner());
        }
        return builder;
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

    public static Planet handleCreatePlanet(DataAccess dataAccess, String input) throws Exception {
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
                        break;
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
        planet = new Planet(planetName, planetType, planetResources, planetPopulation, planetDevelopment,
                planetSize, dataAccess.starDAO.random(new Coordinates(x, y)), true, 0);
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

        if (nationName != null && leader != null && government != null && economicType != null && species != null) {
            Nation nation = new Nation(nationName, leader, government, economicType, species, population, stability, centralization, approval, capital, 0, ownerID);
            nation.development = development;
            dataAccess.nationDAO.insert(nation);
            dataAccess.planetDAO.setOwner(capital, nation);
            dataAccess.connection.commit();
            return true;
        } else {
            dataAccess.connection.rollback();
            return false;
        }
    }
}


