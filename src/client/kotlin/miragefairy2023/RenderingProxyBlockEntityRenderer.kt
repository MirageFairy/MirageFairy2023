package miragefairy2023

import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

class RenderingProxyBlockEntityRenderer<T>(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<T> where T : BlockEntity, T : RenderingProxyBlockEntity {
    override fun render(blockEntity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        val renderingProxy = object : RenderingProxy {
            override fun stack(block: () -> Unit) {
                matrices.push()
                try {
                    block()
                } finally {
                    matrices.pop()
                }
            }

            override fun translate(x: Double, y: Double, z: Double) = matrices.translate(x, y, z)
            override fun scale(x: Float, y: Float, z: Float) = matrices.scale(x, y, z)
            override fun rotateX(degrees: Float) = matrices.multiply(Quaternion(Vec3f.POSITIVE_X, degrees, true))
            override fun rotateY(degrees: Float) = matrices.multiply(Quaternion(Vec3f.POSITIVE_Y, degrees, true))
            override fun rotateZ(degrees: Float) = matrices.multiply(Quaternion(Vec3f.POSITIVE_Z, degrees, true))

            override fun renderItem(itemStack: ItemStack) = MinecraftClient.getInstance().itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0)
        }
        blockEntity.render(renderingProxy, tickDelta, light, overlay)
    }
}

