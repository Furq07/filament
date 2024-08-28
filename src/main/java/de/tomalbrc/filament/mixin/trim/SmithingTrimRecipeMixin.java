package de.tomalbrc.filament.mixin.trim;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTrimRecipe.class)
public class SmithingTrimRecipeMixin {
    @Inject(method = "isBaseIngredient", at = @At("HEAD"), cancellable = true)
    public void filament$isBaseIngredient(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial() == ArmorMaterials.CHAIN && !(itemStack.getItem() instanceof PolymerItem)) {
            cir.setReturnValue(false);
        }
    }
}
