package online.cszt0.mcmod.bank.util;

import java.io.Serializable;
import java.math.BigDecimal;

public interface Serialize<T> {
    byte[] serialize(T obj);

    T deserialize(byte[] data);

    @Deprecated(forRemoval = true)
    static Serialize<Serializable> serializable() {
        return SerializableSerialize.getInstance();
    }

    static Serialize<BigDecimal> bigDecimal() {
        return BigDecimalSerialize.getInstance();
    }

}
