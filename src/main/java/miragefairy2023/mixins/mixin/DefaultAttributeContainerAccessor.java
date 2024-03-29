package miragefairy2023.mixins.mixin;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DefaultAttributeContainer.class)
public interface DefaultAttributeContainerAccessor {
    @Accessor
    public Map<EntityAttribute, EntityAttributeInstance> getInstances();
}
