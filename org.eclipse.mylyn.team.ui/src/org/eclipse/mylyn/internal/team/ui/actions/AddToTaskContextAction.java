/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.team.ui.LinkedTaskInfo;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.resources.ui.ResourcesUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
import org.eclipse.team.internal.core.subscribers.ChangeSet;
import org.eclipse.team.internal.core.subscribers.DiffChangeSet;
import org.eclipse.team.internal.ui.synchronize.SynchronizeModelElement;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ObjectPluginAction;

/**
 * @author Mik Kersten
 */
public class AddToTaskContextAction extends Action implements IViewActionDelegate {

	private ISelection selection;

	public AddToTaskContextAction() {
		setText(Messages.AddToTaskContextAction_Add_to_Task_Context);
		setToolTipText(Messages.AddToTaskContextAction_Add_to_Task_Context);
		setImageDescriptor(TasksUiImages.CONTEXT_ADD);
	}

	public void init(IViewPart view) {
	}

	@Override
	public void run() {
		if (selection instanceof StructuredSelection) {
			run((StructuredSelection) selection);
		}
	}

	public void run(IAction action) {
		if (action instanceof ObjectPluginAction) {
			ObjectPluginAction objectAction = (ObjectPluginAction) action;
			if (objectAction.getSelection() instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) objectAction.getSelection();
				run(selection);
			}
		}
	}

	private void run(StructuredSelection selection) {
		if (!ContextCore.getContextManager().isContextActive()) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.AddToTaskContextAction_Add_to_Task_Context,
					Messages.AddToTaskContextAction_ACTIVATE_TASK_TO_ADD_RESOURCES);
		}

		Object element = selection.getFirstElement();
		IResource[] resources = null;

		if (element instanceof ActiveChangeSet) {
			resources = ((ActiveChangeSet) element).getResources();
		} else if (element instanceof DiffChangeSet) {
			resources = ((DiffChangeSet) element).getResources();
		} else if (element instanceof LinkedTaskInfo) {
			LinkedTaskInfo linkedTaskInfo = (LinkedTaskInfo) element;
			ChangeSet changeSet = linkedTaskInfo.getChangeSet();
			resources = changeSet.getResources();
		}

		Set<IResource> resourcesToAdd = new HashSet<IResource>();
		if (resources != null) {
			resourcesToAdd.addAll(Arrays.asList(resources));
		} else {
			for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
				Object object = iterator.next();
				if (object instanceof IResource) {
					resourcesToAdd.add((IResource) object);
				} else if (object instanceof SynchronizeModelElement) {
					resourcesToAdd.add(((SynchronizeModelElement) object).getResource());
				} else if (object instanceof IAdaptable) {
					Object adapted = ((IAdaptable) object).getAdapter(IResource.class);
					if (adapted != null) {
						resourcesToAdd.add((IResource) adapted);
					}
				}
			}
		}

		if (!resourcesToAdd.isEmpty()) {
			ResourcesUi.addResourceToContext(resourcesToAdd, InteractionEvent.Kind.SELECTION);
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.AddToTaskContextAction_Add_to_Task_Context,
					Messages.AddToTaskContextAction_No_resources_to_add);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}
