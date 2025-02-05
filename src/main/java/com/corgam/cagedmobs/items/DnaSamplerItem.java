package com.corgam.cagedmobs.items;

import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.tileEntities.MobCageTE;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class DnaSamplerItem extends Item {
    public DnaSamplerItem(Properties properties) {
        super(properties);
    }
    // 右键提取
    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if(target.level.isClientSide()) return ActionResultType.FAIL;
        return recordDNA(stack, target, player, hand)? ActionResultType.SUCCESS : ActionResultType.FAIL;
    }

    // 右键末地石获得末影龙dna
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        ItemStack stack = context.getItemInHand();
        PlayerEntity player = context.getPlayer();

        if (canBeCached(EntityType.ENDER_DRAGON)
                && samplerTierSufficient(stack, EntityType.ENDER_DRAGON)
                && blockState.getBlock().getName().equals(Blocks.END_STONE.getName())){
            CompoundNBT nbt = new CompoundNBT();
            SerializationHelper.serializeEntityTypeNBT(nbt, EntityType.ENDER_DRAGON);
            stack.setTag(nbt);
            if (player != null){
                player.setItemInHand(context.getHand(), stack);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    // Called on left click on an entity to get it's sample
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(target.level.isClientSide() || !(attacker instanceof PlayerEntity)) return false;
        PlayerEntity player = (PlayerEntity) attacker;
        // Select the hand where the sampler is
        Hand hand;
        if(player.getMainHandItem().equals(stack)){
            hand = Hand.MAIN_HAND;
        }else if(player.getOffhandItem().equals(stack)){
            hand = Hand.OFF_HAND;
        }else{
            return false;
        }

        return recordDNA(stack, target, player, hand);
    }
    private boolean recordDNA(ItemStack stack, LivingEntity target, PlayerEntity player, Hand hand){
        // Try to sample the target
        if (canBeCached(target) && !RecipesHelper.isEntityTypeBlacklisted(target.getType())) {
            if(samplerTierSufficient(stack, target)) {
                CompoundNBT nbt = new CompoundNBT();
                SerializationHelper.serializeEntityTypeNBT(nbt, target.getType());
                // If sheep add it's color to nbt
                if(target instanceof SheepEntity){
                    SheepEntity sheep = (SheepEntity) target;
                    DyeColor color = sheep.getColor();
                    nbt.putInt("Color",color.getId());
                }
                stack.setTag(nbt);
                player.setItemInHand(hand, stack);
                return true;
            }else{
                player.displayClientMessage(new TranslationTextComponent("item.cagedmobs.dnasampler.not_sufficient").withStyle(TextFormatting.RED), true);
            }
        }else{
            player.displayClientMessage(new TranslationTextComponent("item.cagedmobs.dnasampler.not_cachable").withStyle(TextFormatting.RED), true);
        }
        return false;
    }


    // Checks if a sampler's tier is sufficient to sample given entity
    private static boolean samplerTierSufficient(ItemStack stack, Entity target) {
        return samplerTierSufficient(stack, target.getType());
    }
    private static boolean samplerTierSufficient(ItemStack stack, EntityType<?> type) {
        boolean sufficient = false;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                // Check for null exception
                if(mobData.getEntityType() == null){continue;}
                if(mobData.getEntityType().equals(type) && mobData.getSamplerTier() <= getSamplerTierInt(stack.getItem())) {
                    sufficient = true;
                    break;
                }
            }
        }
        return sufficient;
    }

    // Returns a tier number in a form of int from Item
    private static int getSamplerTierInt(Item item) {
        if(item instanceof DnaSamplerNetheriteItem){
            return 3;
        }else if(item instanceof DnaSamplerDiamondItem){
            return 2;
        }else{
            return 1;
        }
    }

    // Check if entity can be cached based on the list of cachable entities
    private static boolean canBeCached(Entity clickedEntity) {
        return canBeCached(clickedEntity.getType());
    }
    private static boolean canBeCached(EntityType<?> entityType) {
        boolean contains = false;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                // Check for null exception
                if(mobData.getEntityType() == null){continue;}
                if(mobData.getEntityType().equals(entityType)) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if(playerIn.isCrouching() && itemstack.hasTag()) {
            removeEntityType(itemstack);
            playerIn.swing(handIn);
            ActionResult.success(itemstack);
        }
        return ActionResult.fail(itemstack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(getTooltip(stack));
        tooltip.add(getInformationForTier().withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.cagedmobs.dnasampler.makeEmpty").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.cagedmobs.dnasampler.getBackEntity").withStyle(TextFormatting.GRAY));
    }

    private TranslationTextComponent getInformationForTier(){
        if(this instanceof DnaSamplerNetheriteItem){
            return new TranslationTextComponent("item.cagedmobs.dnasampler.tier3Info");
        }else if(this instanceof DnaSamplerDiamondItem){
            return new TranslationTextComponent("item.cagedmobs.dnasampler.tier2Info");
        }else{
            return new TranslationTextComponent("item.cagedmobs.dnasampler.tier1Info");
        }
    }

    private ITextComponent getTooltip(ItemStack stack) {
        if(!DnaSamplerItem.containsEntityType(stack)) {
            return new TranslationTextComponent("item.cagedmobs.dnasampler.empty").withStyle(TextFormatting.YELLOW);
        }else {
            EntityType<?> type = SerializationHelper.deserializeEntityTypeNBT(stack.getTag());
            // Add the text component
            if(type != null){
                return new TranslationTextComponent(type.getDescriptionId()).withStyle(TextFormatting.YELLOW);
            }else{
                // If not found say Unknown entity for crash prevention
                return new TranslationTextComponent("item.cagedmobs.dnasampler.unknown_entity").withStyle(TextFormatting.YELLOW);
            }
        }
    }

    public static boolean containsEntityType(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains("entity");
    }

    public void removeEntityType(ItemStack stack) {
        stack.removeTagKey("entity");
        stack.removeTagKey("Color");
    }

    public void setEntityTypeFromCage(MobCageTE cage, ItemStack stack, PlayerEntity player, Hand hand){
        EntityType<?> type = cage.getEntityType();
        CompoundNBT nbt = new CompoundNBT();
        SerializationHelper.serializeEntityTypeNBT(nbt, type);
        // If sheep add it's color to nbt
        if(type.toString().contains("sheep")){
            nbt.putInt("Color",cage.getColor());
        }
        stack.setTag(nbt);
        player.setItemInHand(hand, stack);
    }

    public EntityType<?> getEntityType(ItemStack stack) {
        if(stack.hasTag() && stack.getTag() != null) {
            return SerializationHelper.deserializeEntityTypeNBT(stack.getTag());
        }else {
            return null;
        }
    }
}
