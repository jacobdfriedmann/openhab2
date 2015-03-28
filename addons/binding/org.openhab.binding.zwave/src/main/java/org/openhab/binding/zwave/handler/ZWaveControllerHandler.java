/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zwave.handler;

import static org.openhab.binding.zwave.ZWaveBindingConstants.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.zwave.discovery.ZWaveDiscoveryService;
import org.openhab.binding.zwave.internal.ZWaveNetworkMonitor;
import org.openhab.binding.zwave.internal.converter.ZWaveConverterHandler;
import org.openhab.binding.zwave.internal.protocol.SerialInterfaceException;
import org.openhab.binding.zwave.internal.protocol.ZWaveController;
import org.openhab.binding.zwave.internal.protocol.ZWaveEventListener;
import org.openhab.binding.zwave.internal.protocol.ZWaveNode;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveEvent;
import org.openhab.binding.zwave.internal.protocol.initialization.ZWaveNodeInitStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ZWaveControllerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author Chris Jackson - Initial contribution
 */
public class ZWaveControllerHandler extends BaseThingHandler implements ZWaveEventListener {

    private Logger logger = LoggerFactory.getLogger(ZWaveControllerHandler.class);

    private ZWaveDiscoveryService discoveryService;
    
	private volatile ZWaveController controller;
	private volatile ZWaveConverterHandler converterHandler;
	
	// Network monitoring class
	ZWaveNetworkMonitor networkMonitor;


//	private Iterator<ZWavePollItem> pollingIterator = null;
//	private List<ZWavePollItem> pollingList = new ArrayList<ZWavePollItem>();
    
    private Boolean isMaster;
    private Boolean isSUC;
    
	public ZWaveControllerHandler(Thing thing) {
		super(thing);
	}
	
	@Override
	public void initialize() {
		logger.debug("Initializing ZWave Controller.");

		isMaster = (Boolean) getConfig().get(PARAMETER_MASTER);
		isSUC = (Boolean) getConfig().get(PARAMETER_SUC);

		super.initialize();
	}
	
	/**
	 * Common initialisation point for all ZigBee coordinators.
	 * Called by bridges after they have initialised their interfaces.
	 * @param networkInterface a ZigBeePort interface instance
	 */
	protected void initializeNetwork(String port) {
		try {
			logger.debug("Initialising ZWave controller");

			// TODO: Handle soft reset better!
			controller = new ZWaveController(isMaster, isSUC, port, 5000, false);
			converterHandler = new ZWaveConverterHandler(controller);
			controller.addEventListener(this);

			// The network monitor service needs to know the controller...
			this.networkMonitor = new ZWaveNetworkMonitor(controller);
//			if(healtime != null) {
//				this.networkMonitor.setHealTime(healtime);
//			}
//			if(aliveCheckPeriod != null) {
//				this.networkMonitor.setPollPeriod(aliveCheckPeriod);
//			}
//			if(softReset != false) {
//				this.networkMonitor.resetOnError(softReset);
//			}

			// The config service needs to know the controller and the network monitor...
//			this.zConfigurationService = new ZWaveConfiguration(this.zController, this.networkMonitor);
//			zController.addEventListener(this.zConfigurationService);
			return;
		} catch (SerialInterfaceException ex) {
//			throw new ConfigurationException("port", ex.getLocalizedMessage(), ex);
		}

	}
	
	@Override
	public void dispose() {
		// Remove the discovery service
		discoveryService.deactivate();

		if (this.converterHandler != null) {
			this.converterHandler = null;
		}

//		if (this.zConfigurationService != null) {
//			this.zController.removeEventListener(this.zConfigurationService);
//			this.zConfigurationService = null;
//		}

		ZWaveController controller = this.controller;
		if (controller != null) {
			this.controller = null;
			controller.close();
			controller.removeEventListener(this);
		}
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
//        if(channelUID.getId().equals(CHANNEL_1)) {
            // TODO: handle command
//        }
	}

	public void startDeviceDiscovery() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This method rebuilds the polling table. The polling table is a list of items that have
	 * polling enabled (ie a refresh interval is set). This list is then checked periodically
	 * and any item that has passed its polling interval will be polled.
	 */
	private void rebuildPollingTable() {
		// Rebuild the polling table
//		pollingList.clear();
		
		if(converterHandler == null) {
			logger.debug("ConverterHandler not initialised. Polling disabled.");
			
			return;
		}
/*
			// Loop all bound items for this provider
			for (String name : eachProvider.getItemNames()) {
				// Find the node and check if it's completed initialisation.
				ZWaveBindingConfig cfg = eachProvider.getZwaveBindingConfig(name);
				ZWaveNode node = controller.getNode(cfg.getNodeId());
				if(node == null) {
					logger.debug("NODE {}: Polling list: can't get node for item {}", cfg.getNodeId(), name);
					continue;
				}
				if(node.getNodeInitializationStage() != ZWaveNodeInitStage.DONE) {
					logger.debug("NODE {}: Polling list: item {} is not completed initialisation", cfg.getNodeId(), name);
					continue;
				}

				logger.trace("Polling list: Checking {} == {}", name, converterHandler.getRefreshInterval(eachProvider, name));

				// If this binding is configured to poll - add it to the list
				if (converterHandler.getRefreshInterval(eachProvider, name) > 0) {
					ZWavePollItem item = new ZWavePollItem();
					item.item = name;
					item.provider = eachProvider;
					pollingList.add(item);
					logger.trace("Polling list added {}", name);
				}
			}

		pollingIterator = null;*/
	}

	@Override
	public void ZWaveIncomingEvent(ZWaveEvent event) {
		// TODO Auto-generated method stub
		
	}

	private class ZWavePollItem {
//		ZWaveBindingProvider provider;
		String item;
	}
}
