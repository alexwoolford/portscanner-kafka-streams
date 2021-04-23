package io.woolford.serde;

import io.woolford.PortscanRecord;
import org.apache.kafka.common.serialization.Serdes;

public class PortscanRecordSerde extends Serdes.WrapperSerde<PortscanRecord> {
    public PortscanRecordSerde() {
        super(new JsonSerializer(), new JsonDeserializer(PortscanRecord.class));
    }
}