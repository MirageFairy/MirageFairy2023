package miragefairy2023.mixins.api;

import net.minecraft.entity.attribute.EntityAttribute;

import java.util.ArrayList;
import java.util.List;

public final class PlayerAttributeRegistry {
    private static final List<EntityAttribute> registry = new ArrayList<>();

    private PlayerAttributeRegistry() {
    }

    public static void register(EntityAttribute entityAttribute) {
        registry.add(entityAttribute);
    }

    public static List<EntityAttribute> getPlayerAttributes() {
        return registry;
    }
}
