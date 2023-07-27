package org.jboss.tools.hibernate.orm.runtime.v_6_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.Property;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IHQLCompletionProposalTest {
	
	private IHQLCompletionProposal hqlCompletionProposalFacade = null;
	private HQLCompletionProposal hqlCompletionProposalTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		hqlCompletionProposalTarget = new HQLCompletionProposal(HQLCompletionProposal.PROPERTY, Integer.MAX_VALUE);
		hqlCompletionProposalFacade = (IHQLCompletionProposal)GenericFacadeFactory.createFacade(
				IHQLCompletionProposal.class, 
				WrapperFactory.createHqlCompletionProposalWrapper(hqlCompletionProposalTarget));;
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hqlCompletionProposalTarget);
		assertNotNull(hqlCompletionProposalFacade);
		Object hqlCompletionProposalWrapper = ((IFacade)hqlCompletionProposalFacade).getTarget();
		assertTrue(hqlCompletionProposalWrapper instanceof Wrapper);
		assertSame(hqlCompletionProposalTarget, ((Wrapper)hqlCompletionProposalWrapper).getWrappedObject());
	}

	@Test
	public void testGetCompletion() {
		assertNotEquals("foo", hqlCompletionProposalFacade.getCompletion());
		hqlCompletionProposalTarget.setCompletion("foo");
		assertEquals("foo", hqlCompletionProposalFacade.getCompletion());
	}
	
	@Test
	public void testGetReplaceStart() {
		assertNotEquals(Integer.MAX_VALUE, hqlCompletionProposalFacade.getReplaceStart());
		hqlCompletionProposalTarget.setReplaceStart(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, hqlCompletionProposalFacade.getReplaceStart());
	}
	
	@Test
	public void testGetReplaceEnd() {
		assertNotEquals(Integer.MIN_VALUE, hqlCompletionProposalFacade.getReplaceEnd());
		hqlCompletionProposalTarget.setReplaceEnd(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, hqlCompletionProposalFacade.getReplaceEnd());
	}
	
	@Test
	public void testGetSimpleName() {
		assertNotEquals("foo", hqlCompletionProposalFacade.getSimpleName());
		hqlCompletionProposalTarget.setSimpleName("foo");
		assertEquals("foo", hqlCompletionProposalFacade.getSimpleName());
	}
	
	@Test
	public void testGetCompletionKind() {
		assertEquals(HQLCompletionProposal.PROPERTY, hqlCompletionProposalFacade.getCompletionKind());
		hqlCompletionProposalTarget.setCompletionKind(HQLCompletionProposal.KEYWORD);
		assertEquals(HQLCompletionProposal.KEYWORD, hqlCompletionProposalFacade.getCompletionKind());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("foo", hqlCompletionProposalFacade.getEntityName());
		hqlCompletionProposalTarget.setEntityName("foo");
		assertEquals("foo", hqlCompletionProposalFacade.getEntityName());
	}
	
	@Test
	public void testGetShortEntityName() {
		assertNotEquals("foo", hqlCompletionProposalFacade.getShortEntityName());
		hqlCompletionProposalTarget.setShortEntityName("foo");
		assertEquals("foo", hqlCompletionProposalFacade.getShortEntityName());
	}
	
	@Test
	public void testGetProperty() {
		Property propertyTarget = new Property();
		assertNull(hqlCompletionProposalFacade.getProperty());
		hqlCompletionProposalTarget.setProperty(propertyTarget);
		assertSame(propertyTarget, ((IFacade)hqlCompletionProposalFacade.getProperty()).getTarget());
	}
	
	@Test
	public void testAliasRefKind() {
		assertSame(HQLCompletionProposal.ALIAS_REF, hqlCompletionProposalFacade.aliasRefKind());
	}
	
	@Test
	public void testEntityNameKind() {
		assertSame(HQLCompletionProposal.ENTITY_NAME, hqlCompletionProposalFacade.entityNameKind());
	}
	
	@Test
	public void testPropertyKind() {
		assertSame(HQLCompletionProposal.PROPERTY, hqlCompletionProposalFacade.propertyKind());
	}
	
	@Test
	public void testKeywordKind() {
		assertSame(HQLCompletionProposal.KEYWORD, hqlCompletionProposalFacade.keywordKind());
	}
	
	@Test
	public void testFunctionKind() {
		assertSame(HQLCompletionProposal.FUNCTION, hqlCompletionProposalFacade.functionKind());
	}
	
}
