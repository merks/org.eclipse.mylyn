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

package org.eclipse.mylar.tasks.tests.connector;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class MockRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_KIND = "mock";

	public static final String REPOSITORY_URL = "http://mockrepository.test";

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		// ignore
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		// ignore
		return false;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		// ignore
		return null;
	}

	@Override
	public String getLabel() {
		return "Mock Repository (for unit tests)";
	}

	@Override
	public ITaskDataHandler getTaskDataHandler() {
		// ignore
		return new ITaskDataHandler() {

			public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind,
					String taskKind) {
				// we don't care about the repository information right now
				return new MockAttributeFactory();
			}

			public RepositoryTaskData getTaskData(TaskRepository repository, String taskId) throws CoreException {
				// ignore
				return null;
			}

			public String postTaskData(TaskRepository repository, RepositoryTaskData taskData) throws CoreException {
				// ignore
				return null;
			}

			public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data,
					IProgressMonitor monitor) throws CoreException {
				// ignore
				return false;
			}

			public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
				// ignore
				return null;
			}

		};
	}

	@Override
	public String getRepositoryType() {
		return REPOSITORY_KIND;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		// ignore
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		// ignore
		return null;
	}

	@Override
	public String getTaskWebUrl(String repositoryUrl, String taskId) {
		return null;
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore
	}

	@Override
	public void updateTaskFromRepository(TaskRepository repository, AbstractRepositoryTask repositoryTask) {
		// ignore
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository, IProgressMonitor monitor,
			QueryHitCollector resultCollector) {
		return null;
	}

	@Override
	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws CoreException {
		return Collections.emptySet();
	}

	@Override
	protected AbstractRepositoryTask makeTask(String repositoryUrl, String id, String summary) {
		// ignore
		return null;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository, AbstractRepositoryTask repositoryTask,
			RepositoryTaskData taskData, boolean retrieveSubTasks) {
		// ignore

	}

}
