package crazywoddman.warium_create.block;

import crazywoddman.warium_create.Config;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate.SpeedLevel;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ConverterOutBlockEntity extends StressGaugeBlockEntity {

    public ConverterOutBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private final double defaultStress = Config.SERVER.defaultStress.get();
    private final double defaultSpeed = Config.SERVER.defaultSpeed.get();

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        double kineticPower = Math.max(Math.round(Math.abs(getSpeed()) / (defaultSpeed / 5)) * getThrottle(), 0);
        KineticNetwork network = hasNetwork() ? getOrCreateNetwork() : null;
        float avaiblestress = network != null ? Math.round(network.calculateCapacity() - network.calculateStress()) : 0;
        dialTarget = SpeedGaugeBlockEntity.getDialTarget(((getThrottle() > 0 ? (float)getThrottle() : 0) * getSpeed() / 10));
        this.getPersistentData().putFloat("StressCapacity", avaiblestress);
        this.getPersistentData().putDouble("KineticPower", avaiblestress / (defaultStress / 2000) / Math.abs(getSpeed()) >= defaultSpeed / 5 ? kineticPower : 0);
        sendData();
    }

    private double getThrottle() {
        double controlX = getPersistentData().getDouble("ControlX");
        double controlY = getPersistentData().getDouble("ControlY");
        double controlZ = getPersistentData().getDouble("ControlZ");
        boolean hasLink = !(controlX == 0 && controlY == 0 && controlZ == 0);

        if (!hasLink) {
            return 10;
        }

        BlockPos controlPos = new BlockPos((int) controlX, (int) controlY, (int) controlZ);
        BlockEntity controlNode = level != null ? level.getBlockEntity(controlPos) : null;

        if (controlNode != null && controlNode.getPersistentData().contains("Throttle")) {
            return controlNode.getPersistentData().getDouble("Throttle");
        }
        
        return 0;
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        boolean isOverStressed = Math.round((getNetworkCapacity() - getNetworkStress())) / (defaultStress / 2000) / Math.abs(getSpeed()) < defaultSpeed / 5;

        if (isOverStressed && AllConfigs.client().enableOverstressedTooltip.get()) {
            Lang.translate("gui.stressometer.overstressed")
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip);
            List<Component> cutString = TooltipHelper.cutTextComponent(
                Lang.translateDirect("gui.contraptions.network_overstressed"), Palette.GRAY_AND_WHITE);
            for (Component line : cutString)
                Lang.builder().add(line.copy()).forGoggles(tooltip);
            return true;
        }
        return false;
    }

    @Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        boolean isOverStressed = Math.round((getNetworkCapacity() - getNetworkStress())) / (defaultStress / 2000) / Math.abs(getSpeed()) < defaultSpeed / 5;
        Lang.translate("gui.gauge.info_header")
			.style(ChatFormatting.AQUA)
			.forGoggles(tooltip);
		Lang.translate("gui.speedometer.title")
			.style(ChatFormatting.GRAY)
			.forGoggles(tooltip);
		SpeedLevel.getFormattedSpeedText(getSpeed() / 10 * (getThrottle() > 0 ? (float)getThrottle() : 0) , isOverStressed)
			.forGoggles(tooltip);

		return true;
	}
}
