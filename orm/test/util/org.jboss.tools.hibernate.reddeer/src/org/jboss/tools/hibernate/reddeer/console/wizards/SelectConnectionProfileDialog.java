/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.console.wizards;

import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.CancelButton;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;

public class SelectConnectionProfileDialog extends DefaultShell{
	
	
	public SelectConnectionProfileDialog() {
		super("Select Connection Profile");
	}
	
	public void setProfileName(String profileName){
		new DefaultCombo(this).setSelection(profileName);
	}
	
	public void ok(){
		new OkButton(this).click();
		new WaitWhile(new ShellIsAvailable(this));
		new DefaultShell("");
	}
	
	public void cancel(){
		new CancelButton(this).click();
		new WaitWhile(new ShellIsAvailable(this));
		new DefaultShell("");
	}

}
