import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by lahmann on 2017-01-17.
 */
class Configuration {

    //private static final File configFile = new File("./lib/config.cfg");
    private static final File configFile = new File("/leia/HVPowerSupplyController/lib/config.cfg");

    private static String acromagIpAddress = "192.168.100.56";
    private static Integer modbusPort = 502;
    private static Integer pollPeriod = 1000;
    private static Integer mainWindowPosX = 100;
    private static Integer mainWindowPosY = 100;

    static void loadConfiguration(){

        try {
            Scanner s = new Scanner(configFile);
            s.useDelimiter(";|\\n");

            while (s.hasNext()) {

                switch (s.next()) {
                    case "acromagIpAddress":
                        acromagIpAddress = s.next();
                        break;
                    case "modbusPort":
                        modbusPort = Integer.valueOf(s.next());
                        break;
                    case "pollPeriod":
                        pollPeriod = Integer.valueOf(s.next());
                        break;
                    case "mainWindowPosX":
                        mainWindowPosX = Integer.valueOf(s.next());
                        break;
                    case "mainWindowPosY":
                        mainWindowPosY = Integer.valueOf(s.next());
                        break;
                }
            }
        }
        catch (FileNotFoundException e){
            /**
             * If we can't find the file, we'll just use the hard coded defaults
             */
        }

    }

    static void writeConfiguration(){

        try {
            FileWriter w = new FileWriter(configFile);

            w.write("acromagIpAddress;" + acromagIpAddress);
            w.write("\nmodbusPort;" + modbusPort);
            w.write("\npollPeriod;" + pollPeriod);

            w.write("\nmainWindowPosX;" + mainWindowPosX);
            w.write("\nmainWindowPosY;" + mainWindowPosY);

            w.close();
        }
        catch (IOException exception){

        }

    }

    static String getAcromagIpAddress() {
        return acromagIpAddress;
    }

    static Integer getModbusPort() {
        return modbusPort;
    }

    static Integer getPollPeriod() {
        return pollPeriod;
    }

    static Integer getMainWindowPosX() {
        return mainWindowPosX;
    }

    static Integer getMainWindowPosY() {
        return mainWindowPosY;
    }

    static void setAcromagIpAddress(String acromagIpAddress) {
        Configuration.acromagIpAddress = acromagIpAddress;
    }

    static void setModbusPort(Integer modbusPort) {
        Configuration.modbusPort = modbusPort;
    }

    static void setPollPeriod(Integer pollPeriod) {
        Configuration.pollPeriod = pollPeriod;
    }

    static void setMainWindowPosX(Integer mainWindowPosX) {
        Configuration.mainWindowPosX = mainWindowPosX;
    }

    static void setMainWindowPosY(Integer mainWindowPosY) {
        Configuration.mainWindowPosY = mainWindowPosY;
    }
}
