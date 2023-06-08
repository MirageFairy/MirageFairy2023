package miragefairy2023.mixins.mixin;

import miragefairy2023.mixins.api.PlayerAttributeRegistry;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        DefaultAttributeContainer.Builder builder = info.getReturnValue();
        for (EntityAttribute playerAttribute : PlayerAttributeRegistry.getPlayerAttributes()) {
            builder = builder.add(playerAttribute);
        }
        info.setReturnValue(builder);
    }
}
