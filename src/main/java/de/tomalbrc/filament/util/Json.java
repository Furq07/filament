package de.tomalbrc.filament.util;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import eu.pb4.polymer.blocks.api.BlockModelType;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Json {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(BlockState.class, new BlockStateDeserializer())
            .registerTypeHierarchyAdapter(EquipmentSlot.class, new EquipmentSlotDeserializer())
            .registerTypeHierarchyAdapter(Vector3f.class, new Vector3fDeserializer())
            .registerTypeHierarchyAdapter(Vector2f.class, new Vector2fDeserializer())
            .registerTypeHierarchyAdapter(Quaternionf.class, new QuaternionfDeserializer())
            .registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeHierarchyAdapter(BlockModelType.class, new BlockModelTypeDeserializer())
            .registerTypeHierarchyAdapter(PushReaction.class, new PushReactionDeserializer())
            .registerTypeHierarchyAdapter(Block.class, new RegistryDeserializer<>(BuiltInRegistries.BLOCK))
            .registerTypeHierarchyAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT))
            .create();

    public static class BlockStateDeserializer implements JsonDeserializer<BlockState> {
        @Override
        public BlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsString().toLowerCase();

            BlockStateParser.BlockResult parsed;
            try {
                parsed = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), name, false);
            } catch (CommandSyntaxException e) {
                throw new JsonParseException("Invalid BlockState value: " + name);
            }

            return parsed.blockState();
        }
    }

    public static class EquipmentSlotDeserializer implements JsonDeserializer<EquipmentSlot> {
        @Override
        public EquipmentSlot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsString().toLowerCase();

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.name().equalsIgnoreCase(name)) {
                    return slot;
                }
            }

            throw new JsonParseException("Invalid EquipmentSlot value: " + name);
        }
    }

    public static class QuaternionfDeserializer implements JsonDeserializer<Quaternionf> {
        @Override
        public Quaternionf deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            if (jsonArray.size() < 3) {
                throw new JsonParseException("Array size should be at least 3 for euler angle deserialization.");
            }

            float x = jsonArray.get(0).getAsFloat();
            float y = jsonArray.get(1).getAsFloat();
            float z = jsonArray.get(2).getAsFloat();

            return new Quaternionf().rotateXYZ(x * Mth.DEG_TO_RAD, y * Mth.DEG_TO_RAD, z * Mth.DEG_TO_RAD);
        }
    }

    public static class Vector3fDeserializer implements JsonDeserializer<Vector3f> {
        @Override
        public Vector3f deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            if (jsonArray.size() < 3) {
                throw new JsonParseException("Array size should be at least 3 for Vector3f deserialization.");
            }

            float x = jsonArray.get(0).getAsFloat();
            float y = jsonArray.get(1).getAsFloat();
            float z = jsonArray.get(2).getAsFloat();

            return new Vector3f(x, y, z);
        }
    }

    public static class Vector2fDeserializer implements JsonDeserializer<Vector2f> {
        @Override
        public Vector2f deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            if (jsonArray.size() < 2) {
                throw new JsonParseException("Array size should be at least 2 for Vector2f deserialization.");
            }

            float x = jsonArray.get(0).getAsFloat();
            float y = jsonArray.get(1).getAsFloat();

            return new Vector2f(x, y);
        }
    }

    private static class BlockModelTypeDeserializer implements JsonDeserializer<BlockModelType> {
        @Override
        public BlockModelType deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                throw new JsonParseException("Expected string, got " + element);
            }

            String value = element.getAsString().toUpperCase();
            try {
                return BlockModelType.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Invalid BlockModelType value: " + value, e);
            }
        }
    }

    private static class PushReactionDeserializer implements JsonDeserializer<PushReaction> {
        @Override
        public PushReaction deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                throw new JsonParseException("Expected string, got " + element);
            }

            String value = element.getAsString().toUpperCase();
            try {
                return PushReaction.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Invalid BlockModelType value: " + value, e);
            }
        }
    }

    private record RegistryDeserializer<T>(Registry<T> registry) implements JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            return this.registry.get(new ResourceLocation(element.getAsString()));
        }
    }

    public static class DataComponentsDeserializer implements JsonDeserializer<DataComponentMap> {
        @Override
        public DataComponentMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            DataResult<Pair<DataComponentMap, JsonElement>> result = DataComponentMap.CODEC.decode(JsonOps.INSTANCE, jsonElement);

            if (!result.result().isPresent()) {
                return null;
            }

            return result.result().get().getFirst();
        }
    }
}
