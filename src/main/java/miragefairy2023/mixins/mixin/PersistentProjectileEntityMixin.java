package miragefairy2023.mixins.mixin;

import miragefairy2023.modules.DemonPlayerAttributeCard;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    @Shadow
    private double damage;

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    protected void init(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, CallbackInfo info) {
        if (owner instanceof PlayerEntity) {
            damage += owner.getAttributeValue(DemonPlayerAttributeCard.SHOOTING_DAMAGE.getEntityAttribute());
        }
    }
}
