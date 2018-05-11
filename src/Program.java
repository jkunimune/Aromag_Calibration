import java.io.File;
import java.io.FileWriter;

public class Program {

    static double dV = 0.05;
    static double MAX_VOLTAGE = 10.0;

    public static void main(String ... args) throws Exception{

        FileWriter w = new FileWriter(new File("calibration.dat"));

        AcromagController controller = new AcromagController(
                Configuration.getAcromagIpAddress(), Configuration.getModbusPort());

        double voltageSetting = (-1)*MAX_VOLTAGE;
        while (voltageSetting < MAX_VOLTAGE){

            System.out.println(voltageSetting);
            w.write(String.format("%.4f;", voltageSetting));
            for (int i = 0; i < 16; i++) {
                double voltageReading;

                controller.setChannelOutputVoltage(i, voltageSetting);
                voltageReading = controller.getAcromagReadbackVoltage(i);
                controller.setChannelOutputVoltage(i, 0);

                w.write(String.format("%.4f;", voltageReading));
            }
            w.write("\n");
            voltageSetting += dV;
        }

        w.close();
    }
}
