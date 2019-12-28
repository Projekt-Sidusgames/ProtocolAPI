package net.crytec.libs.protocol.scoreboard;

import net.crytec.libs.protocol.scoreboard.api.PlayerBoardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardAPI {

  private final JavaPlugin host;
  private final ScoreboardManager boardManager;
  private final boolean autoSetupOnJoin;

  public ScoreboardAPI(final JavaPlugin host, final boolean registerPlayersOnJoin) {
    this.host = host;
    this.autoSetupOnJoin = registerPlayersOnJoin;
    this.boardManager = new ScoreboardManager(this);
  }

  public PlayerBoardManager getBoardManager() {
    return this.boardManager;
  }

  protected boolean setupPlayersOnJoin() {
    return this.autoSetupOnJoin;
  }

  protected JavaPlugin getHost() {
    return this.host;
  }

}
