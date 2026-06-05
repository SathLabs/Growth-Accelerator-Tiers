package dev.satherov.growthacceleratortiers.datagen.providers.models;

import dev.satherov.growthacceleratortiers.api.GAT;
import dev.satherov.growthacceleratortiers.block.GATGrowthAcceleratorBlock;
import dev.satherov.growthacceleratortiers.core.definitions.GATBlocks;
import dev.satherov.growthacceleratortiers.core.definitions.GATItems;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GATModelProvider extends ModelProvider {
    
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider itemInfoPathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    private Consumer<BlockModelDefinitionGenerator> blockStateOutput;
    private ItemModelOutput itemModelOutput;
    private BiConsumer<Identifier, ModelInstance> modelOutput;
    
    public GATModelProvider(PackOutput output) {
        super(output, GAT.MOD_ID);
        this.blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemInfoPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }
    
    private static Material texture(String path) {
        return new Material(GAT.rl(path));
    }
    
    private static MultiVariant plainVariant(Identifier model) {
        return new MultiVariant(WeightedList.of(new Variant(model)));
    }
    
    private static PropertyDispatch<VariantMutator> createFacingDispatch() {
        return PropertyDispatch.modify(BlockStateProperties.FACING)
                .select(Direction.DOWN, BlockModelGenerators.X_ROT_180)
                .select(Direction.UP, BlockModelGenerators.NOP)
                .select(Direction.NORTH, BlockModelGenerators.X_ROT_90)
                .select(Direction.SOUTH, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_180))
                .select(Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
                .select(Direction.EAST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90));
    }
    
    private static PropertyDispatch<VariantMutator> createPoweredDispatch(Identifier unpoweredModel, Identifier poweredModel) {
        return PropertyDispatch.modify(GATGrowthAcceleratorBlock.POWERED)
                .select(false, VariantMutator.MODEL.withValue(unpoweredModel))
                .select(true, VariantMutator.MODEL.withValue(poweredModel));
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        var blockStateCollector = new BlockStateGeneratorCollector(this::getKnownBlocks);
        var itemInfoCollector = new ItemInfoCollector(this::getKnownItems);
        var modelCollector = new SimpleModelCollector();
        
        this.blockStateOutput = blockStateCollector;
        this.itemModelOutput = itemInfoCollector;
        this.modelOutput = modelCollector;
        try {
            this.registerModels(new BlockModelGenerators(blockStateCollector, itemInfoCollector, modelCollector),
                    new ItemModelGenerators(itemInfoCollector, modelCollector));
        } finally {
            this.blockStateOutput = null;
            this.itemModelOutput = null;
            this.modelOutput = null;
        }
        
        blockStateCollector.validate();
        itemInfoCollector.finalizeAndValidate();
        
        return CompletableFuture.allOf(
                blockStateCollector.save(cache, this.blockStatePathProvider),
                modelCollector.save(cache, this.modelPathProvider),
                itemInfoCollector.save(cache, this.itemInfoPathProvider)
        );
    }
    
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        this.boostedGrowthAccelerator();
        this.crankedGrowthAccelerator();
        this.monoGrowthAccelerator();
        this.flatSingleLayer(GATItems.DIRECTIONAL_MODIFIER);
    }
    
    private void boostedGrowthAccelerator() {
        this.growthAccelerator(GATBlocks.BOOSTED_GROWTH_ACCELERATOR, "boosted");
    }
    
    private void crankedGrowthAccelerator() {
        this.growthAccelerator(GATBlocks.CRANKED_GROWTH_ACCELERATOR, "cranked");
    }
    
    private void monoGrowthAccelerator() {
        this.growthAccelerator(GATBlocks.DIRECTIONAL_GROWTH_ACCELERATOR, "mono");
    }
    
    private void growthAccelerator(BlockDefinition<?> definition, String textureFolder) {
        Block block = definition.block();
        Identifier unpoweredModel = ModelTemplates.CUBE_BOTTOM_TOP.create(
                ModelLocationUtils.getModelLocation(block),
                this.growthAcceleratorTextures(textureFolder, false),
                this.modelOutput);
        Identifier poweredModel = ModelTemplates.CUBE_BOTTOM_TOP.create(
                ModelLocationUtils.getModelLocation(block, "_on"),
                this.growthAcceleratorTextures(textureFolder, true),
                this.modelOutput);
        
        this.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block, GATModelProvider.plainVariant(unpoweredModel))
                        .with(GATModelProvider.createFacingDispatch())
                        .with(GATModelProvider.createPoweredDispatch(unpoweredModel, poweredModel)));
    }
    
    private TextureMapping growthAcceleratorTextures(String textureFolder, boolean powered) {
        String poweredSuffix = powered ? "_on" : "";
        return new TextureMapping()
                .put(TextureSlot.SIDE, GATModelProvider.texture("block/" + textureFolder + "/growth_accelerator_side" + poweredSuffix))
                .put(TextureSlot.BOTTOM, GATModelProvider.texture("block/" + textureFolder + "/growth_accelerator_bottom"))
                .put(TextureSlot.TOP, GATModelProvider.texture("block/" + textureFolder + "/growth_accelerator_top" + poweredSuffix));
    }
    
    private void flatSingleLayer(ItemDefinition<?> item) {
        Item resolvedItem = item.asItem();
        Identifier model = ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(resolvedItem),
                TextureMapping.layer0(resolvedItem),
                this.modelOutput);
        this.itemModelOutput.accept(resolvedItem, ItemModelUtils.plainModel(model));
    }
    
    private static class BlockStateGeneratorCollector implements Consumer<BlockModelDefinitionGenerator> {
        private final Map<Block, BlockModelDefinitionGenerator> generators = new HashMap<>();
        private final Supplier<java.util.stream.Stream<? extends Holder<Block>>> knownBlocks;
        
        private BlockStateGeneratorCollector(Supplier<java.util.stream.Stream<? extends Holder<Block>>> knownBlocks) {
            this.knownBlocks = knownBlocks;
        }
        
        @Override
        public void accept(BlockModelDefinitionGenerator generator) {
            Block block = generator.block();
            BlockModelDefinitionGenerator previous = this.generators.put(block, generator);
            if (previous != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        }
        
        private void validate() {
            List<Identifier> missingDefinitions = this.knownBlocks.get()
                    .filter(holder -> !this.generators.containsKey(holder.value()))
                    .map(holder -> holder.unwrapKey().orElseThrow().identifier())
                    .toList();
            if (!missingDefinitions.isEmpty()) {
                throw new IllegalStateException("Missing blockstate definitions for: " + missingDefinitions);
            }
        }
        
        private CompletableFuture<?> save(CachedOutput cache, PackOutput.PathProvider pathProvider) {
            Map<Block, BlockStateModelDispatcher> definitions = this.generators.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().create()));
            Function<Block, Path> pathGetter = block -> pathProvider.json(block.builtInRegistryHolder().key().identifier());
            return DataProvider.saveAll(cache, BlockStateModelDispatcher.CODEC, pathGetter, definitions);
        }
    }
    
    private static class ItemInfoCollector implements ItemModelOutput {
        private final Map<Item, ClientItem> itemInfos = new HashMap<>();
        private final Map<Item, Item> copies = new HashMap<>();
        private final Supplier<java.util.stream.Stream<? extends Holder<Item>>> knownItems;
        private final Map<Identifier, ClientItem> idItemInfos = new HashMap<>();
        
        private ItemInfoCollector(Supplier<java.util.stream.Stream<? extends Holder<Item>>> knownItems) {
            this.knownItems = knownItems;
        }
        
        @Override
        public void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties) {
            this.register(item, new ClientItem(model, properties));
        }
        
        @Override
        public void register(Item item, ClientItem clientItem) {
            ClientItem previous = this.itemInfos.put(item, clientItem);
            if (previous != null) {
                throw new IllegalStateException("Duplicate item model definition for " + item);
            }
        }
        
        @Override
        public void register(Identifier identifier, ClientItem clientItem) {
            ClientItem existing = this.idItemInfos.putIfAbsent(identifier, clientItem);
            if (existing != null) {
                throw new IllegalStateException("Duplicate item model definition for " + identifier);
            }
        }
        
        @Override
        public void copy(Item donor, Item acceptor) {
            this.copies.put(acceptor, donor);
        }
        
        private void finalizeAndValidate() {
            this.knownItems.get().map(Holder::value).forEach(item -> {
                if (!this.copies.containsKey(item) && item instanceof BlockItem blockItem && !this.itemInfos.containsKey(blockItem)) {
                    Identifier targetModel = ModelLocationUtils.getModelLocation(blockItem.getBlock());
                    this.accept(blockItem, ItemModelUtils.plainModel(targetModel));
                }
            });
            
            this.copies.forEach((acceptor, donor) -> {
                ClientItem donorInfo = this.itemInfos.get(donor);
                if (donorInfo == null) {
                    throw new IllegalStateException("Missing donor: " + donor + " -> " + acceptor);
                }
                this.register(acceptor, donorInfo);
            });
            
            List<Identifier> missingDefinitions = this.knownItems.get()
                    .filter(holder -> !this.itemInfos.containsKey(holder.value()))
                    .map(holder -> holder.unwrapKey().orElseThrow().identifier())
                    .toList();
            if (!missingDefinitions.isEmpty()) {
                throw new IllegalStateException("Missing item model definitions for: " + missingDefinitions);
            }
        }
        
        private CompletableFuture<?> save(CachedOutput cache, PackOutput.PathProvider pathProvider) {
            return CompletableFuture.allOf(
                    DataProvider.saveAll(cache, ClientItem.CODEC,
                            item -> pathProvider.json(item.builtInRegistryHolder().key().identifier()),
                            this.itemInfos),
                    DataProvider.saveAll(cache, ClientItem.CODEC, pathProvider::json, this.idItemInfos));
        }
    }
    
    private static class SimpleModelCollector implements BiConsumer<Identifier, ModelInstance> {
        private final Map<Identifier, ModelInstance> models = new HashMap<>();
        
        @Override
        public void accept(Identifier id, ModelInstance contents) {
            ModelInstance previous = this.models.put(id, contents);
            if (previous != null) {
                throw new IllegalStateException("Duplicate model definition for " + id);
            }
        }
        
        private CompletableFuture<?> save(CachedOutput cache, PackOutput.PathProvider pathProvider) {
            return DataProvider.saveAll(cache, Supplier::get, pathProvider::json, this.models);
        }
    }
}
