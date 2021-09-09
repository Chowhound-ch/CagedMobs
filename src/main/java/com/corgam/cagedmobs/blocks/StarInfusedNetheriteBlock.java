package com.corgam.cagedmobs.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class StarInfusedNetheriteBlock extends Block {
    public StarInfusedNetheriteBlock(Properties props) {
        super(props);
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("block.cagedmobs.starinfusednetheriteblock.beacon").withStyle(TextFormatting.GRAY));
    }
}
