package binnie.genetics.item;

import binnie.Binnie;
import binnie.core.genetics.Gene;
import binnie.core.item.ItemCore;
import binnie.genetics.CreativeTabGenetics;
import binnie.genetics.Genetics;
import binnie.genetics.api.IGene;
import binnie.genetics.api.IItemAnalysable;
import binnie.genetics.api.IItemChargeable;
import binnie.genetics.genetics.GeneItem;
import binnie.genetics.genetics.SequencerItem;
import com.google.common.base.Preconditions;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemSequence extends ItemCore implements IItemAnalysable, IItemChargeable {
	public ItemSequence() {
		super("sequence");
		this.setMaxStackSize(1);
		this.setMaxDamage(5);
		this.setUnlocalizedName("sequence");
		this.setCreativeTab(CreativeTabGenetics.instance);
	}

	@Override
	public String getItemStackDisplayName(final ItemStack itemstack) {
		final GeneItem gene = GeneItem.create(itemstack);
		if (gene == null) {
			return "Corrupted Sequence";
		} else {
			return gene.getBreedingSystem().getDescriptor() + " DNA Sequence";
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack itemstack, final EntityPlayer entityPlayer, final List<String> list, final boolean par4) {
		super.addInformation(itemstack, entityPlayer, list, par4);
		list.add(Genetics.proxy.localise("item.sequence." + (5 - itemstack.getItemDamage() % 6)));
		final SequencerItem gene = SequencerItem.create(itemstack);
		if (gene != null) {
			if (gene.analysed) {
				gene.getInfo(list);
			} else {
				list.add("<Unknown>");
			}
			final int seq = gene.sequenced;
			if (seq == 0) {
				list.add("Unsequenced");
			} else if (seq < 100) {
				list.add("Partially Sequenced (" + seq + "%)");
			} else {
				list.add("Fully Sequenced");
			}
		}
	}

//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerIcons(final IIconRegister register) {
//		this.itemIcon = Genetics.proxy.getIcon(register, "sequencer");
//	}

	@Override
	public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final NonNullList<ItemStack> itemList) {
		final IAlleleBeeSpecies species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele("forestry.speciesMeadows");
		Preconditions.checkNotNull(species);
		itemList.add(create(new Gene(species, EnumBeeChromosome.SPECIES, Binnie.GENETICS.getBeeRoot()), false));
	}

	@Override
	public boolean isAnalysed(final ItemStack stack) {
		final SequencerItem seq = SequencerItem.create(stack);
		return seq != null && seq.analysed;
	}

	@Override
	public ItemStack analyse(final ItemStack stack) {
		final SequencerItem seq = SequencerItem.create(stack);
		Preconditions.checkNotNull(seq, "Cannot analyze itemstack that is not a valid item sequence");
		seq.analysed = true;
		seq.writeToItem(stack);
		return stack;
	}

	@Override
	public float getAnalyseTimeMult(final ItemStack stack) {
		return 1.0f;
	}

	public static ItemStack create(final IGene gene) {
		return create(gene, false);
	}

	public static ItemStack create(final IGene gene, final boolean sequenced) {
		final ItemStack item = new ItemStack(Genetics.itemSequencer);
		item.setItemDamage(sequenced ? 0 : item.getMaxDamage());
		final SequencerItem seq = new SequencerItem(gene);
		seq.writeToItem(item);
		return item;
	}

	@Override
	public int getCharges(final ItemStack itemStack) {
		return itemStack.getMaxDamage() - itemStack.getItemDamage();
	}

	@Override
	public int getMaxCharges(ItemStack itemStack) {
		return itemStack.getMaxDamage();
	}

	@Override
	public ISpeciesRoot getSpeciesRoot(ItemStack itemStack) {
		final SequencerItem seq = SequencerItem.create(itemStack);
		Preconditions.checkNotNull(seq, "Cannot getSpeciesRoot from itemstack that is not a valid item sequence");
		return seq.getSpeciesRoot();
	}
}
