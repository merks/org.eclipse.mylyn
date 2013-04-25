/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.internal.gerrit.core.GerritOperationFactory;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.SaveDraftRequest;
import org.eclipse.mylyn.internal.gerrit.ui.egit.GitFileRevisionUtils;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.team.core.history.IFileRevision;

import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchLineComment;

/**
 * @author Steffen Pingel
 */
public class GerritReviewBehavior extends ReviewBehavior {

	private Repository repository = null;

	public GerritReviewBehavior(ITask task) {
		super(task);
	}

	public GerritReviewBehavior(ITask task, Repository repository) {
		super(task);
		this.repository = repository;
	}

	public GerritOperationFactory getOperationFactory() {
		return GerritUiPlugin.getDefault().getOperationFactory();
	}

	@Override
	public IStatus addComment(IReviewItem item, IComment comment, IProgressMonitor monitor) {
		short side = 1;
		String id = item.getId();
		if (id.startsWith("base-")) {
			// base revision
			id = id.substring(5);
			side = 0;
		}
		Patch.Key key = Patch.Key.parse(id);
		for (ILocation location : comment.getLocations()) {
			if (location instanceof ILineLocation) {
				ILineLocation lineLocation = (ILineLocation) location;
				SaveDraftRequest request = new SaveDraftRequest(key, lineLocation.getRangeMin(), side);
				request.setMessage(comment.getDescription());

				GerritOperation<PatchLineComment> operation = getOperationFactory().createSaveDraftOperation(getTask(),
						request);
				return operation.run(monitor);
			}
		}
		//We'll only get here if there is something really broken in calling code or model. Gerrit has one and only one comment per location.
		throw new RuntimeException("Internal Exception. No line location for comment. Comment: " + comment.getId());
	}

	@Override
	public IFileRevision getFileRevision(IFileVersion reviewFileVersion) {
		if (repository != null) {
			return GitFileRevisionUtils.getFileRevision(repository, reviewFileVersion);
		}
		return null;
	}

}
