package crazywoddman.warium_create.util;

import net.minecraft.world.level.material.Fluid;

public class FluidTransferContext {
    private static final ThreadLocal<Fluid> currentFluid = new ThreadLocal<>();
    
    public static void setFluid(Fluid fluid) {
        currentFluid.set(fluid);
    }
    
    public static Fluid getFluid() {
        return currentFluid.get();
    }
    
    public static void clearFluid() {
        currentFluid.remove();
    }
}