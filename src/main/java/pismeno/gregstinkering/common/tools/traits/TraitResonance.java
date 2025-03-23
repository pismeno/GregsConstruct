package pismeno.gregstinkering.common.tools.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import pismeno.gregstinkering.GregsTinkering;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.potion.TinkerPotion;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitResonance extends AbstractTrait {
    public static final TinkerPotion Resonance = new TinkerPotion(Util.getResource("resonance"), false, true);
    public static final TinkerPotion ResonanceTime = new TinkerPotion(Util.getResource("resonance_time"), false, true);

    public TraitResonance() {
        super("resonance", TextFormatting.YELLOW);
    }

    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        float boost = Resonance.getLevel(event.getEntityPlayer());
        boost /= 37.5F;
        event.setNewSpeed(event.getNewSpeed() + event.getNewSpeed() * boost);
        GregsTinkering.LOGGER.info("Speed: " + event.getNewSpeed());
    }

    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        if (Resonance.getLevel(player) == 15) {
            newDamage = damage * 1.4F;
            GregsTinkering.LOGGER.info("Resonance before stacks: " + Resonance.getLevel(player));
            Resonance.apply(player, 1, 1);
            GregsTinkering.LOGGER.info("Resonance after stacks: " + Resonance.getLevel(player));
        }

        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
        int level = 5;
        level += Resonance.getLevel(player);
        level = Math.min(15, level);
        applyStacks(player, level, 80, 40);
    }

    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        int level = 5;
        if (wasCritical) level = 10;
        level += Resonance.getLevel(player);
        level = Math.min(15, level);
        applyStacks(player, level, 160, 30);
        GregsTinkering.LOGGER.info("Resonance: " + Resonance.getLevel(player));
    }

    private void applyStacks(EntityLivingBase player, int level, int duration, int cooldown) {
        if (player.getActivePotionEffect(ResonanceTime) != null){
            if (player.getActivePotionEffect(ResonanceTime).getDuration() <= cooldown) {
                Resonance.apply(player, duration, level);
                ResonanceTime.apply(player, duration, 1);
            } else {
                Resonance.apply(player, duration, Resonance.getLevel(player));
            }
        } else {
            Resonance.apply(player, duration, 5);
            ResonanceTime.apply(player, duration, 1);
        }
    }
}
