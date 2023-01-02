package com.fedor.cs34.discord.bot;

import com.fedor.cs34.discord.bot.dao.nation.*;
import com.fedor.cs34.discord.bot.dao.system.PlanetDAO;
import com.fedor.cs34.discord.bot.dao.system.StarDAO;
import com.fedor.cs34.discord.bot.dao.system.StarTypeDAO;
import com.fedor.cs34.discord.bot.dao.system.SystemDAO;

import java.sql.Connection;

public class DataAccess {
    public final Connection connection;
    public final EconomicDAO economicDAO;
    public final GovernmentDAO governmentDAO;
    public final LeaderDAO leaderDAO;
    public final NationDAO nationDAO;
    public final SpeciesDAO speciesDAO;
    public final PlanetDAO planetDAO;
    public final StarDAO starDAO;
    public final StarTypeDAO starTypeDAO;
    public final SystemDAO systemDAO;


    public DataAccess(Connection connection) {
        this.connection = connection;
        this.economicDAO = new EconomicDAO(connection);
        this.governmentDAO = new GovernmentDAO(connection);
        this.leaderDAO = new LeaderDAO(connection);
        this.nationDAO = new NationDAO(connection);
        this.speciesDAO = new SpeciesDAO(connection);
        this.planetDAO = new PlanetDAO(connection);
        this.starDAO = new StarDAO(connection);
        this.starTypeDAO = new StarTypeDAO(connection);
        this.systemDAO = new SystemDAO(connection);
    }


}
