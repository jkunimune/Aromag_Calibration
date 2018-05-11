import net.wimpi.modbus.facade.ModbusTCPMaster;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;

import java.net.InetAddress;


/**
 * Class the handles all writing and reading to the Acromag unit
 */
class AcromagController {

    private final int MAX_DATA_VALUE = 30000;

    private TCPMasterConnection connection;
    private ModbusTCPMaster master;


    AcromagController(){
    }


    /**
     * Basic constructor
     * @param ipAddress IP address that will be used to attempt to establish communication with the Acromag
     * @param port Modbus port to use in communications
     */
    AcromagController(String ipAddress, Integer port) throws Exceptions.AcromagConnectionException {
        this(ipAddress, port, Configuration.getPollPeriod());
    }


    /**
     * Basic constructor
     * @param ipAddress IP address that will be used to attempt to establish communication with the Acromag
     * @param port Modbus port to use in communications
     * @param timeout Time (ms) before we give up trying to connect
     */
    AcromagController(String ipAddress, Integer port, Integer timeout) throws Exceptions.AcromagConnectionException {

        try {
            connection = new TCPMasterConnection(InetAddress.getByName(ipAddress));
            connection.setTimeout(timeout);
            connection.setPort(port);
            connection.connect();

            master = new ModbusTCPMaster(ipAddress, port);
            master.connect();
        }
        catch (Exception e) {
            disconnect();
            throw new Exceptions.AcromagConnectionException(ipAddress);
        }
    }


    /**
     * Check the state of the connection between the software and the Acromag
     * @return true if connected, false otherwise
     */
    boolean isConnected(){
        if (connection == null || master == null) return false;
        return connection.isConnected();
    }


    /**
     * Return the current IP address as a string
     * @return the IP address as a string
     */
    String getAddress(){
        return connection.getAddress().toString();
    }


    /**
     * Disconnect from the Acromag
     */
    void disconnect(){
        if (connection != null) connection.close();
        if (master != null)     master.disconnect();
    }


    double getAcromagInputVoltage(int channelID) throws Exceptions.ReadInputVoltageException, Exceptions.AcromagConnectionException{

        if (!isConnected()) throw new Exceptions.AcromagConnectionException(Configuration.getAcromagIpAddress());

        // Get the addresses we need from the dictionary
        int configAddress = Constants.getInputChannelConfigAddress(channelID);
        int dataAddress   = Constants.getInputChannelDataAddress(channelID);


        // Read the config register
        InputRegister configRegister = null;
        try {
            configRegister = master.readInputRegisters(configAddress, 1)[0];
        }catch (Exception e) {
            throw new Exceptions.ReadInputVoltageException(channelID, configAddress);
        }


        // Read the data register
        InputRegister dataRegister = null;
        try{
            dataRegister = master.readInputRegisters(dataAddress, 1)[0];
        }
        catch (Exception e){
            throw new Exceptions.ReadInputVoltageException(channelID, dataAddress);
        }


        // The voltage corresponding to the max data value is either 5V or 10V depending on whether the
        // 0th bit of the config register is 0 or 1 respectively
        double maxVoltage;
        int bit = getBit(configRegister.toShort(), 0);
        if (bit == 0){
            maxVoltage = 5.0;
        }else{
            maxVoltage = 10.0;
        }


        // Return the corresponding voltage
        return maxVoltage * ((double) dataRegister.toShort() / (double) MAX_DATA_VALUE);
    }

    double getAcromagReadbackVoltage(int channelID) throws Exceptions.ReadInputVoltageException, Exceptions.AcromagConnectionException{

        if (!isConnected()) throw new Exceptions.AcromagConnectionException(Configuration.getAcromagIpAddress());

        // Get the addresses we need from the dictionary
        int configAddress = Constants.getOutputChannelConfigAddress(channelID);
        int dataAddress   = Constants.getReadbackChannelDataAddress(channelID);


        // Read the config register
        InputRegister configRegister = null;
        try {
            configRegister = master.readInputRegisters(configAddress, 1)[0];
        }catch (Exception e) {
            throw new Exceptions.ReadInputVoltageException(channelID, configAddress);
        }


        // Read the data register
        InputRegister dataRegister = null;
        try{
            dataRegister = master.readInputRegisters(dataAddress, 1)[0];
        }
        catch (Exception e){
            throw new Exceptions.ReadInputVoltageException(channelID, dataAddress);
        }


        // The voltage corresponding to the max data value is either 5V or 10V depending on whether the
        // 0th bit of the config register is 0 or 1 respectively
        double maxVoltage;
        int bit = getBit(configRegister.toShort(), 0);
        if (bit == 0){
            maxVoltage = 10.0;
        }else{
            maxVoltage = 5.0;
        }


        // Return the corresponding voltage
        return maxVoltage * ((double) dataRegister.toShort() / (double) MAX_DATA_VALUE);
    }

    void setChannelOutputVoltage(int channelID, double voltage) throws Exceptions.WriteOutputVoltageException, Exceptions.ReadInputVoltageException, Exceptions.AcromagConnectionException {

        if (!isConnected()) throw new Exceptions.AcromagConnectionException(Configuration.getAcromagIpAddress());

        // Get the addresses we need from the dictionary
        int configAddress = Constants.getOutputChannelConfigAddress(channelID);
        int dataAddress   = Constants.getOutputChannelDataAddress(channelID);


        // Read the config register
        Register configRegister = null;
        try {
            configRegister = master.readMultipleRegisters(configAddress, 1)[0];
        }
        catch (Exception e){
            throw new Exceptions.ReadInputVoltageException(channelID, configAddress);
        }


        // The voltage corresponding to the max data value is either 5V or 10V depending on whether the
        // 0th bit of the config register is 1 or 0 respectively
        double maxVoltage;
        int bit = getBit(configRegister.toShort(), 0);
        if (bit == 0){
            maxVoltage = 10.0;
        }else{
            maxVoltage = 5.0;
        }


        // Calculate the data value corresponding to the requested voltage
        int dataValue = (int) (MAX_DATA_VALUE * (voltage / maxVoltage));


        // Set that value to the data channel
        try {
            Register dataRegister = new SimpleRegister(dataValue);
            master.writeSingleRegister(dataAddress, dataRegister);
        }
        catch (Exception e){
            throw new Exceptions.WriteOutputVoltageException(channelID, voltage, dataAddress);
        }
    }


    /**
     * Convenience function for getting the nth bit for an integer
     * @param integer Any positive integer
     * @param n Index of the bit of interest
     * @return Value (0 or 1) of the nth bit of the integer
     */
    private int getBit(int integer, int n){
        return (integer >> n) & 1;
    }
}
