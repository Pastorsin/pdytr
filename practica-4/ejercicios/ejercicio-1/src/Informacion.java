import static java.lang.Math.pow;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import java.io.Serializable;

import java.net.*;


public class Informacion implements Serializable {
    private String container;
    private double cpuUsado;
    private long memoriaDisponible;

    public Informacion(String container) {
        this.container = container;
    }

    public void actualizar() throws Exception {
        OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory
                                     .getOperatingSystemMXBean();

        this.cpuUsado = bean.getSystemCpuLoad();
        this.memoriaDisponible = bean.getFreePhysicalMemorySize();
    }

    public String getContainer() {
        return container;
    }

    public String getHostname() {
        InetAddress ip;
        String hostname;

        try {
            ip = InetAddress.getLocalHost();
            return ip.getHostName();
        } catch (UnknownHostException e) {
            return "Desconocido";
        }
    }

    @Override
    public String toString() {
        return String.format("%-20s %-20s %-10.2f %.2f MB",
                             container,
                             getHostname(),
                             cpuUsado * 100,
                             memoriaDisponible / pow(10, 6));
    }

}