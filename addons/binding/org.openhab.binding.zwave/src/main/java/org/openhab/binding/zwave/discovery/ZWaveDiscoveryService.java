/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zwave.discovery;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.zwave.ZWaveBindingConstants;
import org.openhab.binding.zwave.handler.ZWaveControllerHandler;
import org.openhab.binding.zwave.internal.protocol.ZWaveNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ZWaveDiscoveryService} tracks ZigBee lights which are associated
 * to coordinator.
 * 
 * @author Chris Jackson - Initial contribution
 *
 */
public class ZWaveDiscoveryService extends AbstractDiscoveryService {
	private final Logger logger = LoggerFactory
			.getLogger(ZWaveDiscoveryService.class);

	private final static int SEARCH_TIME = 30;

	private ZWaveControllerHandler controllerHandler;

	private Set<ThingTypeUID> supportedThings;

	public ZWaveDiscoveryService(ZWaveControllerHandler coordinatorHandler) {
		super(SEARCH_TIME);
		this.controllerHandler = coordinatorHandler;
	}

	public void activate() {
		logger.debug("Activating ZWave discovery service for {}",
				controllerHandler.getThing().getUID());

		// Listen for device events
		// coordinatorHandler.addDeviceListener(this);

		// startScan();
	}

	@Override
	public void deactivate() {
		logger.debug("Deactivating ZWave discovery service for {}",
				controllerHandler.getThing().getUID());

		// Remove the listener
		// coordinatorHandler.removeDeviceListener(this);
	}

	@Override
	public Set<ThingTypeUID> getSupportedThingTypes() {
		return supportedThings;
	}

	@Override
	public void startScan() {
		logger.debug("Starting ZWave inclusion scan for {}", controllerHandler
				.getThing().getUID());

		// Start the search for new devices
		controllerHandler.startDeviceDiscovery();
	}

	private ThingUID getThingUID(ZWaveNode node) {
		ThingUID bridgeUID = controllerHandler.getThing().getUID();

		
		// TODO: Needs to get the real UUID
		// Out ThingType UID is based on the device type 
		ThingTypeUID thingTypeUID = new ThingTypeUID(
				ZWaveBindingConstants.BINDING_ID);

		if (getSupportedThingTypes().contains(thingTypeUID)) {
			String thingId = "NODE" + node.getNodeId();
			ThingUID thingUID = new ThingUID(thingTypeUID, bridgeUID, thingId);
			return thingUID;
		} else {
			return null;
		}
	}

	public void deviceAdded(ZWaveNode node) {
		logger.debug("Device discovery: {} {} {}", node.getNodeId());

		ThingUID thingUID = getThingUID(node);
		if (thingUID != null) {
			// TODO: Get the device description here!
			String label = "";

			ThingUID bridgeUID = controllerHandler.getThing().getUID();
			Map<String, Object> properties = new HashMap<>(1);
			properties.put(ZWaveBindingConstants.PARAMETER_NODEID,
					node.getNodeId());
			DiscoveryResult discoveryResult = DiscoveryResultBuilder
					.create(thingUID).withProperties(properties)
					.withBridge(bridgeUID).withLabel(label).build();

			thingDiscovered(discoveryResult);
		} else {
			logger.debug("NODE {}: Discovered unknown device", node.getNodeId());
		}
	}

	public void deviceRemoved(ZWaveNode node) {
		ThingUID thingUID = getThingUID(node);

		if (thingUID != null) {
			thingRemoved(thingUID);
		}
	}

	@Override
	protected void startBackgroundDiscovery() {
	}
}