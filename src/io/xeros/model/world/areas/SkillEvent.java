package io.xeros.model.world.areas;

import io.xeros.content.PlayerEmotes;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;
import lombok.Getter;

@Getter
public class SkillEvent extends Event<Player> {

    private final Skill skill;
    private final Player player;
    private final PlayerEmotes.SKILLCAPE_ANIMATION_DATA emotes;

    public SkillEvent(Player player, Skill skill, PlayerEmotes.SKILLCAPE_ANIMATION_DATA skillCapeData) {
        super("skilling", player, 1);
        this.skill = skill;
        this.player = player;
        this.emotes = skillCapeData;
        getPlayer().startAnimation(getEmotes().getAnimation());
        getPlayer().gfx0(getEmotes().getGraphic());
    }

    @Override
    public void execute() {
        if (getPlayer().isMoving) {
            stop();
            return;
        }
        if (!getPlayer().isOnline()) {
            stop();
            return;
        }
        if (super.getElapsedTicks() % getEmotes().getDelay() == 0) {
            getPlayer().stopMovement();
            getPlayer().startAnimation(getEmotes().getAnimation());
            getPlayer().gfx0(getEmotes().getGraphic());
            getPlayer().lastPerformedEmote = System.currentTimeMillis();
            attachment.getPA().addSkillXPMultiplied(1 + ((double) 1 / 10), getSkill().getId(), true);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (attachment != null) {
            attachment.startAnimation(65535);
        }
    }
}
