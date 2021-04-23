package io.woolford;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PortScanner {

    public PortscanRecord scanIp(IpRecord ip) throws ExecutionException, InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(500);
        final int timeout = 300;
        final List<Future<Boolean>> futures = new ArrayList<>();
        for (int port = 1; port <= 65535; port++) {
            futures.add(portIsOpen(es, ip.getIp(), port, timeout));
        }
        es.shutdown();

        List<Integer> openPorts = new ArrayList<>();

        int port = 1;
        for (final Future<Boolean> f : futures) {
            if (f.get()) {
                openPorts.add(port);
            }
            port++;
        }

        PortscanRecord portscanRecord = new PortscanRecord();
        portscanRecord.setIp(ip.getIp());
        portscanRecord.setOpenPorts(openPorts);

        return portscanRecord;
    }

    private static Future<Boolean> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(new Callable<Boolean>() {
            @Override public Boolean call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });
    }

}
