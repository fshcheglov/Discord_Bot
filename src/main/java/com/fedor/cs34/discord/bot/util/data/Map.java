package com.fedor.cs34.discord.bot.util.data;

import com.fedor.cs34.discord.bot.DataAccess;
import com.fedor.cs34.discord.bot.dao.system.StarDAO;
import com.fedor.cs34.discord.bot.dao.system.StarTypeDAO;

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

    public BufferedImage createNationMap() throws SQLException {
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
                        image.setRGB(i, j, typeColor);
                    }
                }
            }
        }
        return image;
    }

//    boolean[][] spiral() {
//        var spiral = new boolean[1000][1000];
//        int x = 500;
//        int y = 500;
//        int turn = 45;
//        double distance = 1.618;
//        for (var k = 0; k < 45; k++) {
//            double theta = k * turn % 360.0;
//            double segmentLength = k * distance;
//            int endX = (int) (x + segmentLength * Math.cos(theta * Math.PI / 180));
//            int endY = (int) (y + segmentLength * Math.sin(theta * Math.PI / 180));
//
//
//        }
//    }

    double distanceFromCenter(int x, int y) {
        return Math.sqrt(Math.pow(x - 500, 2) + Math.pow(y - 500, 2));
    }
}
