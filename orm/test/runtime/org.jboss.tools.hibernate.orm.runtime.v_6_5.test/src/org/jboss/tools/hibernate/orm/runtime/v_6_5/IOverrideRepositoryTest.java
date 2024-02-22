package org.jboss.tools.hibernate.orm.runtime.v_6_5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.mapping.Table;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IOverrideRepositoryTest {
	
	private static final String HIBERNATE_REVERSE_ENGINEERING_XML =
			"<?xml version='1.0' encoding='UTF-8'?>                                 "+
			"<!DOCTYPE hibernate-reverse-engineering PUBLIC                         "+
			"      '-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN'         "+
			"      'http://hibernate.org/dtd/hibernate-reverse-engineering-3.0.dtd'>"+
			"<hibernate-reverse-engineering>                                        "+
			"    <table name='FOO'/>                                                "+
			"</hibernate-reverse-engineering>                                       ";

	private IOverrideRepository overrideRepositoryFacade = null; 
	private Object overrideRepository;
	
	@BeforeEach
	public void beforeEach() {
		overrideRepositoryFacade = (IOverrideRepository)GenericFacadeFactory.createFacade(
				IOverrideRepository.class, 
				WrapperFactory.createOverrideRepositoryWrapper());
		overrideRepository = ((IFacade)overrideRepositoryFacade).getTarget();
	}
	
	@Test
	public void testAddFile() throws Exception {
		File file = File.createTempFile("addFile", "tst");
		file.deleteOnExit();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(HIBERNATE_REVERSE_ENGINEERING_XML);
		fileWriter.close();
		overrideRepositoryFacade.addFile(file);
		Field tablesField = overrideRepository.getClass().getDeclaredField("tables");
		tablesField.setAccessible(true);
		Object object = tablesField.get(overrideRepository);
		List<?> tables = (List<?>)object;
		Table table = (Table)tables.get(0);
		assertNotNull(table);
		assertEquals("FOO", table.getName());
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() throws Exception {
		IReverseEngineeringStrategy resFacade = (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				WrapperFactory.createRevengStrategyWrapper());
		Object res = ((IFacade)resFacade).getTarget();
		IReverseEngineeringStrategy result = overrideRepositoryFacade.getReverseEngineeringStrategy(resFacade);
		DelegatingStrategy resultTarget = 
				(DelegatingStrategy)((IFacade)result).getTarget();
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		assertSame(res, delegateField.get(resultTarget));
	}
	
	@Test
	public void testAddTableFilter() throws Exception {
		ITableFilter tableFilterFacade = (ITableFilter)GenericFacadeFactory.createFacade(
				ITableFilter.class, 
				(TableFilter)WrapperFactory.createTableFilterWrapper());
		TableFilter tableFilterTarget = (TableFilter)((IFacade)tableFilterFacade).getTarget();
		Field tableFiltersField = OverrideRepository.class.getDeclaredField("tableFilters");
		tableFiltersField.setAccessible(true);
		List<?> tableFilters = (List<?>)tableFiltersField.get(overrideRepository);
		assertTrue(tableFilters.isEmpty());
		overrideRepositoryFacade.addTableFilter(tableFilterFacade);
		tableFilters = (List<?>)tableFiltersField.get(overrideRepository);
		assertSame(tableFilterTarget, tableFilters.get(0));		
	}
	
}
