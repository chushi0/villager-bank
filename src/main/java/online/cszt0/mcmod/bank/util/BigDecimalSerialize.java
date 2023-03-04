package online.cszt0.mcmod.bank.util;

import java.math.BigDecimal;
import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class BigDecimalSerialize implements Serialize<BigDecimal> {

    @Getter
    private static final BigDecimalSerialize instance = new BigDecimalSerialize();

    private static final char[] LEGAL_CHARS = { '\0', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '.',
            ',', ' ' };

    private static final HashMap<Character, Integer> map = new HashMap<>();

    static {
        if (LEGAL_CHARS.length > 16) {
            throw new ExceptionInInitializerError();
        }
        for (int i = 0; i < LEGAL_CHARS.length; i++) {
            map.put(LEGAL_CHARS[i], i);
        }
    }

    @Override
    public byte[] serialize(BigDecimal obj) {
        String plainString = obj.toPlainString();
        char[] chars = plainString.toCharArray();
        int len = chars.length / 2 + 1;
        byte[] data = new byte[len];
        for (int i = 0; i < chars.length; i++) {
            data[i / 2] |= map.get(chars[i]) << (i % 2 * 4);
        }
        return data;
    }

    @Override
    public BigDecimal deserialize(byte[] data) {
        char[] chars = new char[data.length * 2];
        int len = 0;
        for (int i = 0; i < chars.length; i++) {
            int b = (data[i / 2] >> (i % 2 * 4)) & 0xf;
            char c = LEGAL_CHARS[b];
            if (c == '\0') {
                break;
            }
            chars[i] = c;
            len++;
        }
        return new BigDecimal(chars, 0, len);
    }
}
