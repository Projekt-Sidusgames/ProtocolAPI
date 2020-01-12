package net.crytec.libs.protocol.scoreboard.api;

import java.util.HashMap;
import java.util.UUID;
import net.crytec.libs.protocol.scoreboard.PlayerBoard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public interface PlayerBoardManager {

  void setBoard(final Player player, final ScoreboardHandler handler);

  PlayerBoard getBoard(final Player player);

  void resetBoard(final Player player);

  void setPrefix(final Player player, final String prefix);

  void setSuffix(final Player player, final String suffix);

  void setPriority(Player player, int priority);

  HashMap<UUID, Team> getUsers();
}
