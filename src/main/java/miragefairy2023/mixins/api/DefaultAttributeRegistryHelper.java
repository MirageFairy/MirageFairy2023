package miragefairy2023.mixins.api;

import miragefairy2023.mixins.mixin.DefaultAttributeContainerAccessor;
import miragefairy2023.mixins.mixin.DefaultAttributeRegistryAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;

import java.util.HashMap;
import java.util.Map;

public class DefaultAttributeRegistryHelper {
    public static void addDefaultAttribute(EntityType<? extends LivingEntity> type, EntityAttribute attribute) {
        addDefaultAttribute(type, new DefaultAttributeContainer.Builder().add(attribute).build());
    }

    public static void addDefaultAttribute(EntityType<? extends LivingEntity> type, EntityAttribute attribute, double baseValue) {
        addDefaultAttribute(type, new DefaultAttributeContainer.Builder().add(attribute, baseValue).build());

    }

    private static void addDefaultAttribute(EntityType<? extends LivingEntity> type, DefaultAttributeContainer additionalContainer) {
        DefaultAttributeContainer oldContainer = DefaultAttributeRegistryAccessor.getRegistry().computeIfAbsent(type, type2 -> new DefaultAttributeContainer(new HashMap<>()));

        Map<EntityAttribute, EntityAttributeInstance> oldInstances = ((DefaultAttributeContainerAccessor) oldContainer).getInstances();
        Map<EntityAttribute, EntityAttributeInstance> additionalInstances = ((DefaultAttributeContainerAccessor) additionalContainer).getInstances();

        Map<EntityAttribute, EntityAttributeInstance> newInstances = new HashMap<>();
        newInstances.putAll(oldInstances);
        newInstances.putAll(additionalInstances);

        DefaultAttributeRegistryAccessor.getRegistry().put(type, new DefaultAttributeContainer(newInstances));
    }
}
