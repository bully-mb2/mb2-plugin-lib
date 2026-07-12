# MB2 Plugin Lib
<img align="right" width="115" height="115" src="https://user-images.githubusercontent.com/86576295/167314810-c9b6a021-6a41-463f-b35f-6ac3b825be7a.png"></img>
MB2 Plugin Lib defines standard classes derived from the [MB2 dedicated server](https://moviebattles.fandom.com/wiki/Installation_%26_Troubleshooting_Guide) game logs and provides the connective tissue needed for plugins to work with the [MB2 Plugin System](https://github.com/bully-mb2/mb2-plugin-system)

## For plugin developers
To get started developing plugins you must install this project using maven.
```
mvn install
```
Then you can use it in your own project by importing it as a dependency
```pom
<dependency>
    <groupId>com.taivas</groupId>
    <artifactId>mb2-plugin-lib</artifactId>
    <version>1.8.2</version>
</dependency>
```
Example usage: [MB2 Plugin Example](https://github.com/bully-mb2/mb2-plugin-example)

## License
MB2 Plugin Lib is licensed under GPLv2 as free software. You are free to use, modify and redistribute MB2 Log Reader Schema following the terms in LICENSE.txt
