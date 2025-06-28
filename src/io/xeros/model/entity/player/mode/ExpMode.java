package io.xeros.model.entity.player.mode;

import com.google.common.collect.ImmutableList;
import io.xeros.model.entity.player.mode.group.ExpModeType;

import java.util.Arrays;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 27/12/2023
 */
public class ExpMode {

    protected final ExpModeType type;

    public ExpMode(ExpModeType type) {
        this.type = type;
    }

    public ExpModeType getType() {
        return type;
    }

}
