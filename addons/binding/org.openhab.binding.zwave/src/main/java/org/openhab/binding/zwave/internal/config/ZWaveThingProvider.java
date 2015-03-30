package org.openhab.binding.zwave.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ThingTypeProvider;
import org.eclipse.smarthome.core.thing.type.ThingType;
import org.openhab.binding.zwave.ZWaveBindingConstants;

public class ZWaveThingProvider implements ThingTypeProvider{
	Collection<ThingType> things = null;
	
	@Override
	public Collection<ThingType> getThingTypes(Locale locale) {
		// If we've already generated the list, just return it.
		if(things != null) {
			return things;
		}

		Collection<ThingType> things = new ArrayList<ThingType>();
		ZWaveProductDatabase database = new ZWaveProductDatabase();

		// Loop over the manufacturers
		for (ZWaveDbManufacturer manufacturer : database.GetManufacturers()) {
			if (database.FindManufacturer(manufacturer.Id) == false) {
				break;
			}

			// Loop over the products
			for (ZWaveDbProduct product : database.GetProducts()) {
				ThingType thing = new ThingType(
						ZWaveBindingConstants.BINDING_ID, 
						manufacturer.Id.toString() + "_" + product.Reference.get(0).Type + "_" + product.Reference.get(0).Id,
						database.getLabel(product.Label)
				);

				
//				ThingType thing = new ThingType(
//						new ThingTypeUID(ZWaveBindingConstants.BINDING_ID, manufacturer.Id.toString() + "_" + product.Reference.get(0).Type + "_" + product.Reference.get(0).Id),
//						ZWaveBindingConstants.SUPPORTED_BRIDGE_TYPES_UIDS,
//						manufacturer.Name + " :: " + product.Model + " " + database.getLabel(product.Label),
//						null,			//database.getLabel(product.Description),
//						null,			// channelDefinitions
//						null,			// channelGroupDefinitions
//						null,			// properties
//						null			//configDescriptionURI
//						);


				things.add(thing);
			}
		}

		return things;
	}

	@Override
	public ThingType getThingType(ThingTypeUID thingTypeUID, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

}
