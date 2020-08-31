# BetterGUI ![Spiget Version](https://img.shields.io/spiget/version/75620?color=orange&label=version) [![Build Status](https://ci.codemc.io/job/BetterGUI-MC/job/BetterGUI/badge/icon)](https://ci.codemc.io/job/BetterGUI-MC/job/BetterGUI/) [![Discord](https://img.shields.io/discord/660795353037144064)](https://discord.gg/9m4GdFD) [![Javadocs](https://img.shields.io/badge/javadocs-link-green)](https://bettergui-mc.github.io/BetterGUI)
## Feature
* Modular design (Base plugin with addons)
* Simple settings
* Animated Icons
* Priority in icons
* Special commands
* View & Click Requirements
* Cooldown
## Building
1. Clone this repo
2. Open the folder in terminal
3. Type `mvn clean install`
4. Get the final file in `/target/`
## For Developer
### Include in your Maven project
* Add this in your `<repositories>`
```xml
    <repository>
      <id>codemc-repo</id>
      <url>https://repo.codemc.io/repository/maven-public/</url>
    </repository>
```
* Add this in your `<dependencies>`
```xml
    <dependency>
      <groupId>me.HSGamer</groupId>
      <artifactId>BetterGUI</artifactId>
      <version>VERSION</version>
      <scope>provided</scope>
    </dependency>
```
### Create an addon
`TODO`
