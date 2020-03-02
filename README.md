[![Build Status](https://ci.codemc.io/job/BetterGUI-MC/job/BetterGUI/badge/icon)](https://ci.codemc.io/job/BetterGUI-MC/job/BetterGUI/) [![Discord](https://img.shields.io/discord/660795353037144064)](https://discord.gg/8mJJMqH)
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
      <id>codemc-snapshots</id>
      <url>https://repo.codemc.io/repository/maven-snapshots/</url>
    </repository>
```
* Add this in your `<dependencies>`
```xml
    <dependency>
      <groupId>me.HSGamer</groupId>
      <artifactId>BetterGUI</artifactId>
      <version>1.1-SNAPSHOT</version>
      <scope>provided</scope>
    </dependency>
```
### Create an addon
`TODO`
