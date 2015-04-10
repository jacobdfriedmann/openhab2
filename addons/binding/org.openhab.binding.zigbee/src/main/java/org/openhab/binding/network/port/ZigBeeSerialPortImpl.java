package org.openhab.binding.network.port;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import org.bubblecloud.zigbee.network.port.ZigBeePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The implementation of SerialPort.
 *
 * @author Chris Jackson
 */
public class ZigBeeSerialPortImpl implements ZigBeePort {
	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory
			.getLogger(ZigBeeSerialPortImpl.class);

	// The serial port.
	private SerialPort serialPort;
	
	// The serial port input stream.
	private InputStream inputStream;
	
	// The serial port output stream.
	private OutputStream outputStream;

	private final String portIdentifier;
	private final int    baudRate;

	public ZigBeeSerialPortImpl(final String portIdentifier, final int baudRate) {
		this.portIdentifier = portIdentifier;
		this.baudRate = baudRate;
	}

    @Override
    public boolean open() {
        try {
            openSerialPort(portIdentifier, baudRate);
            return true;
        } catch (Exception e) {
            logger.error("Error...", e);
            return false;
        }
    }

	private void openSerialPort(final String serialPortName, int baudRate) {
		logger.info("Connecting to serial port {}", serialPortName);
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(serialPortName);
			CommPort commPort = portIdentifier.open(
					"org.openhab.binding.zigbee", 2000);
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			this.serialPort.enableReceiveThreshold(1);
			this.serialPort.enableReceiveTimeout(120000);

			// RXTX serial port library causes high CPU load
			// Start event listener, which will just sleep and slow down event
			// loop
			// serialPort.addEventListener(this.receiveThread);
			// serialPort.notifyOnDataAvailable(true);

			logger.info("Serial port is initialized");
		} catch (NoSuchPortException e) {
			logger.error("Serial Error: Port {} does not exist", serialPortName);
			return;
		} catch (PortInUseException e) {
			logger.error("Serial Error: Port {} in use.", serialPortName);
			return;
		} catch (UnsupportedCommOperationException e) {
			logger.error(
					"Serial Error: Unsupported comm operation on Port {}.",
					serialPortName);
			return;
		}

		try {
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}

	@Override
	public void close() {
		try {
			if (serialPort != null) {
				while (inputStream.available() > 0) {
					try {
						Thread.sleep(100);
					} catch (final InterruptedException e) {
						logger.warn("Interrupted while waiting input stream to flush.");
					}
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
				serialPort.close();
				// logger.info("Serial port '" + serialPort.getName() +
				// "' closed.");
				serialPort = null;
				inputStream = null;
				outputStream = null;
			}
		} catch (Exception e) {
			// logger.warn("Error closing serial port: '" + serialPort.getName()
			// + "'", e);
		}
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
}
