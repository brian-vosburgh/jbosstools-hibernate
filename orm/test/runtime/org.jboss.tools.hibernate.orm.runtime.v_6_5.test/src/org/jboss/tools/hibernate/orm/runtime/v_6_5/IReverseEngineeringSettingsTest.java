package org.jboss.tools.hibernate.orm.runtime.v_6_5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IReverseEngineeringSettingsTest {

	private RevengSettings revengSettingsTarget = null;
	private IReverseEngineeringSettings revengSettingsFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		IReverseEngineeringStrategy revengStrategyFacade = 
				(IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
						IReverseEngineeringStrategy.class, 
						WrapperFactory.createRevengStrategyWrapper());
		revengSettingsFacade = (IReverseEngineeringSettings)GenericFacadeFactory.createFacade(
				IReverseEngineeringSettings.class, 
				WrapperFactory.createRevengSettingsWrapper(((IFacade)revengStrategyFacade).getTarget()));
		revengSettingsTarget = (RevengSettings)((IFacade)revengSettingsFacade).getTarget();	
	}
	
	@Test
	public void testSetDefaultPackageName() {
		assertEquals("", revengSettingsTarget.getDefaultPackageName());
		revengSettingsFacade.setDefaultPackageName("foo");
		assertEquals("foo", revengSettingsTarget.getDefaultPackageName());
	}
	
	@Test
	public void testSetDetectManyToMany() {
		assertTrue(revengSettingsTarget.getDetectManyToMany());
		revengSettingsFacade.setDetectManyToMany(false);
		assertFalse(revengSettingsTarget.getDetectManyToMany());
	}
	
	@Test
	public void testSetDetectOneToOne() {
		assertTrue(revengSettingsTarget.getDetectOneToOne());
		revengSettingsFacade.setDetectOneToOne(false);
		assertFalse(revengSettingsTarget.getDetectOneToOne());
	}
	
	@Test
	public void testSetDetectOptimisticLock() {
		assertTrue(revengSettingsTarget.getDetectOptimsticLock());
		revengSettingsFacade.setDetectOptimisticLock(false);
		assertFalse(revengSettingsTarget.getDetectOptimsticLock());
	}
	
}
