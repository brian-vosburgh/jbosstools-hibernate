package org.hibernate.eclipse.jdt.ui.test.hbmexporter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AllEntitiesProcessor;
import org.hibernate.eclipse.jdt.ui.test.HibernateJDTuiTestPlugin;
import org.hibernate.eclipse.jdt.ui.wizards.ConfigurationActor;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class HbmExporterTest extends TestCase {
	
	public static final String PROJECT_NAME = "TestProject"; //$NON-NLS-1$
	public static final String RESOURCE_PATH = "res/hbm/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String TESTRESOURCE_PATH = "testresources"; //$NON-NLS-1$

	protected AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
	protected AllEntitiesProcessor processor = new AllEntitiesProcessor();

	protected TestProject project = null;
	
	protected void setUp() throws Exception {
		try {
			createTestProject();
		} catch (JavaModelException e1) {
			fail(e1.getMessage());
		} catch (CoreException e1) {
			fail(e1.getMessage());
		} catch (IOException e1) {
			fail(e1.getMessage());
		}
		assertNotNull(project);
		IJavaProject javaProject = ProjectUtils.findJavaProject(PROJECT_NAME);
		assertNotNull(javaProject);
		try {
			javaProject.getProject().open(null);
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Method returns Configuration object for selected ICompilationUnits.
	 * Fails if configuration is null.
	 * @return
	 */
	protected IConfiguration getConfigurationFor(String... cuNames){
		Set<ICompilationUnit> selectionCU = new HashSet<ICompilationUnit>();
		for (int i = 0; i < cuNames.length; i++) {
			ICompilationUnit icu = Utils.findCompilationUnit(project.getIJavaProject(),
					cuNames[i]);
			assertNotNull(icu);
			selectionCU.add(icu);
		}
		ConfigurationActor actor = new ConfigurationActor(selectionCU);
		Map<IJavaProject, IConfiguration> configurations = actor.createConfigurations(Integer.MAX_VALUE);
		assertEquals(1, configurations.size());
		IConfiguration config = configurations.get(project.getIJavaProject());
		assertNotNull(config);
		return config;
	}
	
	protected void checkClassesMaped(IConfiguration config, String... classesNames){
		for (int i = 0; i < classesNames.length; i++) {
			assertNotNull(config.getClassMapping(classesNames[i]));
		}
	}
	
	public void testId(){
		IConfiguration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "pack.A", "pack.B"); //$NON-NLS-1$ //$NON-NLS-2$
		IPersistentClass a = config.getClassMapping("pack.A"); //$NON-NLS-1$
		IPersistentClass b = config.getClassMapping("pack.B"); //$NON-NLS-1$
		
		IProperty aId= a.getIdentifierProperty();
		IProperty bId= b.getIdentifierProperty();
		assertNotNull(aId);
		assertNotNull(bId);
		assertEquals("id", aId.getName()); //$NON-NLS-1$
		assertEquals("id", bId.getName()); //$NON-NLS-1$
	}
	
	public void testProperty(){
		IConfiguration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "pack.A", "pack.B"); //$NON-NLS-1$ //$NON-NLS-2$
		IPersistentClass a = config.getClassMapping("pack.A"); //$NON-NLS-1$
		
		IProperty prop = a.getProperty("prop"); //$NON-NLS-1$
		assertNotNull(prop.getValue());
		IValue value = prop.getValue();
		assertTrue("Expected to get ManyToOne-type mapping", value.isManyToOne()); //$NON-NLS-1$
		assertEquals("pack.B", value.getTypeName()); //$NON-NLS-1$
	}
	
	public void testArray(){
		IConfiguration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "pack.A", "pack.B"); //$NON-NLS-1$ //$NON-NLS-2$
		IPersistentClass a = config.getClassMapping("pack.A"); //$NON-NLS-1$
		IPersistentClass b = config.getClassMapping("pack.B"); //$NON-NLS-1$
		
		IProperty bs = a.getProperty("bs"); //$NON-NLS-1$
		assertNotNull(bs.getValue());
		IValue value = bs.getValue();
		assertTrue("Expected to get Array-type mapping", value.isArray());
		assertEquals("pack.B", value.getElementClassName()); //$NON-NLS-1$
		assertTrue("Expected to get one-to-many array's element type", //$NON-NLS-1$
				value.getCollectionElement().isOneToMany());
		
		IProperty testIntArray = b.getProperty("testIntArray"); //$NON-NLS-1$
		assertNotNull(testIntArray);
		value = testIntArray.getValue();
		assertNotNull(value);
		assertTrue("Expected to get PrimitiveArray-type mapping", //$NON-NLS-1$  
				value.isPrimitiveArray());
		assertNotNull(value.getCollectionElement());
		assertTrue("Expected to get int-type primitive array", value.getCollectionElement().getType().isIntegerType()); //$NON-NLS-1$
	}
	
	public void testList(){
		IConfiguration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "pack.A", "pack.B"); //$NON-NLS-1$ //$NON-NLS-2$
		IPersistentClass a = config.getClassMapping("pack.A"); //$NON-NLS-1$
		IPersistentClass b = config.getClassMapping("pack.B"); //$NON-NLS-1$
		
		IProperty listProp = a.getProperty("list"); //$NON-NLS-1$
		assertNotNull(listProp.getValue());
		IValue value = listProp.getValue();
		assertTrue("Expected to get List-type mapping", value.isList());
		assertTrue(value.getCollectionElement().isOneToMany());
		assertTrue(value.getCollectionTable().equals(b.getTable()));
		assertNotNull(value.getIndex());
		assertNotNull(value.getKey());
	}
	
	public void testSet(){
		IConfiguration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "pack.A", "pack.B"); //$NON-NLS-1$ //$NON-NLS-2$
		IPersistentClass a = config.getClassMapping("pack.A"); //$NON-NLS-1$
		IPersistentClass b = config.getClassMapping("pack.B"); //$NON-NLS-1$
		IProperty setProp = a.getProperty("set"); //$NON-NLS-1$
		assertNotNull(setProp.getValue());
		IValue value = setProp.getValue();
		assertTrue("Expected to get Set-type mapping", value.isSet());
		assertTrue(value.getCollectionElement().isOneToMany());
		assertTrue(value.getCollectionTable().equals(b.getTable()));
		assertNotNull(value.getKey());
	}
	
	public void testMap(){
		IConfiguration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "pack.A", "pack.B"); //$NON-NLS-1$ //$NON-NLS-2$
		IPersistentClass a = config.getClassMapping("pack.A"); //$NON-NLS-1$
		IPersistentClass b = config.getClassMapping("pack.B"); //$NON-NLS-1$		
		IProperty mapValue = a.getProperty("mapValue"); //$NON-NLS-1$
		assertNotNull(mapValue.getValue());
		IValue value = mapValue.getValue();
		assertTrue("Expected to get Map-type mapping", value.isMap());
		assertTrue(value.getCollectionElement().isOneToMany());
		assertTrue(value.getCollectionTable().equals(b.getTable()));
		assertNotNull(value.getKey());
		assertEquals("string", value.getKey().getType().getName()); //$NON-NLS-1$
	}
	

	protected void createTestProject() throws JavaModelException,
			CoreException, IOException {
		project = new TestProject(PROJECT_NAME);
		File resourceFolder = getResourceItem(RESOURCE_PATH);
		if (!resourceFolder.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		IPackageFragmentRoot sourceFolder = project.createSourceFolder();
		FilesTransfer.copyFolder(resourceFolder, (IFolder) sourceFolder
				.getResource());
		File resourceFolderLib = getResourceItem(TESTRESOURCE_PATH);
		if (!resourceFolderLib.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		List<IPath> libs = project.copyLibs2(resourceFolderLib.getAbsolutePath());
		project.generateClassPath(libs, sourceFolder);
	}
	
	protected File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFolder = resourcePath.toFile();
		URL entry = HibernateJDTuiTestPlugin.getDefault().getBundle().getEntry(
				strResPath);
		URL resProject = FileLocator.resolve(entry);
		String tplPrjLcStr = FileLocator.resolve(resProject).getFile();
		resourceFolder = new File(tplPrjLcStr);
		return resourceFolder;
	}
	
	protected void tearDown() throws Exception {
		assertNotNull(project);
		project.deleteIProject();
		project = null;
	}

}
