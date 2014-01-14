/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.spi.ModelUtil;

/**
 * Class to represent a comment in a review
 * 
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class CommentAnnotation extends Annotation {

	public static final String COMMENT_ANNOTATION_ID = "org.eclipse.mylyn.reviews.ui.comment.Annotation"; //$NON-NLS-1$

	private final Position position;

	private final IComment comment;

	public CommentAnnotation(int offset, int length, IComment comment) {
		super(COMMENT_ANNOTATION_ID, false, null);
		position = new Position(offset, length);
		this.comment = comment;
	}

	public Position getPosition() {
		return position;
	}

	@Override
	public String getText() {
		return comment.getAuthor().getDisplayName() + " - " + comment.getTitle();
	}

	public IComment getComment() {
		return comment;
	}

	@Override
	public int hashCode() {
		int result = ((position == null) ? 0 : position.hashCode());
		return ModelUtil.ecoreHash(result, comment);
	}

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
		CommentAnnotation other = (CommentAnnotation) obj;
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		if (comment != null && other.comment != null && comment.getId() != null && other.comment.getId() != null) {
			return comment.getId().equals(other.comment.getId());
		}
		return EcoreUtil.equals(comment, other.comment);
	}
}
