/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habmin.internal.services.chart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.io.rest.RESTResource;
import org.eclipse.smarthome.ui.items.ItemUIRegistry;
import org.openhab.ui.habmin.HABminConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * <p>
 * This class acts as a REST resource for history data and provides different
 * methods to interact with the, persistence store
 * 
 * <p>
 * The typical content types are plain text for status values and XML or JSON(P)
 * for more complex data structures
 * </p>
 * 
 * <p>
 * This resource is registered with the Jersey servlet.
 * </p>
 * 
 * @author Chris Jackson
 * @since 1.3.0
 */
@Path(ChartResource.PATH)
public class ChartResource implements RESTResource {

	private static String CHART_FILE = "charts.xml";

	private static final Logger logger = LoggerFactory.getLogger(ChartResource.class);

	/** The URI path to this resource */
	public static final String PATH = "habmin/charts";

	@Context
	UriInfo uriInfo;
	
	static private ItemUIRegistry itemUIRegistry;

	public void setItemUIRegistry(ItemUIRegistry itemUIRegistry) {
		ChartResource.itemUIRegistry = itemUIRegistry;
	}

	public void unsetItemUIRegistry(ItemRegistry itemUIRegistry) {
		ChartResource.itemUIRegistry = null;
	}

	static public ItemUIRegistry getItemUIRegistry() {
		return itemUIRegistry;
	}

	@GET
	@Path("/")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response httpGetPersistenceCharts(@Context HttpHeaders headers,
			@QueryParam("jsoncallback") @DefaultValue("callback") String callback) {
		logger.trace("Received HTTP GET request at '{}'.", uriInfo.getPath());

		Object responseObject = getPersistenceChartList();
		return Response.ok(responseObject).build();
	}

	@POST
	@Path("/")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response httpPostPersistenceCharts(@Context HttpHeaders headers,
			@QueryParam("jsoncallback") @DefaultValue("callback") String callback, ChartConfigBean chart) {
		logger.trace("Received HTTP POST request at '{}'.", uriInfo.getPath());

		Object responseObject = putChartBean(0, chart);
		return Response.ok(responseObject).build();
	}

	@PUT
	@Path("/{chartid: [a-zA-Z_0-9]*}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response httpPutPersistenceCharts(@Context HttpHeaders headers,
			@QueryParam("jsoncallback") @DefaultValue("callback") String callback,
			@PathParam("chartid") Integer chartId, ChartConfigBean chart) {
		logger.trace("Received HTTP PUT request at '{}'.", uriInfo.getPath());

		Object responseObject = putChartBean(chartId, chart);
		return Response.ok(responseObject).build();
	}

	@DELETE
	@Path("/{chartid: [a-zA-Z_0-9]*}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response httpDeletePersistenceCharts(@Context HttpHeaders headers, @QueryParam("type") String type,
			@QueryParam("jsoncallback") @DefaultValue("callback") String callback, @PathParam("chartid") Integer chartId) {
		logger.trace("Received HTTP DELETE request at '{}'.", uriInfo.getPath());

		Object responseObject = deleteChart(chartId);
		return Response.ok(responseObject).build();
	}

	@GET
	@Path("/{chartid: [a-zA-Z_0-9]*}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response httpGetPersistenceCharts(@Context HttpHeaders headers,
			@QueryParam("jsoncallback") @DefaultValue("callback") String callback, @PathParam("chartid") Integer chartId) {
		logger.trace("Received HTTP GET request at '{}'.", uriInfo.getPath());

		Object responseObject = getChart(chartId);
		return Response.ok(responseObject).build();
	}

	static public org.eclipse.smarthome.core.items.Item getItem(String itemname) {
		ItemUIRegistry registry = getItemUIRegistry();
		if (registry != null) {
			try {
				org.eclipse.smarthome.core.items.Item item;
				item = registry.getItem(itemname);

				return item;
			} catch (org.eclipse.smarthome.core.items.ItemNotFoundException e) {
				logger.debug(e.getMessage());
			}
		}
		return null;
	}

	private ChartConfigBean putChartBean(Integer chartRef, ChartConfigBean bean) {
		if (chartRef == 0) {
			bean.id = null;
		} else {
			bean.id = chartRef;
		}

		// Load the existing list
		ChartListBean list = loadCharts();

		int high = 0;

		ChartConfigBean foundChart = null;
		// Loop through the interface list
		for (ChartConfigBean i : list.entries) {
			if (i.id > high)
				high = i.id;
			if (i.id.intValue() == chartRef) {
				// If it was found in the list, remember it...
				foundChart = i;
			}
		}

		// If it was found in the list, remove it...
		if (foundChart != null) {
			list.entries.remove(foundChart);
		}

		// Set defaults if this is a new chart
		if (bean.id == null) {
			bean.id = high + 1;
		}

		// Now save the updated version
		list.entries.add(bean);
		saveCharts(list);

		return bean;
	}

	private ChartListBean getPersistenceChartList() {
		ChartListBean charts = loadCharts();
		ChartListBean newList = new ChartListBean();

		// We only want to return the id and name
		for (ChartConfigBean i : charts.entries) {
			ChartConfigBean newChart = new ChartConfigBean();
			newChart.id = i.id;
			newChart.name = i.name;
			newChart.icon = i.icon;

			newList.entries.add(newChart);
		}

		return newList;
	}

	private ChartConfigBean getChart(Integer chartRef) {
		ChartListBean charts = loadCharts();

		for (ChartConfigBean i : charts.entries) {
			if (i.id.intValue() == chartRef)
				return i;
		}

		return null;
	}

	private ChartListBean deleteChart(Integer chartRef) {
		ChartListBean charts = loadCharts();

		ChartConfigBean foundChart = null;
		for (ChartConfigBean i : charts.entries) {
			if (i.id.intValue() == chartRef) {
				// If it was found in the list, remember it...
				foundChart = i;
				break;
			}
		}

		// If it was found in the list, remove it...
		if (foundChart != null) {
			charts.entries.remove(foundChart);
		}

		saveCharts(charts);

		return getPersistenceChartList();
	}

	private boolean saveCharts(ChartListBean chart) {
		File folder = new File(HABminConstants.getDataDirectory());
		// create path for serialization.
		if (!folder.exists()) {
			logger.debug("Creating directory {}", HABminConstants.getDataDirectory());
			folder.mkdirs();
		}

		try {
			long timerStart = System.currentTimeMillis();
			
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(HABminConstants.getDataDirectory() + CHART_FILE),"UTF-8"));

			XStream xstream = new XStream(new StaxDriver());
			xstream.alias("charts", ChartListBean.class);
			xstream.alias("chart", ChartConfigBean.class);
			xstream.alias("item", ChartItemConfigBean.class);
			xstream.alias("axis", ChartAxisConfigBean.class);
			xstream.processAnnotations(ChartListBean.class);

			xstream.toXML(chart, out);

			out.close();

			long timerStop = System.currentTimeMillis();
			logger.debug("Chart list saved in {}ms.", timerStop - timerStart);
		} catch (FileNotFoundException e) {
			logger.debug("Unable to open Chart list for SAVE - ", e);

			return false;
		} catch (IOException e) {
			logger.debug("Unable to write Chart list for SAVE - ", e);

			return false;
		}

		return true;
	}

	private ChartListBean loadCharts() {
		ChartListBean charts = null;

		FileInputStream fin;
		try {
			long timerStart = System.currentTimeMillis();

			fin = new FileInputStream(HABminConstants.getDataDirectory() + CHART_FILE);

			XStream xstream = new XStream(new StaxDriver());
			xstream.alias("charts", ChartListBean.class);
			xstream.alias("chart", ChartConfigBean.class);
			xstream.alias("item", ChartItemConfigBean.class);
			xstream.alias("axis", ChartAxisConfigBean.class);
			xstream.processAnnotations(ChartListBean.class);

			charts = (ChartListBean) xstream.fromXML(fin);

			fin.close();

			long timerStop = System.currentTimeMillis();
			logger.debug("Charts loaded in {}ms.", timerStop - timerStart);

		} catch (FileNotFoundException e) {
			charts = new ChartListBean();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return charts;
	}
}
