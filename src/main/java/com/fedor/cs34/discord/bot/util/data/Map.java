package com.fedor.cs34.discord.bot.util.data;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.dao.system.StarDAO;
import com.fedor.cs34.discord.bot.dao.system.StarTypeDAO;
import com.fedor.cs34.discord.bot.util.data.system.Coordinates;
import com.fedor.cs34.discord.bot.util.data.system.Star;
import com.fedor.cs34.discord.bot.util.data.system.StarSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

public class Map {
    private final Connection connection;
    private final DataAccess dataAccess;

    public Map(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.connection = dataAccess.connection;
    }

    public BufferedImage generateGalaxyMap() throws SQLException {
        var random = new Random();

        var width = 1000;
        var height = 1000;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var starProbability = 0.05;
        for (var i = 0; i < width; i++) {
            for (var j = 0; j < height; j++) {
                var distFromCenter = distanceFromCenter(i, j);
                if (distFromCenter <= 25) {
                    image.setRGB(i, j, 0xFFFFFF);
                } else {
                    starProbability = (550 - distFromCenter) / 3000;
                    if (random.nextDouble() < starProbability) {
                        var type = new StarTypeDAO(dataAccess).random();
                        var typeColor = type.mapColor;
                        var name = "S-" + i + j;
                        var system = new StarSystem(new Coordinates(i, j), name, null, -1);
                        dataAccess.starSystemDAO.insert(system);
                        dataAccess.starDAO.insert(new Star(type, name, system, random.nextInt(15), -1));
                        image.setRGB(i, j, typeColor);
                    }
                }
            }
        }
        return image;
    }

    public BufferedImage displayGalaxyMap() throws SQLException {
        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        for (var i = 0; i < image.getWidth(); i++) {
            for (var j = 0; j < image.getHeight(); j++) {
                var coordinates = new Coordinates(i, j);
                var system = dataAccess.starSystemDAO.getByCoordinates(coordinates);
                if(system != null) {
                    // Making it be the star color will have to be added later, ATM I need Star, which isn't mapped to coordinates.
                    image.setRGB(i, j, 0xFFFFFF);
                } else {
                    image.setRGB(i, j, 0x000000);
                }
            }
        }
        return image;
    }

    double distanceFromCenter(int x, int y) {
        return Math.sqrt(Math.pow(x - 500, 2) + Math.pow(y - 500, 2));
    }
}
