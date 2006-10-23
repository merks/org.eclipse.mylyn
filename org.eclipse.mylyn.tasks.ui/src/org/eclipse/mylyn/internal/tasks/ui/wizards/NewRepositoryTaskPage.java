/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryFilter;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class NewRepositoryTaskPage extends SelectRepositoryPage {

	public NewRepositoryTaskPage(TaskRepositoryFilter taskRepositoryFilter) {
		super(taskRepositoryFilter);
	}

	@Override
	protected IWizard createWizard(TaskRepository taskRepository) {
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(
				taskRepository.getKind());
		return connectorUi.getNewTaskWizard(taskRepository);  // TODO remove unused parameter
	}

}