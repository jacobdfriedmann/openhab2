/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zwave.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Dictionary;
import java.util.Set;

import org.openhab.binding.zwave.ZWaveBindingConstants;

import com.google.common.collect.Sets;

public class ZWaveThingHandler extends BaseThingHandler  {

	public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets .newHashSet();

	private Logger logger = LoggerFactory.getLogger(ZWaveThingHandler.class);

	private ZWaveControllerHandler controllerHandler;

	private int nodeId;

	public ZWaveThingHandler(Thing zwaveDevice) {
		super(zwaveDevice);
	}

	@Override
	public void initialize() {
		logger.debug("Initializing ZWave thing handler.");
		final BigInteger nodeId = (BigInteger) getConfig().get(
				ZWaveBindingConstants.PARAMETER_NODEID);

		
	}

	protected void bridgeHandlerInitialized(ThingHandler thingHandler, Bridge bridge) {
		controllerHandler = (ZWaveControllerHandler) thingHandler;

		if (nodeId != 0) {
			if (controllerHandler != null) {
				// getThing().setStatus(getBridge().getStatus());
			}
		}
		// Until we get an update put the Thing offline
		updateStatus(ThingStatus.OFFLINE);
	}

	@Override
	public void dispose() {
		logger.debug("Handler disposes. Unregistering listener.");
		if (nodeId != 0) {
			if (controllerHandler != null) {
			}
			nodeId = 0;
		}
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		if (controllerHandler == null) {
			logger.warn("Coordinator handler not found. Cannot handle command without coordinator.");
			return;
		}

		switch (channelUID.getId()) {
		}
	}

}
