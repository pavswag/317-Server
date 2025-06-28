package io.xeros.content.minigames.xeric;

import io.xeros.content.taskmaster.TaskMasterKills;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Patrity, Arithium, Fox News
 *
 */
public class Xeric {

	public static boolean xericEnabled;
	private int xeric_kills;
	private int xeric_wave;

	public Xeric() {
	}

	private int index;
	private int wavelength = 0;
	private int count = 15;


	private List <Player> xericTeam = new ArrayList<>();
	private final List<NPC> spawns = new ArrayList<>();

	public void spawn() {
		if (wavelength >= 20) {
			stop();
			return;
		}
		wavelength++;

		if (wavelength != 0) {
			for (Player xericTeam : xericTeam)
				xericTeam.sendMessage("You are now on wave " + (xericTeam.getXeric().xeric_wave + 1) + ". - Total damage done: " + xericTeam.xericDamage + ".", 255);
		}

		setKillsRemaining(getXericTeam().size() > 1 ? getXericTeam().size() + 2 : 3);

		for (int i = 0; i < xeric_kills; i++) {

			int npcType = XericWave.wave[Misc.random(XericWave.wave.length-1)];
			int x = 3232 + (Misc.random(-4,4));
			int y = 4832 + (Misc.random(-4,4));

			NPC npc = NPCSpawning.spawn(npcType, x,y, getIndex() * 4, 1, XericWave.getMax(npcType), true);

			npc.getHealth().setMaximumHealth(XericWave.getHp(npcType));
			npc.getHealth().setCurrentHealth(npc.getHealth().getMaximumHealth());
			npc.getBehaviour().setRespawn(false);

			spawns.add(npc);
		}
	}

	public void stop() {
		for (Player xericTeam : xericTeam) {
			Player p = PlayerHandler.getPlayerByLoginName(xericTeam.getLoginNameLower());
			if (wavelength >= 10) {
				XericRewards.giveReward(p.xericDamage, p , wavelength);
			} else {
				p.sendMessage("You didn't work hard enough to warrant a reward.");
			}
			p.getPA().movePlayer(1234,3567,0);
			p.getDH().sendStatement("Congratulations on finishing the Trials of Xeric!");
			p.nextChat = 0;
			p.setRunEnergy(100, true);
			p.getPA().removeWalkableInterface();
			for (TaskMasterKills taskMasterKills : xericTeam.getTaskMaster().taskMasterKillsList) {
				if (taskMasterKills.getDesc().contains("Trials")) {
					taskMasterKills.incrementAmountKilled(1);
					xericTeam.getTaskMaster().trackActivity(xericTeam, taskMasterKills);
				}
			}
			CycleEventHandler.getSingleton().stopEvents(xericTeam);
		}
		killAllSpawns();
		XericLobby.removeGame(this);
	}

	public void deathStop() {
		for (Player xericTeam : xericTeam) {
			Player p = PlayerHandler.getPlayerByLoginName(xericTeam.getLoginNameLower());
			if (wavelength >= 10) {
				XericRewards.giveReward(p.xericDamage, p , wavelength);
			} else {
				p.sendMessage("You didn't work hard enough to warrant a reward.");
			}
			p.getPA().movePlayer(1234, 3567,0);
			p.nextChat = 0;
			p.setRunEnergy(100, true);
			p.getPA().removeWalkableInterface();
			CycleEventHandler.getSingleton().stopEvents(xericTeam);
		}
		killAllSpawns();
		XericLobby.removeGame(this);
	}

	public void leaveGame(Player player, boolean death) {
		player.getPA().movePlayer(1234, 3567,0);
		player.getPA().removeWalkableInterface();
		player.getDH().sendStatement((death ? "Unfortunately you died" : "You've left the game") + " on wave " + (player.getXeric().xeric_wave + 1) + ". Better luck next time.");
		player.nextChat = 0;
		removePlayer(player);
	}

	public void HandleDeath(Player player, boolean death) {
		player.getPA().movePlayer(3232, 4843);

		count -= 1;
		for (Player player1 : getXericTeam()) {
			player1.sendMessage(player.getDisplayName() + " has died your team has " + count + " life's remaining.");
		}

		if (count == 0) {
			for (Player player1 : xericTeam) {
				player1.sendMessage("Your team has ran out of lives! the game has been added");
			}
			this.deathStop();
			return;
		}

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer event) {
				if (player.getXeric() == null) {
					event.stop();
				}

				if (!getXericTeam().contains(player)) {
					event.stop();
				}

				player.sendMessage("You have been revived!");
				player.getPA().movePlayer(3232, 4837);
				event.stop();
			}

			@Override
			public void onStopped() {

			}
		}, 30);
	}

	public void killAllSpawns() {
		for (NPC npc : getSpawns()) {
			if (npc != null) {
				NPCHandler.npcs[npc.getIndex()] = null;
			}
		}
		for (int i = 0; i < NPCHandler.npcs.length; i++) {
			NPC npc = NPCHandler.npcs[i];
			if (npc != null) {
				if (Boundary.isIn(npc, Boundary.XERIC) && npc.getHeight() == index) {
					npc.unregister();
				}
			}
		}

		if (Boundary.getPlayersInBoundary(Boundary.XERIC) <= 1) {
			for (Player player : PlayerHandler.getPlayers()) {
				if (player.getXeric() != null) {
					player.getXeric().getXericTeam().remove(player);
					player.setXeric(null);
				}
			}
		}
	}

	public int getKillsRemaining() {
		return xeric_kills;
	}

	public void setKillsRemaining(int remaining) {
		this.xeric_kills = remaining;
	}

	public List<Player> getXericTeam() {
		return xericTeam;
	}

	public List<NPC> getSpawns() {
		return spawns;
	}

	public void setXericTeam(List<Player> list) {
		this.xericTeam = new ArrayList<>(list);
	}

	public void removePlayer(Player player) {
		xericTeam.remove(player);
		if (xericTeam.size() == 0) {
			this.stop();
		}
	}

	public void addPlayer(Player player) {
		xericTeam.add(player);
		player.setXeric(this);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int z) {
		this.index = z;
	}

	public int getWaveId() {
		return xeric_wave;
	}

	public void incWaveID() {
		xeric_wave++;
	}
	public void Tick() {
		for (Player player : PlayerHandler.getPlayers()) {
			if (player.getXeric() != null) {
				player.getXeric().getXericTeam().remove(player);
				player.setXeric(null);
			}
		}
	}

}
