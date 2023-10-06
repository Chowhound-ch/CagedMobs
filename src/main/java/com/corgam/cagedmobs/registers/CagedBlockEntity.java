package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.blockEntities.MobCageBlockEntity;
import com.corgam.cagedmobs.blockEntities.TestEntity;
import com.corgam.cagedmobs.setup.Constants;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CagedBlockEntity {
    //Registry
    public static final DeferredRegister<BlockEntityType<?>> TE_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<BlockEntityType<MobCageBlockEntity>> MOB_CAGE = TE_REG.register("mobcage", () -> BlockEntityType.Builder.of(MobCageBlockEntity::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
    public final static RegistryObject<BlockEntityType<TestEntity>> TEST_ENTITY = TE_REG.register("testentity", () -> BlockEntityType.Builder.of(TestEntity::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));

}