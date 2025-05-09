package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand implements SubCommand {

    private final SkyGiantsCommand parentCommand;

    public HelpCommand(SkyGiantsCommand parentCommand) {
        this.parentCommand = parentCommand;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        parentCommand.sendHelp(sender);
        return true;
    }
}
