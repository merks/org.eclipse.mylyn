/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class BuildDetailsPage extends BuildEditorPage {

	public static final String BUILD_EDITOR_PAGE_ID = "org.eclipse.mylyn.build.ui.editor.DetailsPage";

	private Form form;

	private List<AbstractBuildEditorPart> parts;

	private FormToolkit toolkit;

	public BuildDetailsPage(FormEditor editor, String title) {
		super(editor, BUILD_EDITOR_PAGE_ID, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		form = managedForm.getForm().getForm();
		toolkit = managedForm.getToolkit();

		// the outer body uses a GridLayout to support filling the page vertically
		Composite body = form.getBody();
		body.setLayout(GridLayoutFactory.fillDefaults().create());
		body.setBackgroundMode(SWT.INHERIT_FORCE);

		// last part grabs excess vertical space 
		boolean fillBottomPart = parts.size() > 0 && parts.get(parts.size() - 1).span == 2
				&& parts.get(parts.size() - 1).expandVertically;

		if (parts.size() > 1) {
			// the top composite uses a TableWrapLayout for performance and proper support for wrapping text
			Composite bodyTop = new Composite(body, SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(bodyTop);

			TableWrapLayout layout = new TableWrapLayout();
			layout.numColumns = 2;
			bodyTop.setLayout(layout);

			for (AbstractBuildEditorPart part : parts.subList(0, (fillBottomPart) ? parts.size() - 1 : parts.size())) {
				createPart(bodyTop, part);
				TableWrapData data = new TableWrapData();
				data.colspan = part.getSpan();
				data.align = TableWrapData.FILL;
				data.valign = TableWrapData.FILL;
				data.grabHorizontal = true;
				data.grabVertical = part.getExpandVertically();
				part.getControl().setLayoutData(data);
			}
		}

		if (fillBottomPart) {
			// the bottom composite contains the last part only and grabs the remaining vertical space
			Composite bodyBottom = new Composite(body, SWT.NONE);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(bodyBottom);
			FillLayout fillLayout = new FillLayout();
			fillLayout.marginHeight = 5;
			fillLayout.marginWidth = 5;
			bodyBottom.setLayout(fillLayout);

			AbstractBuildEditorPart part = parts.get(parts.size() - 1);
			createPart(bodyBottom, part);
		}
	}

	private void createPart(Composite body, AbstractBuildEditorPart part) {
		part.initialize(this);
		getManagedForm().addPart(part);
		Control control = part.createControl(body, toolkit);
		part.setControl(control);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		parts = new ArrayList<AbstractBuildEditorPart>();
		parts.add(new HeaderPart());
		parts.add(new SummaryPart());
		parts.add(new ActionPart());
		parts.add(new TestResultPart());
		parts.add(new ArtifactsPart());
		parts.add(new ChangesPart());
		//parts.add(new BuildOutputPart());
	}

	@Override
	public void setFocus() {
		getManagedForm().getForm().getForm().getBody().setFocus();
	}

}
