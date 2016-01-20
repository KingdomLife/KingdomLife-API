# KingdomLife-API
A hookable API used to retrieve level and class type of player and items of certain rarity, class, and level. To retrieve items of any rarity, pass "any" as a rarity. This feature might be bugged.

To add as a dependency in Maven, download the KingdomLifeAPI-0.0.1-SNAPSHOT.jar and find the path to it.
Open up pom.xml and add a dependency as follows, replacing my path inside <systemPath></systemPath> with your path to your downloaded jar
  
  ```
  <dependency>
      <groupId>com.patrickzhong</groupId>
      <artifactId>KingdomLifeAPI</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <systemPath>C:/Users/Patrick/neworkspace/KingdomLifeAPI/target/KingdomLifeAPI-0.0.1-SNAPSHOT.jar</systemPath>
      <type>jar</type>
      <scope>system</scope>
  </dependency>
  ```
Then, import com.patrickzhong.kingdomlifeapi.KingdomLifeAPI. Retrieve the instance using getServer().getPluginManager().getPlugin("KingdomLifeAPI").

Please inform me of your hooked plugin so that I can sync enables and disables with my API.
