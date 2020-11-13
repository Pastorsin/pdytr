import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import java.io.Serializable;


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

        this.cpuUsado = bean.getSystemCpuLoad() * 100;
        this.memoriaDisponible = bean.getFreePhysicalMemorySize();
    }

    public String getContainer() {
        return container;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-10.2f %d Bytes",
                             container, cpuUsado, memoriaDisponible);
    }

}