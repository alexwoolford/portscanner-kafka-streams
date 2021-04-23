# portscanner-kafka-streams

This is a Kafka Streams port-scanner that takes an IP address as an input, scans all the ports, and returns the original IP with a list of open ports.

Input:

    {"ip":"10.0.1.41"}

Output:

    {"ip":"10.0.1.41","openPorts":[22,111,7083,8083,8090,8092,9092,9093,9100]}
