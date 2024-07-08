package org.jboss.tools.hibernate.orm.runtime.v_7_0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class IArtifactCollectorTest {
	
	private static final String FOO_XML = "<foo><bar/></foo>";
	
	private Map<String, List<File>> filesMap = null;
	private List<File> xmlFiles = new ArrayList<File>();
	private File fooFile = null;
	private IArtifactCollector facade = null;
	
	@TempDir
	private File tempDir;
	
	@SuppressWarnings("unchecked")
	@BeforeEach
	public void beforeEach() throws Exception {
		facade = (IArtifactCollector)GenericFacadeFactory.createFacade(
				IArtifactCollector.class, 
				WrapperFactory.createArtifactCollectorWrapper());
		Wrapper wrapper = (Wrapper)((IFacade)facade).getTarget();
		Object wrappedObject = wrapper.getWrappedObject();
		Field filesField = wrappedObject.getClass().getDeclaredField("files");
		filesField.setAccessible(true);
		filesMap = (Map<String, List<File>>)filesField.get(wrappedObject);
		tempDir = Files.createTempDirectory("temp").toFile();
		fooFile = new File(tempDir, "foo.xml");
		Files.write(fooFile.toPath(), FOO_XML.getBytes());
		xmlFiles.add(fooFile);
	}
	
	@Test
	public void testGetFileTypes() {
		assertTrue(facade.getFileTypes().isEmpty());
		filesMap.put("xml", xmlFiles);
		filesMap.put("foo", new ArrayList<File>());
		Set<String> fileTypes = facade.getFileTypes();
		assertTrue(fileTypes.size() == 2);
		assertTrue(fileTypes.contains("xml"));
		assertTrue(fileTypes.contains("foo"));
	}
	
	@Test
	public void testGetFiles() {
		assertTrue(facade.getFiles("xml").length == 0);
		filesMap.put("xml", xmlFiles);
		File[] files = facade.getFiles("xml");
		assertTrue(files.length == 1);
		assertSame(fooFile, files[0]);
	}
	
	@Test
	public void testFormatFiles() throws IOException {
		facade.formatFiles();
		assertEquals(FOO_XML, new String(Files.readAllBytes(fooFile.toPath())));
		filesMap.put("xml", xmlFiles);
		facade.formatFiles();
		assertNotEquals("xml", new String(Files.readAllBytes(fooFile.toPath())));
	}

}
