package crazywoddman.warium_create.block;

import com.simibubi.create.foundation.utility.VoxelShaper;
import java.util.function.BiFunction;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WariumCreateShaper {
   public static WariumCreateShaper.Builder shape(VoxelShape shape) {
      return new WariumCreateShaper.Builder(shape);
   }

   public static WariumCreateShaper.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
      return shape(cuboid(x1, y1, z1, x2, y2, z2));
   }

   public static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
      return Block.box(x1, y1, z1, x2, y2, z2);
   }

   public static class Builder {
      VoxelShape shape;

      public Builder(VoxelShape shape) {
         this.shape = shape;
      }

      public WariumCreateShaper.Builder add(VoxelShape shape) {
         this.shape = Shapes.or(this.shape, shape);
         return this;
      }

      public WariumCreateShaper.Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
         return this.add(WariumCreateShaper.cuboid(x1, y1, z1, x2, y2, z2));
      }

      public VoxelShaper build(BiFunction<VoxelShape, Direction, VoxelShaper> factory, Direction direction) {
         return factory.apply(this.shape, direction);
      }

      public VoxelShaper forDirectional(Direction direction) {
         return this.build(VoxelShaper::forDirectional, direction);
      }

      public VoxelShaper forDirectional() {
         return this.forDirectional(Direction.NORTH);
      }
   }
}
