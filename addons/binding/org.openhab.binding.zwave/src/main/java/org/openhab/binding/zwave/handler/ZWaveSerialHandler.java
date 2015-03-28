/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zwave.handler;

import static org.openhab.binding.zwave.ZWaveBindingConstants.*;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ZWaveSerialHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author Chris Jackson - Initial contribution
 */
public class ZWaveSerialHandler extends ZWaveControllerHandler {

    private Logger logger = LoggerFactory.getLogger(ZWaveSerialHandler.class);

    private String portId;
    
	public ZWaveSerialHandler(Thing thing) {
		super(thing);
	}
	
	@Override
	public void initialize() {
		logger.debug("Initializing ZWave serial controller.");

		// TODO: The serial stuff needs to be separated from the controller
		// TODO: and then initialised here
		portId = (String) getConfig().get(PARAMETER_PORT);

		super.initialize();

		initializeNetwork(portId);
	}
	
	@Override
	public void dispose() {
		// TODO: Put the serial close in here
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
//        if(channelUID.getId().equals(CHANNEL_1)) {
            // TODO: handle command
//        }
	}
}
