/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.debug.ui.messages"; //$NON-NLS-1$

	public static String Breakpoints_closed_projects_warning;

	public static String Breakpoints_locations_warning;

	public static String BreakpointsPreferencePage_bug_link;

	public static String BreakpointsPreferencePage_Known_Issues;

	public static String BreakpointsPreferencePage_Manage_breakpoints;

	public static String BreakpointsPreferencePage_unchecking_will_remove;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
