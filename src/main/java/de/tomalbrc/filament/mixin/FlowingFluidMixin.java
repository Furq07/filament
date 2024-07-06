package de.tomalbrc.filament.mixin;

import de.tomalbrc.filament.decoration.block.entity.DecorationBlockEntity;
import de.tomalbrc.filament.decoration.block.DecorationBlock;
import de.tomalbrc.filament.registry.filament.DecorationRegistry;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {

    @Inject(method = "canSpreadTo", at = @At("TAIL"), cancellable = true)
    private void filament$canSpreadTo(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Direction direction, BlockPos blockPos2, BlockState blockState2, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (DecorationRegistry.isDecoration(blockState2)) {
            boolean isWaterloggable = this.isWaterloggable((DecorationBlock) blockState2.getBlock()) && direction != Direction.DOWN;
            boolean isSolid = this.isSolid((DecorationBlock) blockState2.getBlock()) && direction != Direction.DOWN;
            cir.setReturnValue(isWaterloggable || !isSolid);
        }
    }

    @Inject(method = "spreadTo", at = @At("TAIL"))
    protected void filament$spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState, CallbackInfo ci) {
        if (DecorationRegistry.isDecoration(blockState) && !isWaterloggable((DecorationBlock) blockState.getBlock()) && !isSolid((DecorationBlock) blockState.getBlock())) {
            if (levelAccessor.getBlockEntity(blockPos) instanceof DecorationBlockEntity decorationBlockEntity) {
                decorationBlockEntity.destroyStructure(true);
            }

            // let it overflow with liquid
            levelAccessor.setBlock(blockPos, fluidState.createLegacyBlock(), 3);
        }
    }

    @Inject(method = "canPassThrough", at = @At("TAIL"), cancellable = true)
    private void filament$canPassThrough(BlockGetter blockGetter, Fluid fluid, BlockPos blockPos, BlockState blockState, Direction direction, BlockPos blockPos2, BlockState blockState2, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        // pass-thu but only non-waterloggable blocks and non-solid
        if (DecorationRegistry.isDecoration(blockState2) && !isWaterloggable((DecorationBlock) blockState2.getBlock()) && !isSolid((DecorationBlock) blockState2.getBlock()))
            cir.setReturnValue(this.canFlowThrough(blockState2));
    }
    @Inject(method = "canPassThroughWall", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("TAIL"), cancellable = true)
    private void filament$canPassThroughWall(Direction direction, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2, CallbackInfoReturnable<Boolean> cir, Object2ByteLinkedOpenHashMap object2ByteLinkedOpenHashMap, Block.BlockStatePairKey blockStatePairKey, VoxelShape voxelShape, VoxelShape voxelShape2, boolean bl) {
        if (DecorationRegistry.isDecoration(blockState) || DecorationRegistry.isDecoration(blockState2))
            cir.setReturnValue(true);
    }

    @Unique
    private boolean canFlowThrough(BlockState blockState) {
        if (DecorationRegistry.isDecoration(blockState)) {
            DecorationBlock decorationBlock = (DecorationBlock) blockState.getBlock();

            if (decorationBlock.getDecorationData() != null && decorationBlock.getDecorationData().properties() != null) {
                return !decorationBlock.getDecorationData().hasBlocks() && !decorationBlock.getDecorationData().properties().waterloggable && !decorationBlock.getDecorationData().properties().solid;
            }
        }

        return false;
    }

    @Unique
    private boolean isWaterloggable(DecorationBlock decorationBlock) {
        if (decorationBlock.getDecorationData() != null && decorationBlock.getDecorationData().properties() != null) {
            return decorationBlock.getDecorationData().properties().waterloggable;
        }

        return false;
    }

    @Unique
    private boolean isSolid(DecorationBlock decorationBlock) {
        if (decorationBlock.getDecorationData() != null && decorationBlock.getDecorationData().properties() != null) {
            return decorationBlock.getDecorationData().properties().solid;
        }

        return false;
    }
}
