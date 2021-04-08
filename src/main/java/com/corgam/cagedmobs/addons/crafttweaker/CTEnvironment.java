//package com.corgam.cagedmobs.addons.crafttweaker;
//
//import com.blamejared.crafttweaker.api.annotations.ZenRegister;
//import com.blamejared.crafttweaker.api.item.IIngredient;
//import com.blamejared.crafttweaker.impl_native.blocks.MCBlockState;
//import com.corgam.cagedmobs.serializers.env.EnvironmentData;
//import net.minecraft.util.ResourceLocation;
//import org.openzen.zencode.java.ZenCodeType;
//
//import java.util.Arrays;
//import java.util.HashSet;
//
//@ZenRegister
//@ZenCodeType.Name("mods.cagedmobs.Environment")
//public class CTEnvironment {
//
//    private final EnvironmentData data;
//
//    public CTEnvironment(String id, IIngredient item, MCBlockState renderState, float growModifier, String[] categories) {
//        this(new EnvironmentData(ResourceLocation.tryCreate(id),item.asVanillaIngredient(),renderState.getInternal(),growModifier,new HashSet<>(Arrays.asList(categories))));
//    }
//
//    public CTEnvironment(EnvironmentData envData){
//        this.data = envData;
//    }
//
//    @ZenCodeType.Method
//    public CTEnvironment addCategory (String category) {
//
//        this.data.getEnvironments().add(category);
//        return this;
//    }
//
//    @ZenCodeType.Method
//    public CTEnvironment removeCategory (String category) {
//
//        this.data.getEnvironments().remove(category);
//        return this;
//    }
//
//    @ZenCodeType.Method
//    public CTEnvironment clearCategories () {
//
//        this.data.getEnvironments().clear();
//        return this;
//    }
//
//    @ZenCodeType.Method
//    public CTEnvironment setInputItem (IIngredient ingredient) {
//
//        this.data.setInputItem(ingredient.asVanillaIngredient());
//        return this;
//    }
//
//    @ZenCodeType.Method
//    public CTEnvironment setDisplay (MCBlockState state) {
//
//        this.data.setRenderState(state.getInternal());
//        return this;
//    }
//
//    @ZenCodeType.Method
//    public CTEnvironment setGrowthModifier (float modifier) {
//        this.data.setGrowthModifier(modifier);
//        return this;
//    }
//
//    public EnvironmentData getEnvironmentData(){
//        return this.data;
//    }
//}
