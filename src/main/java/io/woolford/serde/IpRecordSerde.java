package io.woolford.serde;

import io.woolford.IpRecord;
import org.apache.kafka.common.serialization.Serdes;

public class IpRecordSerde extends Serdes.WrapperSerde<IpRecord> {
    public IpRecordSerde() {
        super(new JsonSerializer(), new JsonDeserializer(IpRecord.class));
    }
}
