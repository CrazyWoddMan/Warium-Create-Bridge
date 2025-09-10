package crazywoddman.warium_create.block.converter;

import crazywoddman.warium_create.Config;
import crazywoddman.warium_create.util.WariumCreateUtil;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate.SpeedLevel;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ConverterOutBlockEntity extends SpeedGaugeBlockEntity {

    public ConverterOutBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private final int defaultStress = Config.SERVER.defaultStress.get();
    private final int defaultSpeed = Config.SERVER.defaultSpeed.get();
    private final int maxThrottle = Config.SERVER.maxThrottle.get();
    private int lastThrottle;

    private void updateKineticPower() {
        getPersistentData().putDouble(
            "KineticPower",
            (double) Math.round(Math.abs(getSpeed()) / this.defaultSpeed / this.maxThrottle * this.lastThrottle * 10) / 10
        );
        sendData();
    }

    private void updateDialTarget() {
        this.dialTarget = getDialTarget(getSpeed() / this.maxThrottle * lastThrottle);
    }

    @Override
    public void tick() {
        super.tick();

        int throttle = Math.abs(WariumCreateUtil.getThrottle(this, this.maxThrottle));

        if (this.lastThrottle != throttle) {
            this.lastThrottle = throttle;

            if (this.level.isClientSide)
                return;

            KineticNetwork network = getOrCreateNetwork();
            
            if (network != null)
                network.updateStressFor(this, calculateStressApplied());

            this.dialTarget = getDialTarget(getSpeed() / this.maxThrottle * throttle);
            updateDialTarget();
            updateKineticPower();
        }

    }

    @Override
	public void onSpeedChanged(float prevSpeed) {
		super.onSpeedChanged(prevSpeed);

		updateDialTarget();
        this.color = Color
            .mixColors(SpeedLevel.of(this.speed)
			.getColor(), 0xffffff, .25f);
        updateKineticPower();
        setChanged();
	}

    @Override
    public float calculateStressApplied() {
        float impact = (float) this.defaultStress / this.defaultSpeed / this.maxThrottle * this.lastThrottle;
        this.lastStressApplied = impact;
		return impact;
    }

    @Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.translate("gui.gauge.info_header")
			.style(ChatFormatting.AQUA)
			.forGoggles(tooltip);
		Lang.translate("gui.speedometer.title")
			.style(ChatFormatting.GRAY)
			.forGoggles(tooltip);
		SpeedLevel.getFormattedSpeedText(getSpeed() / this.maxThrottle * this.lastThrottle, this.overStressed)
			.forGoggles(tooltip);
        addStressImpactStats(tooltip, calculateStressApplied());

		return true;
	}
}
