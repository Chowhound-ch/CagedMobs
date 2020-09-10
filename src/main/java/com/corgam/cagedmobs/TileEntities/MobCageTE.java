package com.corgam.cagedmobs.TileEntities;

import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedTE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

import java.util.List;

public class MobCageTE extends TileEntity implements ITickableTileEntity {
    // Hopping
    boolean hopping = false;
    // Env
    private EnvironmentData environment = null;
    private ItemStack envItem = ItemStack.EMPTY;
    // Entity
    private MobData entity = null;
    private EntityType<?> entityType = null;
    // Ticks
    private int currentGrowTicks = 0;
    private int totalGrowTicks = 0;
    boolean waitingForHarvest = false;



    public MobCageTE(boolean hopping) {
        super(CagedTE.MOB_CAGE.get());
        this.hopping = hopping;
    }

    public MobCageTE() {
        super(CagedTE.MOB_CAGE.get());
    }

    @Override
    public void tick() {
        //Tick only when env and mob is inside
        if(this.hasEnvAndEntity() && !waitingForHarvest) {
            // Check if ready to harvest
            if(this.currentGrowTicks >= this.getTotalGrowTicks()) {
                this.attemptHarvest();
            }else {
                // Add one tick
                this.currentGrowTicks++;
            }
        }
    }

    private boolean hasEnvAndEntity() {
        return this.hasEntity() && this.hasEnvironment();
    }


    public int getTotalGrowTicks() {
        return this.totalGrowTicks;
    }

    public int getCurrentGrowTicks() {
        return currentGrowTicks;
    }

    public boolean isHopping() {
        return hopping;
    }


    public EntityType<?> getEntityType() {
        return entityType;
    }

    public ItemStack getEnvItem() {
        return envItem;
    }

///// ENVIRONMENT /////

    public void setEnvironment(ItemStack stack) {
        EnvironmentData env = getEnvironmentFromItemStack(stack);
        this.environment = env;
        // Set the env item
        ItemStack itemstack = stack.copy();
        itemstack.setCount(1);
        this.envItem = itemstack;
    }
    public EnvironmentData getEnvironment() {
        return environment;
    }

    public boolean hasEnvironment() {
        return environment != null;
    }

    private static EnvironmentData getEnvironmentFromItemStack(ItemStack stack){
        EnvironmentData finalEnvData = null;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.ENV_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof EnvironmentData) {
                final EnvironmentData envData = (EnvironmentData) recipe;
                if(envData.getInputItem().test(stack)) {
                    finalEnvData = envData;
                    break;
                }
            }
        }
        return finalEnvData;
    }

    public static boolean existsEnvironmentFromItemStack(ItemStack stack){
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.ENV_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof EnvironmentData) {
                final EnvironmentData envData = (EnvironmentData) recipe;
                if(envData.getInputItem().test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onEnvironmentRemoval() {
        this.totalGrowTicks = 0;
        this.currentGrowTicks = 0;
        this.waitingForHarvest = false;
        this.environment = null;
        this.envItem = ItemStack.EMPTY;
        this.entity = null;
        this.entityType = null;
    }


/// ENTITY /////

    public MobData getEntity() {
        return entity;
    }

    public boolean hasEntity() {
        return entity != null;
    }

    public void setEntityFromType(EntityType<?> type) {
        MobData mobData = getMobDataFromType(type);
        this.entity = mobData;
        this.entityType = type;
        this.totalGrowTicks = mobData.getTotalGrowTicks();
    }

    public static boolean existsEntityFromType(EntityType<?> entityType) {
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                if(mobData.getEntityType().equals(entityType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private MobData getMobDataFromType(EntityType<?> type){
        MobData finalMobData = null;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                if(mobData.getEntityType().equals(type)) {
                    finalMobData = mobData;
                    break;
                }
            }
        }
        return finalMobData;
    }

    public void onEntityRemoval() {
        this.entity = null;
        this.entityType = null;

        this.currentGrowTicks = 0;
        this.totalGrowTicks = 0;
        this.waitingForHarvest = false;
    }


////// HARVEST AND LOOT /////

    // Attemp harvest (when hopping cage, then harvest, when not, lock for players interaction)
    private void attemptHarvest() {
        if(this.hopping) {
            this.currentGrowTicks = 0;
            this.autoHarvest();
        }else {
            waitingForHarvest = true;
            this.currentGrowTicks = this.getTotalGrowTicks();
        }
    }

    // Auto-harvests the cage
    private void autoHarvest() {

    }

    // Check if locked and waiting for player interaction
    public boolean isWaitingForHarvest() {
        return this.waitingForHarvest;
    }

    // Is run when player right clicks to harvest
    public void onPlayerHarvest() {
        if(!this.hopping && canPlayerHarvest()){
            this.currentGrowTicks = 0;
            this.waitingForHarvest = false;
            List<ItemStack> drops = createDropsList();
            for( ItemStack item : drops) {
                dropItem(item.copy());
            }
        }
    }

    // Check if a player can harvest the cage
    private boolean canPlayerHarvest(){
        return this.hasEnvAndEntity() && this.getTotalGrowTicks() > 0 && this.getCurrentGrowTicks() >= this.getTotalGrowTicks();
    }

    // Gives back a list of items that harvest will yeld
    private NonNullList<ItemStack> createDropsList(){
        NonNullList<ItemStack> drops = NonNullList.create();
        for(LootData loot : this.entity.getResults()) {
            if(this.world.rand.nextFloat() <= loot.getChance()) {
                // Roll the amount of items
                int amount = 2;
                if(amount > 0) {
                    for(int i=0; i < amount ; i++) {
                        // Add copied itemstack to the drop list
                        drops.add(loot.getItem().copy());
                    }
                }
            }
        }
        return drops;
    }

    // Creates an item entity
    public void dropItem(ItemStack item) {
        if(!this.world.isRemote) {
            final double offsetX = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.15F;
            final double offsetY = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
            final double offsetZ = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.15F;
            final ItemEntity itemEntity = new ItemEntity(this.world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, item);
            itemEntity.setDefaultPickupDelay();
            this.world.addEntity(itemEntity);
        }
    }

    /// SERIALIZATION ///

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT nbt) {
        super.func_230337_a_(state, nbt);
        this.hopping = nbt.getBoolean("hopping");
        this.waitingForHarvest = nbt.getBoolean("waitingForHarvest");
        this.currentGrowTicks = nbt.getInt("currentGrowTicks");

        CompoundNBT entityNBT = nbt.getCompound("mobData");
        CompoundNBT envNBT = nbt.getCompound("env");

        this.entity = MobData.deserializeNBT(entityNBT);
        this.environment = EnvironmentData.deserializeNBT(envNBT);
    }

    // Serialize the block to save it on drive
    @Override
    public CompoundNBT write(CompoundNBT dataTag) {
        dataTag.putBoolean("hopping", this.hopping);
        if(this.hasEnvironment()) {
            //dataTag.put("environment", this.environment.serializeNBT());
            if(this.hasEntity()){
                dataTag.put("mobData", this.entity.serializeNBT());
                dataTag.putInt("currentGrowTicks", this.currentGrowTicks);
                dataTag.putBoolean("waitingForHarvest", this.waitingForHarvest);
            }
        }
        return super.write(dataTag);
    }
}