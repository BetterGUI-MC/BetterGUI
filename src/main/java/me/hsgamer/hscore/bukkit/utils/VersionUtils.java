package me.hsgamer.hscore.bukkit.utils;

import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The helper class for server versions
 */
public final class VersionUtils {
  private static final int MAJOR_VERSION;
  private static final int MINOR_VERSION;
  private static final int PATCH_VERSION;
  private static final boolean IS_CRAFTBUKKIT_MAPPED;
  private static final String CRAFTBUKKIT_PACKAGE_VERSION;

  static {
    Matcher versionMatcher = Pattern.compile("MC: (\\d+)\\.(\\d+)(\\.(\\d+))?").matcher(Bukkit.getVersion());
    if (versionMatcher.find()) {
      int majorVersion = Integer.parseInt(versionMatcher.group(1));
      int minorVersion = Integer.parseInt(versionMatcher.group(2));
      int patchVersion = Optional.ofNullable(versionMatcher.group(4)).filter(s -> !s.isEmpty()).map(Integer::parseInt).orElse(0);
      if (majorVersion == 1) {
        MAJOR_VERSION = minorVersion;
        MINOR_VERSION = patchVersion;
        PATCH_VERSION = 0;
      } else {
        MAJOR_VERSION = majorVersion;
        MINOR_VERSION = minorVersion;
        PATCH_VERSION = patchVersion;
      }
    } else {
      MAJOR_VERSION = -1;
      MINOR_VERSION = -1;
      PATCH_VERSION = -1;
    }

    Matcher packageMatcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
    IS_CRAFTBUKKIT_MAPPED = packageMatcher.find();
    if (IS_CRAFTBUKKIT_MAPPED) {
      CRAFTBUKKIT_PACKAGE_VERSION = packageMatcher.group();
    } else {
      CRAFTBUKKIT_PACKAGE_VERSION = "";
    }
  }

  private VersionUtils() {
    // EMPTY
  }

  /**
   * Get the major version of the server
   *
   * @return the version
   */
  public static int getMajorVersion() {
    return MAJOR_VERSION;
  }

  /**
   * Get the minor version of the server
   *
   * @return the version
   */
  public static int getMinorVersion() {
    return MINOR_VERSION;
  }

  /**
   * Get the patch version of the server
   *
   * @return the version
   */
  public static int getPatchVersion() {
    return PATCH_VERSION;
  }

  /**
   * Compare the server version with the given version
   *
   * @param majorVersion the major version
   * @param minorVersion the minor version
   * @param patchVersion the patch version
   *
   * @return 0 if the versions are the same, negative if the server version is lower, positive if the server version is higher
   */
  public static int compare(int majorVersion, int minorVersion, int patchVersion) {
    int compare = Integer.compare(MAJOR_VERSION, majorVersion);
    if (compare == 0) {
      compare = Integer.compare(MINOR_VERSION, minorVersion);
    }
    if (compare == 0) {
      compare = Integer.compare(PATCH_VERSION, patchVersion);
    }
    return compare;
  }

  /**
   * Compare the server version with the given version
   *
   * @param majorVersion the major version
   * @param minorVersion the minor version
   *
   * @return 0 if the versions are the same, negative if the server version is lower, positive if the server version is higher
   */
  public static int compare(int majorVersion, int minorVersion) {
    return compare(majorVersion, minorVersion, getPatchVersion());
  }

  /**
   * Compare the server version with the given version
   *
   * @param majorVersion the major version
   *
   * @return 0 if the versions are the same, negative if the server version is lower, positive if the server version is higher
   */
  public static int compare(int majorVersion) {
    return compare(majorVersion, getMinorVersion());
  }

  /**
   * Check if the server major version is at least the given major version
   *
   * @param majorVersion the major version to check
   *
   * @return true if it is
   */
  public static boolean isAtLeast(int majorVersion) {
    return compare(majorVersion) >= 0;
  }

  /**
   * Check if the server version is at least the given version
   *
   * @param majorVersion the major version to check
   * @param minorVersion the minor version to check
   *
   * @return true if it is
   */
  public static boolean isAtLeast(int majorVersion, int minorVersion) {
    return compare(majorVersion, minorVersion) >= 0;
  }

  /**
   * Check if the server version is at the given version
   *
   * @param majorVersion the major version to check
   *
   * @return true if it is
   */
  public static boolean isAt(int majorVersion) {
    return compare(majorVersion) == 0;
  }

  /**
   * Check if the server version is at the given version
   *
   * @param majorVersion the major version to check
   * @param minorVersion the minor version to check
   *
   * @return true if it is
   */
  public static boolean isAt(int majorVersion, int minorVersion) {
    return compare(majorVersion, minorVersion) == 0;
  }

  /**
   * Check if the server version is newer than the given version
   *
   * @param majorVersion the major version to check
   *
   * @return true if it is
   */
  public static boolean isNewerThan(int majorVersion) {
    return compare(majorVersion) > 0;
  }

  /**
   * Check if the server version is newer than the given version
   *
   * @param majorVersion the major version to check
   * @param minorVersion the minor version to check
   *
   * @return true if it is
   */
  public static boolean isNewerThan(int majorVersion, int minorVersion) {
    return compare(majorVersion, minorVersion) > 0;
  }

  /**
   * Check if the server version is lower than the given version
   *
   * @param majorVersion the major version to check
   *
   * @return true if it is
   */
  public static boolean isLowerThan(int majorVersion) {
    return compare(majorVersion) < 0;
  }

  /**
   * Check if the server version is lower than the given version
   *
   * @param majorVersion the major version to check
   * @param minorVersion the minor version to check
   *
   * @return true if it is
   */
  public static boolean isLowerThan(int majorVersion, int minorVersion) {
    return compare(majorVersion, minorVersion) < 0;
  }

  /**
   * Check if the server is using CraftBukkit mappings.
   * CraftBukkit mappings are usually used in Spigot and old Paper versions.
   * It's useful to check whether the server is using CraftBukkit mappings (Spigot, old Paper) or new Paper mappings.
   * <a href="https://forums.papermc.io/threads/important-dev-psa-future-removal-of-cb-package-relocation.1106/">More info</a>
   *
   * @return true if it is
   */
  public static boolean isCraftBukkitMapped() {
    return IS_CRAFTBUKKIT_MAPPED;
  }

  /**
   * Get the CraftBukkit package version
   *
   * @return the CraftBukkit package version, or empty if {@link #isCraftBukkitMapped()} returns false
   */
  public static String getCraftBukkitPackageVersion() {
    return CRAFTBUKKIT_PACKAGE_VERSION;
  }
}