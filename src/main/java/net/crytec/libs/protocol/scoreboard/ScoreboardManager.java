package net.crytec.libs.protocol.scoreboard;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.UUID;
import net.crytec.libs.protocol.scoreboard.api.PlayerBoardManager;
import net.crytec.libs.protocol.scoreboard.api.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.server.ServerLoadEvent.LoadType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager implements Listener, Runnable, PlayerBoardManager {

  private final HashMap<UUID, Team> users = Maps.newHashMap();
  private final HashMap<UUID, PlayerBoard> boards = Maps.newHashMap();

  private final Scoreboard titleboard;
  private final ScoreboardAPI api;

  protected ScoreboardManager(final ScoreboardAPI api) {
    this.api = api;
    ProtocolLibrary.getProtocolManager().addPacketListener(new TeamPacketListener(api.getHost(), this));

    this.titleboard = Bukkit.getScoreboardManager().getNewScoreboard();
    Bukkit.getPluginManager().registerEvents(this, api.getHost());

    final Objective obj = this.titleboard.registerNewObjective("sidebar", "dummy", " ");
    obj.setDisplaySlot(DisplaySlot.SIDEBAR);

    Bukkit.getScheduler().runTaskTimerAsynchronously(api.getHost(), this, 20, 5);
  }

  @Override
  public void addPlayer(final Player player, String priority) {
    if (this.users.containsKey(player.getUniqueId())) {
      this.unregisterPlayer(player);
    }

    String name = player.getName();

    if (name.length() > 14) {
      name = name.substring(0, 14);
    }

    if (priority.length() > 2) {
      priority = priority.substring(0, 2);
    }

    final Team team = this.titleboard.registerNewTeam(priority + name);

    team.setPrefix("");
    team.setSuffix("");
    team.setColor(ChatColor.WHITE);
    team.addEntry(player.getName());
    this.users.put(player.getUniqueId(), team);

    player.setScoreboard(this.titleboard);
    this.boards.put(player.getUniqueId(), new PlayerBoard(player));
  }

  @Override
  public void addPlayer(final Player player) {
    this.addPlayer(player, "0");
  }


  @Override
  public void unregisterPlayer(final Player player) {
    if (!this.users.containsKey(player.getUniqueId())) {
      return;
    }
    this.titleboard.getTeam(this.users.get(player.getUniqueId()).getName()).unregister();
    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    this.users.remove(player.getUniqueId());
    this.boards.get(player.getUniqueId()).deactivate();
    this.boards.remove(player.getUniqueId());
  }

  @Override
  public void setBoard(final Player player, final ScoreboardHandler handler) {
    final PlayerBoard board = this.getBoard(player);
    if (board.isActivated()) {
      board.deactivate();
    }
    board.setHandler(handler);
    board.activate();
  }


  @Override
  public PlayerBoard getBoard(final Player player) {
    return this.boards.get(player.getUniqueId());
  }

  @Override
  public void resetBoard(final Player player) {
    this.getBoard(player).deactivate();
  }

  @Override
  public void setPrefix(final Player player, final String prefix) {
    this.users.get(player.getUniqueId()).setPrefix(prefix);
  }

  @Override
  public void setSuffix(final Player player, final String suffix) {
    this.users.get(player.getUniqueId()).setSuffix(suffix);
  }

  @Override
  public HashMap<UUID, Team> getUsers() {
    return this.users;
  }

  private Scoreboard getScoreboard() {
    return this.titleboard;
  }

  @EventHandler
  public void handleReload(final ServerLoadEvent event) {
    if (event.getType() == LoadType.RELOAD) {
      Bukkit.getOnlinePlayers().forEach(this::addPlayer);
    }
  }

  @EventHandler
  public void setupBoardOnJoin(final PlayerJoinEvent event) {
    if (this.api.setupPlayersOnJoin()) {
      this.addPlayer(event.getPlayer());
    }
  }

  @EventHandler
  public void unregisterOnQuit(final PlayerQuitEvent event) {
    this.unregisterPlayer(event.getPlayer());
  }

  @Override
  public void run() {
    for (final PlayerBoard board : this.boards.values()) {
      if (!board.isActivated()) {
        continue;
      }
      board.update();
    }
  }
}