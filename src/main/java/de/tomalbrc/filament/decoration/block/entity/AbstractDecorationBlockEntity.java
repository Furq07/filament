package de.tomalbrc.filament.decoration.block.entity;

import de.tomalbrc.filament.Filament;
import de.tomalbrc.filament.decoration.block.DecorationBlock;
import de.tomalbrc.filament.registry.DecorationRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractDecorationBlockEntity extends BlockEntity {
    public static final String MAIN = "Main";
    public static final String VERSION = "V";
    public static final String ITEM = "Item";
    public static final String PASSTHROUGH = "Passthrough";
    public static final String ROTATION = "Rotation";
    public static final String DIRECTION = "Direction";

    protected BlockPos main;
    protected int version;

    protected int rotation;

    protected Direction direction = Direction.UP;

    protected ItemStack itemStack;

    private boolean passthrough = false;

    public AbstractDecorationBlockEntity(BlockPos pos, BlockState state) {
        super(DecorationRegistry.getBlockEntityType(state), pos, state);
    }

    public boolean isMain() {
        return this.main != null && this.main.equals(BlockPos.ZERO);
    }

    public void setMain(BlockPos main) {
        this.main = main;
    }

    public DecorationBlockEntity getMainBlockEntity() {
        assert this.level != null;
        return (DecorationBlockEntity)this.level.getBlockEntity(new BlockPos(this.worldPosition).subtract(this.main));
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        if (!compoundTag.contains(VERSION)) {
            this.version = 1; // upgrade old format
            if (compoundTag.contains(MAIN)) this.main = NbtUtils.readBlockPos(compoundTag, MAIN).get().subtract(this.worldPosition);
        }
        else {
            this.version = compoundTag.getInt(VERSION);
            if (compoundTag.contains(MAIN)) this.main = NbtUtils.readBlockPos(compoundTag, MAIN).get();
        }

        if (compoundTag.contains(ITEM)) {
            this.itemStack = ItemStack.parseOptional(provider, compoundTag.getCompound(ITEM));
        }

        if (this.itemStack == null || this.itemStack.isEmpty()) {
            this.itemStack = BuiltInRegistries.ITEM.get(((DecorationBlock)this.getBlockState().getBlock()).getDecorationData().id()).getDefaultInstance();
        }

        if (compoundTag.contains(PASSTHROUGH)) {
            this.passthrough = compoundTag.getBoolean(PASSTHROUGH);
            this.setCollision(!this.passthrough);
        }

        if (!this.isMain())
            return;

        if (compoundTag.contains(ROTATION))
            this.rotation = compoundTag.getInt(ROTATION);
        if (compoundTag.contains(DIRECTION))
            this.direction = Direction.from3DDataValue(compoundTag.getInt(DIRECTION));
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);

        if (this.itemStack == null) this.itemStack = BuiltInRegistries.ITEM.get(this.getBlockState().getBlockHolder().unwrapKey().get().location()).getDefaultInstance();

        if (this.itemStack == null) {
            Filament.LOGGER.error("No item for decoration! Removing decoration block entity at " + this.getBlockPos().toShortString());
            this.level.destroyBlock(this.getBlockPos(), false);
            this.setRemoved();
            return;
        }

        if (this.itemStack != null)
            compoundTag.put(ITEM, this.itemStack.save(provider));

        if (this.main == null) this.main = BlockPos.ZERO;

        compoundTag.put(MAIN, NbtUtils.writeBlockPos(this.main));
        compoundTag.putInt(VERSION, this.version);
        compoundTag.putBoolean(PASSTHROUGH, this.passthrough);

        if (this.isMain()) {
            compoundTag.putInt(ROTATION, this.rotation);
            compoundTag.putInt(DIRECTION, this.direction.get3DDataValue());
        }
    }

    abstract protected void destroyBlocks();
    abstract protected void destroyStructure(boolean dropItems);
    abstract protected void setCollisionStructure(boolean collisionStructure);

    protected void setCollision(boolean collision) {
        this.getBlockState().setValue(DecorationBlock.PASSTHROUGH, !collision);
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }

    public void setItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public float getVisualRotationYInDegrees() {
        Direction direction = this.getDirection();
        int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
        return (float) Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + rotation * 45 + i);
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return this.rotation;
    }
}