package online.cszt0.mcmod.bank.net;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public abstract class NetworkPackage {
    public final void send(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, identifier(), getMarshalResult());
        }
    }

    protected abstract Identifier identifier();

    public final PacketByteBuf getMarshalResult() {
        PacketByteBuf buf = PacketByteBufs.create();
        marshal(buf);
        return buf;
    }

    @SneakyThrows
    public void marshal(PacketByteBuf buf) {
        Class<? extends NetworkPackage> clazz = getClass();
        List<Pair<Field, Net>> list = Arrays.stream(clazz.getDeclaredFields())
                .map(field -> new Pair<>(field, field.getAnnotation(Net.class)))
                .filter(pair -> pair.getRight() != null)
                .sorted(Comparator.comparingInt(pair -> pair.getRight().order()))
                .collect(Collectors.toList());
        for (Pair<Field, Net> pair : list) {
            Field field = pair.getLeft();
            field.setAccessible(true);
            Class<?> type = field.getType();
            Object value = field.get(this);
            if (value == null) {
                throw new NullPointerException("unsupport marshal null value");
            }
            if (type == Integer.TYPE) {
                buf.writeInt((int) value);
            } else if (type == String.class) {
                buf.writeString((String) value);
            } else if (type == BlockPos.class) {
                buf.writeBlockPos((BlockPos) value);
            } else if (type == Text.class) {
                buf.writeText((Text) value);
            } else if (type == UUID.class) {
                buf.writeUuid((UUID) value);
            } else if (NetworkPackage.class.isAssignableFrom(type)) {
                ((NetworkPackage) value).marshal(buf);
            } else {
                throw new IllegalArgumentException("unsupport type to marshal: " + type);
            }
        }
    }

    @SneakyThrows
    public void unmarshal(PacketByteBuf buf) {
        Class<? extends NetworkPackage> clazz = getClass();
        List<Pair<Field, Net>> list = Arrays.stream(clazz.getDeclaredFields())
                .map(field -> new Pair<>(field, field.getAnnotation(Net.class)))
                .filter(pair -> pair.getRight() != null)
                .sorted(Comparator.comparingInt(pair -> pair.getRight().order()))
                .collect(Collectors.toList());
        for (Pair<Field, Net> pair : list) {
            Field field = pair.getLeft();
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (type == Integer.TYPE) {
                field.set(this, buf.readInt());
            } else if (type == String.class) {
                field.set(this, buf.readString());
            } else if (type == BlockPos.class) {
                field.set(this, buf.readBlockPos());
            } else if (type == Text.class) {
                field.set(this, buf.readText());
            } else if (type == UUID.class) {
                field.set(this, buf.readUuid());
            } else if (NetworkPackage.class.isAssignableFrom(type)) {
                NetworkPackage object = (NetworkPackage) type.getConstructor().newInstance();
                object.unmarshal(buf);
                field.set(this, object);
            } else {
                throw new IllegalArgumentException("unsupport type to marshal: " + type);
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Net {
        int order() default -1;
    }
}
