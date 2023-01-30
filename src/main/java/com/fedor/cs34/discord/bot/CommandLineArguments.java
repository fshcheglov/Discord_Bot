package com.fedor.cs34.discord.bot;

public class CommandLineArguments {
    private static final String databasePathArgument = "--database-path=";
    private static final String discordTokenArgument = "--discord-token=";

    public final String databasePath;
    public final String discordToken;

    private CommandLineArguments(String databasePath, String discordToken) {
        this.databasePath = databasePath;
        this.discordToken = discordToken;
    }

    public static CommandLineArguments parse(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected exactly two command line arguments: " +
                    databasePathArgument + " and " + discordTokenArgument);
        }
        String databasePath;
        if (args[0].startsWith(databasePathArgument)) {
            databasePath = args[0].substring(databasePathArgument.length());
        } else {
            throw new IllegalArgumentException("The first argument must be: " + databasePathArgument);
        }
        String discordToken;
        if (args[1].startsWith(discordTokenArgument)) {
            discordToken = args[1].substring(discordTokenArgument.length());
        } else {
            throw new IllegalArgumentException("The second argument must be: " + discordTokenArgument);
        }
        return new CommandLineArguments(databasePath, discordToken);
    }
}
