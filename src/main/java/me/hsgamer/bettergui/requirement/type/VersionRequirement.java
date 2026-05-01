package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionRequirement extends BaseRequirement<VersionRequirement.Version> {
  private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(\\.(\\d+))?");

  public VersionRequirement(RequirementBuilder.Input input) {
    super(input);
  }

  @Override
  protected Version convert(Object value, UUID uuid) {
    String replaced = StringReplacerApplier.replace(String.valueOf(value).trim(), uuid, this);
    Matcher versionMatcher = VERSION_PATTERN.matcher(replaced);
    if (versionMatcher.find()) {
      int major = Integer.parseInt(versionMatcher.group(1));
      int minor = Integer.parseInt(versionMatcher.group(2));
      int patch = Optional.ofNullable(versionMatcher.group(4)).filter(s -> !s.isEmpty()).map(Integer::parseInt).orElse(0);
      if (major == 1) {
        return new Version(minor, patch);
      } else {
        return new Version(major, minor, patch);
      }
    } else {
      return Validate.getNumber(replaced)
        .map(BigDecimal::intValue)
        .map(Version::new)
        .orElseGet(() -> {
          MessageUtils.sendMessage(uuid, BetterGUI.getInstance().get(MessageConfig.class).getInvalidNumber(replaced));
          return new Version(0);
        });
    }
  }

  @Override
  protected Result checkConverted(UUID uuid, Version value) {
    return VersionUtils.compare(value.major, value.minor, value.patch) >= 0 ? Result.success() : Result.fail();
  }

  public static final class Version {
    final int major;
    final int minor;
    final int patch;

    private Version(int major) {
      this(major, VersionUtils.getMinorVersion(), VersionUtils.getPatchVersion());
    }

    private Version(int major, int minor) {
      this(major, minor, VersionUtils.getPatchVersion());
    }

    private Version(int major, int minor, int patch) {
      this.major = major;
      this.minor = minor;
      this.patch = patch;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Version version = (Version) o;
      return major == version.major && minor == version.minor && patch == version.patch;
    }

    @Override
    public int hashCode() {
      return Objects.hash(major, minor, patch);
    }
  }
}
