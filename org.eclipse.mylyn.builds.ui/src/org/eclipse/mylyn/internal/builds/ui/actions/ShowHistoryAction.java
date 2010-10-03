/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class ShowHistoryAction extends BaseSelectionListenerAction {

	public ShowHistoryAction() {
		super("Show History");
		setToolTipText("Show Plan in History View");
		setImageDescriptor(BuildImages.VIEW_HISTORY);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection.getFirstElement() instanceof IBuildPlan || selection.getFirstElement() instanceof IBuild) {
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof IBuildPlan) {
			IBuildPlan plan = (IBuildPlan) selection;
			TeamUI.showHistoryFor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), selection, null);
		} else if (selection instanceof IBuild) {
			IBuild build = (IBuild) selection;
			TeamUI.showHistoryFor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), selection, null);
		}
	}

}
