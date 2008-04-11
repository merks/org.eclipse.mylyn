/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on 19-Jan-2005
 */
package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorInputFactory;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * Input for task editors.
 * 
 * @author Eric Booth
 * @author Rob Elves
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 2.0
 */
public class TaskEditorInput implements IEditorInput, IPersistableElement {

	private static final int MAX_LABEL_LENGTH = 60;

	private final AbstractTask task;

	private final String taskId;

	private final TaskRepository taskRepository;

	/**
	 * @since 2.0
	 */
	@Deprecated
	public TaskEditorInput(AbstractTask task, boolean newTask) {
		this(TasksUiPlugin.getRepositoryManager().getRepository(task.getConnectorKind(), task.getRepositoryUrl()), task);
	}

	/**
	 * @since 3.0
	 */
	public TaskEditorInput(TaskRepository taskRepository, AbstractTask task) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(task);
		this.taskRepository = taskRepository;
		this.task = task;
		this.taskId = task.getTaskId();
	}

	/**
	 * @since 3.0
	 */
	public TaskEditorInput(TaskRepository taskRepository, String taskId) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(taskId);
		this.taskRepository = taskRepository;
		this.taskId = taskId;
		this.task = null;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TaskEditorInput other = (TaskEditorInput) obj;
		if (task != null) {
			return task.equals(other.task);
		} else {
			return taskRepository.equals(other.taskRepository) && taskId.equals(other.taskId);
		}
	}

	/**
	 * @since 2.0
	 */
	public boolean exists() {
		return task != null;
	}

	/**
	 * @since 2.0
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IEditorInput.class) {
			return this;
		}
		return null;
	}

	/**
	 * @since 2.0
	 */
	public String getFactoryId() {
		return TaskEditorInputFactory.ID_FACTORY;
	}

	/**
	 * @since 2.0
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/**
	 * @deprecated use {@link #getName()}
	 * @since 2.0
	 */
	@Deprecated
	public String getLabel() {
		return getName();
	}

	/**
	 * @since 2.0
	 */
	public String getName() {
		String toolTipText = getToolTipText();
		if (toolTipText == null) {
			return null;
		}

		if (task != null) {
			String taskKey = task.getTaskKey();
			if (taskKey != null) {
				return truncate(taskKey + ": " + toolTipText);
			}
		}
		return truncate(toolTipText);
	}

	/**
	 * @since 2.0
	 */
	public IPersistableElement getPersistable() {
		return this;
	}

	/**
	 * Returns the task if the task is in the task list; returns <code>null</code> otherwise.
	 */
	public AbstractTask getTask() {
		return task;
	}

	/**
	 * @since 3.0
	 */
	public RepositoryTaskData getTaskData() {
		return TasksUiPlugin.getTaskDataManager().getNewTaskData(taskRepository.getRepositoryUrl(), taskId);
	}

	/**
	 * @since 3.0
	 */
	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	/**
	 * @since 2.0
	 */
	public String getToolTipText() {
		if (task != null) {
			return task.getSummary();
		} else {
			RepositoryTaskData taskData = getTaskData();
			if (taskData != null) {
				return taskData.getSummary();
			}
		}
		return null;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public int hashCode() {
		return taskId.hashCode();
	}

	/**
	 * @since 2.0
	 */
	@Deprecated
	public boolean isNewTask() {
		return false;
	}

	/**
	 * @since 2.0
	 */
	public void saveState(IMemento memento) {
		TaskEditorInputFactory.saveState(memento, this);
	}

	private String truncate(String description) {
		if (description == null || description.length() <= MAX_LABEL_LENGTH) {
			return description;
		} else {
			return description.substring(0, MAX_LABEL_LENGTH) + "...";
		}
	}
}
