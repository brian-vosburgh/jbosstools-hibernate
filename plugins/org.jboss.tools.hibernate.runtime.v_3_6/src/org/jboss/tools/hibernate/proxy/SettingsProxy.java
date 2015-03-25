package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.Settings;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;

public class SettingsProxy implements ISettings {
	
	private Settings target;

	public SettingsProxy(
			IFacadeFactory facadeFactory, 
			Settings settings) {
		target = settings;
	}

	public Settings getTarget() {
		return target;
	}

	@Override
	public String getDefaultCatalogName() {
		return target.getDefaultCatalogName();
	}

	@Override
	public String getDefaultSchemaName() {
		return target.getDefaultSchemaName();
	}

}
