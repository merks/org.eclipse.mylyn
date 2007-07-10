/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

/**
 * @author Steffen Pingel
 */
public class TracTicketStatus extends TracTicketAttribute {

	private static final long serialVersionUID = -8844909853931772506L;

	public TracTicketStatus(String name, int value) {
		super(name, value);
	}

}
