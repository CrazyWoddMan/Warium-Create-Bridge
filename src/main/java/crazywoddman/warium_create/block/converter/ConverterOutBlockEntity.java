package crazywoddman.warium_create.block.converter;

import crazywoddman.warium_create.Config;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate.SpeedLevel;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.foundation.utility.Lang;

import net.mcreator.valkyrienwarium.block.entity.VehicleControlNodeBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        int throttle = getThrottle();
        double kineticPower = Math.round(Math.abs(getSpeed()) / maxThrottle * throttle / 2);
        if (throttle != lastThrottle) {
            KineticNetwork network = getOrCreateNetwork();
            if (network != null) {
                network.updateStressFor(this, calculateStressApplied());
            }
            lastThrottle = throttle;
        }
        getPersistentData().putDouble("KineticPower", kineticPower);
        sendData();
    }

    private int getThrottle() {
        CompoundTag data = getPersistentData();

        if (data.contains("ControlX")) {
            BlockEntity controlNode =
                level != null ?
                level.getBlockEntity(new BlockPos(
                    data.getInt("ControlX"),
                    data.getInt("ControlY"),
                    data.getInt("ControlZ")
                ))
                : null;

            if (controlNode != null && controlNode instanceof VehicleControlNodeBlockEntity) {
                int throttle = controlNode.getPersistentData().getInt("Throttle");
                String key = data.getString("Key");

                if (key.isEmpty() || (throttle > 0 && key.equals("Throttle+")) || (throttle < 0 && key.equals("Throttle-")))
                    return Math.abs(throttle);
            }
        }
        
        return maxThrottle;
    }

    @Override
    public float calculateStressApplied() {
        float impact = (float) (defaultStress / defaultSpeed / maxThrottle * getThrottle());
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
		SpeedLevel.getFormattedSpeedText(Math.abs(getSpeed()) / maxThrottle * getThrottle(), overStressed)
			.forGoggles(tooltip);
        addStressImpactStats(tooltip, calculateStressApplied());

		return true;
	}
}
