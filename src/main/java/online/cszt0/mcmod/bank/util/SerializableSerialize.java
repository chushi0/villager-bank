package online.cszt0.mcmod.bank.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Deprecated(forRemoval = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SerializableSerialize implements Serialize<Serializable> {

    @Getter
    private static final Serialize<Serializable> instance = new SerializableSerialize();

    @Override
    @SneakyThrows
    public byte[] serialize(Serializable obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        return outputStream.toByteArray();
    }

    @Override
    @SneakyThrows
    public Serializable deserialize(byte[] data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (Serializable) objectInputStream.readObject();
    }
}