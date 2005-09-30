/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 18, 2005
  */
package org.eclipse.mylar.xml.ant;

import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.AbstractContextLabelProvider;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.xml.MylarXmlPlugin;
import org.eclipse.mylar.xml.XmlReferencesProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class AntContextLabelProvider extends AbstractContextLabelProvider {

	@Override
	protected Image getImage(IMylarContextNode node) {
		return MylarImages.getImage(MylarImages.FILE_XML); 
	}

	@Override
	protected Image getImage(IMylarContextEdge edge) {
		return MylarImages.getImage(MylarXmlPlugin.EDGE_REF_XML);
	}

	@Override
	protected String getText(IMylarContextNode node) {
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(AntStructureBridge.CONTENT_TYPE);
        return bridge.getName(bridge.getObjectForHandle(node.getElementHandle()));
	}

	@Override
	protected String getText(IMylarContextEdge edge) {
		return XmlReferencesProvider.NAME;  
	}

	@Override
	protected Image getImageForObject(Object object) {
		return MylarImages.getImage(MylarImages.FILE_XML); 
	}

	@Override
	protected String getTextForObject(Object object) {
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(AntStructureBridge.CONTENT_TYPE);
        return bridge.getName(object);
	}

} 
