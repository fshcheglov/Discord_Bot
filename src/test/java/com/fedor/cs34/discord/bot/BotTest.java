package com.fedor.cs34.discord.bot;

import com.fedor.cs34.discord.bot.dao.AbstractDAOTest;
import com.fedor.cs34.discord.bot.util.data.system.Coordinates;
import com.fedor.cs34.discord.bot.jda.JDAObjects;
import com.fedor.cs34.discord.bot.jda.JDATesting;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BotTest extends AbstractDAOTest {
    @Test
    public void empireList() throws Exception {
        var testMember = JDAObjects.getMember("Test User", "0000", "-1");

        var nation = createNation(new Coordinates(0, 0), "2", "Nation 1");

        var listener = new BotListener(dataAccess);
        var result = JDATesting.testMessageReceivedEvent(listener, "!EmpireList", testMember);
        assertThat(result.getContentStripped(), equalTo("**Nation name:** " + nation.nationName + " | **Owner:** <@" + nation.ownerID + ">\n"));


        var nation2 = createNation(new Coordinates(0, 0), "2347923874239", "Nation 2");
        var nation3 = createNation(new Coordinates(0, 2), "64980953845", "Nation 3");
        var nation4 = createNation(new Coordinates(3, 0), "4723847298347923", "Nation 4");
        var result2 = JDATesting.testMessageReceivedEvent(listener, "!EmpireList", testMember);

        assertThat(result2.getContentStripped(), equalTo(
                        "**Nation name:** " + nation.nationName + " | **Owner:** <@" + nation.ownerID + ">\n" +
                                "**Nation name:** " + nation2.nationName + " | **Owner:** <@" + nation2.ownerID + ">\n" +
                                "**Nation name:** " + nation3.nationName + " | **Owner:** <@" + nation3.ownerID + ">\n" +
                                "**Nation name:** " + nation4.nationName + " | **Owner:** <@" + nation4.ownerID + ">\n"
                )
        );
    }

    @Test
    public void empireInfo() throws Exception {
        var member = JDAObjects.getMember("Member", "0000", "215");
        var member2 = JDAObjects.getMember("Member2", "1453", "439248");
        var nation = createNation(new Coordinates(0, 0), member.getId(), "Foo");

        var listener = new BotListener(dataAccess);

        var result = JDATesting.testMessageReceivedEvent(listener, "!EmpireInfo " + member.getUser().getAsMention(), member);
        assertThat(result.getContentStripped(), equalTo(
                "**Nation name: ** " + nation.nationName + "\n" +
                        "**Leader: ** " + nation.leader.getName() + "\n" +
                        "**Species: ** " + nation.species.getName() + "\n\n" +

                        "**Government: ** " + nation.government.getName() + "\n" +
                        "**Economic System:** " + nation.economicType.getName() + "\n\n" +

                        "**Resource Points: **" + nation.development.resourcePoints + "\n" +
                        "**Economic Points: **" + nation.development.economicPoints + "\n" +
                        "**Manpower Points: **" + nation.development.manpowerPoints + "\n\n" +

                        "**Population: **" + nation.population + "\n\n" +

                        "**Stability: **" + nation.stability + "\n" +
                        "**Centralization: **" + nation.centralization + "\n" +
                        "**Approval: **" + nation.approval + "\n\n" +

                        "**Capital: ** " + nation.capital.name + "\n"));

        var result2 = JDATesting.testMessageReceivedEvent(listener, "!EmpireInfo " + member2.getUser().getAsMention(), member);
        assertThat(result2.getContentStripped(), equalTo("**No Owned Nations!**"));
    }

    @Test
    public void createNation() throws Exception {
        var member = JDAObjects.getMember("Member", "0000", "389");
        var listener = new BotListener(dataAccess);
        var template = JDATesting.testMessageReceivedEvent(listener, "!CreateNation", member);
        assertThat(template.getContentStripped(), equalTo(
                "First, input capital planet details according to the following template:\n" +
                        """
                                Planet Name:
                                Planet Type:
                                                
                                Planet Resources:
                                Planet Population:
                                Planet Development:

                                Planet Size:
                                Planet X-Coordinate:
                                Planet Y-Coordinate:
                                """)
        );
        var createdPlanet = JDATesting.testMessageReceivedEvent(listener,
                """
                        Planet Name: Foo
                        Planet Type: Bar
                                        
                        Planet Resources: 10
                        Planet Population: 18
                        Planet Development: 16

                        Planet Size: 9
                        Planet X-Coordinate: 0
                        Planet Y-Coordinate: 0
                        """, member);

        assertThat(createdPlanet.getContentStripped(), equalTo("Capital planet successfully created.\n"));
        var testPlanet = dataAccess.planetDAO.getById(1);

        assertThat(testPlanet.name, equalTo("Foo"));
        assertThat(testPlanet.type, equalTo("Bar"));
        assertThat(testPlanet.resources, equalTo(10));
        assertThat(testPlanet.population, equalTo(18));
        assertThat(testPlanet.development, equalTo(16));
        assertThat(testPlanet.size, equalTo(9));
        assertThat(testPlanet.star.system.coordinates.x, equalTo(0));
        assertThat(testPlanet.star.system.coordinates.y, equalTo(0));

        assertThat(Bot.inputNationTemplate(), equalTo(
                "Please input nation details according to the following template:\n" +
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
                                """)
        );
        listener.planet = testPlanet;

        var nationInput = JDATesting.testMessageReceivedEvent(listener,
                """
                        Nation Name: Foo
                        Leader Name: Bar
                        Species: Baz

                        Government Type: Oligarchic Democracy
                        Economic System: Free Market Capitalism

                        Resource Points: 10
                        Economic Points: 11
                        Manpower Points: 12

                        Population: 200

                        Stability: 60.4
                        Centralization: 73.9
                        Approval: 80
                        """, member);

        assertThat(nationInput.getContentStripped(), equalTo("**Nation Registered**"));
        var createdNation = dataAccess.nationDAO.getById(1);

        assertThat(createdNation.nationName, equalTo("Foo"));
        assertThat(createdNation.leader.getName(), equalTo("Bar"));
        assertThat(createdNation.species.getName(), equalTo("Baz"));
        assertThat(createdNation.government.getName(), equalTo("Oligarchic Democracy"));
        assertThat(createdNation.economicType.getName(), equalTo("Free Market Capitalism"));
        assertThat(createdNation.development.resourcePoints, equalTo(10));
        assertThat(createdNation.development.economicPoints, equalTo(11));
        assertThat(createdNation.development.manpowerPoints, equalTo(12));
        assertThat(createdNation.population, equalTo(200));
        assertThat(createdNation.stability, equalTo(60.4));
        assertThat(createdNation.centralization, equalTo(73.9));
        assertThat(createdNation.approval, equalTo(80.0));

        assertThat(createdNation.capital, equalToObject(testPlanet));
        assertThat(createdNation.capital.star, equalToObject(testPlanet.star));
        assertThat(createdNation.capital.star.system, equalToObject(testPlanet.star.system));

        var planetCount = dataAccess.planetDAO.count();
        var starCount = dataAccess.starDAO.count();
        var systemCount = dataAccess.starSystemDAO.count();

        var member2 = JDAObjects.getMember("Member", "0000", "389");
        JDATesting.testMessageReceivedEvent(listener, "!CreateNation", member2);
        var response = JDATesting.testMessageReceivedEvent(listener, "!EmpireList", member2);
        assertThat(response.getContentStripped(), equalTo("**Sorry, you didn't follow the template.**"));

        assertThat(planetCount, equalTo(dataAccess.planetDAO.count()));
        assertThat(starCount, equalTo(dataAccess.starDAO.count()));
        assertThat(systemCount, equalTo(dataAccess.starSystemDAO.count()));

        JDATesting.testMessageReceivedEvent(listener, "!CreateNation", member2);
        JDATesting.testMessageReceivedEvent(listener,
                        """
                        Planet Name: Foo
                        Planet Type: Bar
                                        
                        Planet Resources: 10
                        Planet Population: 18
                        Planet Development: 16

                        Planet Size: 9
                        Planet X-Coordinate: 1
                        Planet Y-Coordinate: 1
                        """, member2);
        var responseError = JDATesting.testMessageReceivedEvent(listener, "!CreateNation", member2);
        assertThat(responseError.getContentStripped(),equalTo("**Sorry, you didn't follow the template.**"));

        assertThat(planetCount, equalTo(dataAccess.planetDAO.count()));
        assertThat(starCount, equalTo(dataAccess.starDAO.count()));
        assertThat(systemCount, equalTo(dataAccess.starSystemDAO.count()));
    }
}
