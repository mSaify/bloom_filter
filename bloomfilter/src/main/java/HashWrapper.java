

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.MurmurHash2;


public class HashWrapper {

    int randomSeed = 0;

    HashWrapper(int _randomSeed) {
        randomSeed = _randomSeed;
    }

    public long getHashValue(String val) {
        byte[] bytes = StringUtils.getBytesUtf8(val);
        return MurmurHash2.hash64(bytes, bytes.length, randomSeed);
    }
}
