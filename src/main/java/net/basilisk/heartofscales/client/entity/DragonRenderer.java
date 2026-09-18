package net.basilisk.heartofscales.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.basilisk.heartofscales.entity.DragonEntity;
import net.basilisk.heartofscales.species.ModRegistries;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {
    // Matches the halved hitbox vanilla gives baby mobs
    private static final float BABY_SCALE = 0.5f;

    public DragonRenderer(EntityRendererProvider.Context context) {
        super(context, new DragonModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, DragonEntity dragon,
                                    BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = dragon.isBaby() ? BABY_SCALE : 1.0f;
        super.scaleModelForRender(widthScale * scale, heightScale * scale, poseStack, dragon, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    // Placeholder: reuses the subspecies egg tint until the genome carries a base colour
    @Override
    public Color getRenderColor(DragonEntity dragon, float partialTick, int packedLight) {
        return Color.ofOpaque(ModRegistries.tint(dragon.level().registryAccess(), dragon.getSubspecies()));
    }
}
