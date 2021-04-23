package io.woolford;

import io.woolford.serde.IpRecordSerde;
import io.woolford.serde.PortscanRecordSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class PortscanStreamer {

    final Logger LOG = LoggerFactory.getLogger(PortscanStreamer.class);

    void run() throws IOException {

        // load properties
        Properties props = new Properties();
        InputStream input = PortscanStreamer.class.getClassLoader().getResourceAsStream("config.properties");
        props.load(input);

        IpRecordSerde ipRecordSerde = new IpRecordSerde();
        PortscanRecordSerde portscanRecordSerde = new PortscanRecordSerde();

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ipRecordSerde.getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        //TODO: parameterize source and destination topics from props file
        //TODO: Dockerize for ease of use

        // create a stream of ip addresses to scan
        KStream<byte[], IpRecord> ipStream = builder.stream("neo4j-fortigate-ip-clean", Consumed.with(Serdes.ByteArray(), ipRecordSerde));

        ipStream.map((key, value) -> {

            PortScanner portScanner = new PortScanner();
            PortscanRecord portscanRecord = new PortscanRecord();
            try {
                portscanRecord = portScanner.scanIp(value);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            return new KeyValue<>(key, portscanRecord);

        }).to("portscan");

        // run it
        final Topology topology = builder.build();

        // show topology
        LOG.info(topology.describe().toString());

        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.cleanUp();
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }

}
