package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Arrays;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.addon.AddonDescription;
import me.hsgamer.bettergui.util.BukkitUtils;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.TestCase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public final class GetAddonsCommand extends BukkitCommand {

  private final TestCase<CommandSender> testCase = new TestCase<CommandSender>()
      .setPredicate(commandSender -> commandSender.hasPermission(Permissions.ADDONS))
      .setSuccessConsumer(this::send)
      .setFailConsumer(commandSender -> CommonUtils.sendMessage(commandSender,
          getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION)));

  public GetAddonsCommand() {
    super("addons", "Get the loaded addons", "/addons",
        Arrays.asList("menuaddons", "getmenuaddons"));
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    return testCase.setTestObject(sender).test();
  }

  private void send(CommandSender commandSender) {
    if (BukkitUtils.isSpigot()) {
      ComponentBuilder builder = new ComponentBuilder(CommonUtils.colorize(
          BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.PREFIX)
              + "&b&lLoaded Addons: "));
      getInstance().getAddonManager().getLoadedAddons().forEach((name, addon) -> {
        builder.append(new TextComponent(name));
        builder.color(ChatColor.GREEN);
        AddonDescription addonDescription = addon.getDescription();
        String author = CommonUtils
            .colorize("&aAuthors: &f" + String.join(", ", addonDescription.getAuthors()));
        String description = CommonUtils
            .colorize("&aDescription: &f" + addonDescription.getDescription());
        String version = CommonUtils.colorize("&aVersion: &f" + addonDescription.getVersion());
        builder.event(new HoverEvent(Action.SHOW_TEXT, Arrays.asList(
            new TextComponent(author), new TextComponent("\n"),
            new TextComponent(version), new TextComponent("\n"),
            new TextComponent(description)
        ).toArray(new TextComponent[3])));
        builder.append(" ");
      });
      commandSender.spigot().sendMessage(builder.create());
    } else {
      CommonUtils.sendMessage(commandSender, "&b&lLoaded Addons: &a" + String
          .join("&f, &a", getInstance().getAddonManager().getLoadedAddons().keySet()));
    }
  }
}
