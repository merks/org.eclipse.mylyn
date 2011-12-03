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

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.internal.core.BuildPackage;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.notifications.AbstractNotification;
import org.eclipse.mylyn.internal.builds.ui.view.BuildLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

/**
 * @author Steffen Pingel
 */
public class BuildNotification extends AbstractNotification {

	private static final String ID_EVENT_PLAN_STATUS_CHANGED = "org.eclipse.mylyn.builds.ui.events.PlanStatusChanged"; //$NON-NLS-1$

	private static final String ID_EVENT_BUILD_COMPLETED = "org.eclipse.mylyn.builds.ui.events.BuildCompleted"; //$NON-NLS-1$

	private static final String ID_EVENT_BUILD_STARTED = "org.eclipse.mylyn.builds.ui.events.BuildStarted"; //$NON-NLS-1$

	private final IBuildElement element;

	private String description;

	private String label;

	public BuildNotification(String eventId, IBuildElement element) {
		super(eventId);
		this.element = element;
	}

	public int compareTo(AbstractNotification o) {
		return -1;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes")
	Class adapter) {
		return null;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Image getNotificationImage() {
		if (element instanceof IBuildPlan) {
			return CommonImages.getImage(BuildLabelProvider.getImageDescriptor(((IBuildPlan) element).getStatus()));
		}
		return null;
	}

	@Override
	public Image getNotificationKindImage() {
		return CommonImages.getImage(BuildImages.VIEW_BUILDS);
	}

	@Override
	public void open() {
		BuildsUi.open(element);
	}

	public static BuildNotification createNotification(Notification msg) {
		if (msg.getOldValue() == msg.getNewValue()) {
			return null;
		}

		BuildNotification notification = null;
		if (msg.getNotifier() instanceof IBuildPlan) {
			IBuildPlan plan = (IBuildPlan) msg.getNotifier();
			int featureId = msg.getFeatureID(IBuildPlan.class);
			if (featureId == BuildPackage.BUILD_PLAN__STATUS) {
				notification = new BuildNotification(ID_EVENT_PLAN_STATUS_CHANGED, (IBuildElement) msg.getNotifier());
				notification.setLabel(NLS.bind("Plan {0}: {1}", plan.getLabel(), plan.getStatus().toString()));
				notification.setDescription(plan.getSummary());
//			} else if (featureId == BuildPackage.BUILD_PLAN__STATE && plan.getLastBuild() != null) {
//				if (plan.getState() == BuildState.RUNNING) {
//					notification = new BuildNotification(ID_EVENT_PLAN_STATUS_CHANGED, (IBuildElement) msg
//							.getNotifier());
//					notification.setLabel(NLS.bind("Build {0} started", plan.getLastBuild().getLabel()));
//					notification.setDescription(plan.getLabel());
//				} else {
//					notification = new BuildNotification(ID_EVENT_BUILD_COMPLETED, (IBuildElement) msg.getNotifier());
//					notification.setLabel(NLS.bind("Build {0} completed", plan.getLastBuild().getLabel()));
//					notification.setDescription(plan.getLabel());
//				}
//			} else if (featureId == BuildPackage.BUILD_PLAN__LAST_BUILD && plan.getLastBuild() != null) {
//				notification = new BuildNotification(ID_EVENT_BUILD_COMPLETED, (IBuildElement) msg.getNotifier());
//				notification.setLabel(NLS.bind("Build {0} completed", plan.getLastBuild().getLabel()));
//				notification.setDescription(plan.getLabel());
			}
		}
		return notification;
	}

}
