package io.xeros.model.cycleevent.impl;

import io.xeros.model.cycleevent.Event;
import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;

public class UpdateQuestTab extends Event<Object> {


	private static final int INTERVAL = Misc.toCycles(5, TimeUnit.SECONDS);

	
	public UpdateQuestTab() {
		super("", new Object(), INTERVAL);
	}	

	@Override
	public void execute() {
/*		PlayerHandler.nonNullStream().forEach(player -> {
			player.getQuestTab().updateInformationTab();
		});*/
	}
} 