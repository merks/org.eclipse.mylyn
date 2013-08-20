/*******************************************************************************
 * Copyright (c) 2012 SpringSource, a divison of VMware, Inc.
 * Copyright (c) 2012 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.views;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiConstants;
import org.eclipse.mylyn.internal.reviews.ui.providers.ReviewsLabelProvider;
import org.eclipse.mylyn.internal.reviews.ui.providers.TableStyledLabelProvider;
import org.eclipse.mylyn.internal.reviews.ui.providers.TableStyledLabelProvider.TableColumnProvider;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.ReviewsClient;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.editor.AbstractReviewTaskEditorPage;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonFilterDescriptor;
import org.eclipse.ui.navigator.INavigatorActivationService;
import org.eclipse.ui.navigator.INavigatorFilterService;

/**
 * @author Miles Parker
 */
public class ReviewExplorer extends CommonNavigator {

	public static final String SHOW_VIEW_LIST = "showViewList";

	public static final String FILTER_FOR_COMMENTS = "filterForComments";

	private static final String TREE_ACTION_GROUP = "tree";

	private static final String FILTER_ACTION_GROUP = "filters";

	private static final String REFRESH_ACTION_GROUP = "refresh";

	private RefreshReviewsAction refreshAction;

	private boolean showList;

	private boolean filterForComments;

	private IReview review = null;

	private TaskEditor currentPart;

	private ReviewsLabelProvider treeLabelProvider;

	private ReviewsLabelProvider flatLabelProvider;

	private TableStyledLabelProvider currentProvider;

	private RemoteEmfConsumer<IRepository, IReview, String, ?, ?, Date> reviewConsumer;

	private final RemoteEmfObserver<IRepository, IReview, String, Date> reviewObserver = new RemoteEmfObserver<IRepository, IReview, String, Date>() {

		@Override
		public void updated(boolean modified) {
			if (modified) {
				updatePatchSetObservers();
				updatePerservingSelection();
			}
		}
	};

	private class RemoteItemSetContentObserver extends RemoteEmfObserver<IReviewItemSet, List<IFileItem>, String, Long> {
		@Override
		public void updated(boolean modified) {
			if (showList) {
				getCommonViewer().refresh(true);
			} else {
				//Tree, so we can just refresh the parent set, assuming it's currently displayed
				TreeItem[] rootItems = getCommonViewer().getTree().getItems();
				boolean parentDisplayed = false;
				for (TreeItem treeItem : rootItems) {
					Object data = treeItem.getData();
					if (data == getConsumer().getParentObject()) {
						parentDisplayed = true;
						break;
					}
				}
				if (parentDisplayed) {
					getCommonViewer().refresh(getConsumer().getParentObject(), true);
				} else { //treeitem is currently being filtered (as a non-commented set, for example)
					getCommonViewer().refresh(true);
				}
			}
		}
	};

	private final Map<IReviewItemSet, RemoteItemSetContentObserver> patchSetObservers = new HashMap<IReviewItemSet, RemoteItemSetContentObserver>();

	private final IPartListener editorPartListener = new IPartListener() {
		public void partOpened(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			if (part == currentPart) {
				currentPart = null;
				setReview(null);
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partActivated(IWorkbenchPart part) {
			if (part instanceof TaskEditor && currentPart != part) {
				TaskEditor editor = (TaskEditor) part;
				IFormPage page = editor.getActivePageInstance();
				if (page instanceof AbstractReviewTaskEditorPage) {
					currentPart = (TaskEditor) part;
					AbstractReviewTaskEditorPage reviewPage = (AbstractReviewTaskEditorPage) page;
					setReview(reviewPage.getReview());
					updateContentDescription();
				}
			}
		}
	};

	private final IPageListener pageListener = new IPageListener() {

		private IWorkbenchPage activePage;

		public void pageOpened(IWorkbenchPage page) {
		}

		public void pageClosed(IWorkbenchPage page) {
			pageActivated(null);
		}

		public void pageActivated(IWorkbenchPage page) {
			if (page != activePage) {
				if (activePage != null) {
					activePage.removePartListener(editorPartListener);
				}
				if (page != null) {
					page.addPartListener(editorPartListener);
					editorPartListener.partActivated(page.getActiveEditor());
				}
				activePage = page;
			}
		}
	};

	private IReviewRemoteFactoryProvider factoryProvider;

	class ShowListAction extends Action {
		public ShowListAction() {
			super("", AS_RADIO_BUTTON); //$NON-NLS-1$
			setText("Show List");
			setDescription("Show comments in a flat list");
			setToolTipText("Show all comments in a flat list (Hide files and patch sets)");
			setImageDescriptor(ReviewsImages.FLAT_LAYOUT);
		}

		/*
		 * @see Action#actionPerformed
		 */
		@Override
		public void run() {
			if (isChecked()) {
				if (getMemento() != null) {
					getMemento().putBoolean(SHOW_VIEW_LIST, true);
				}
				showList = true;
				updateActivations();
			}
		}
	}

	class ShowTreeAction extends Action {

		public ShowTreeAction() {
			super("", AS_RADIO_BUTTON); //$NON-NLS-1$
			setText("Show Tree");
			setDescription("Show all items in a tree");
			setToolTipText("Show artifacts, files and global comments in a tree");
			setImageDescriptor(ReviewsImages.HIERARCHICAL_LAYOUT);
		}

		/*
		 * @see Action#actionPerformed
		 */
		@Override
		public void run() {
			if (isChecked()) {
				if (getMemento() != null) {
					getMemento().putBoolean(SHOW_VIEW_LIST, false);
				}
				showList = false;
				updateActivations();
			}
		}
	}

	class RefreshReviewsAction extends Action {

		public RefreshReviewsAction() {
			super("", AS_PUSH_BUTTON); //$NON-NLS-1$
			setText("Refresh");
			setDescription("Refresh Review Items");
			setToolTipText("Refresh Review Items");
			setImageDescriptor(ReviewsImages.REFRESH);
		}

		/*
		 * @see Action#actionPerformed
		 */
		@Override
		public void run() {
			refresh();
		}
	}

	class FilterNonCommentsReviewsAction extends Action {

		public FilterNonCommentsReviewsAction() {
			super("", AS_CHECK_BOX); //$NON-NLS-1$
			setText("Filter for Comments");
			setDescription("Filter items for comments.");
			setToolTipText("Hide items that don't have comments");
			setImageDescriptor(ReviewsImages.REVIEW_QUOTE);
		}

		/*
		 * @see Action#actionPerformed
		 */
		@Override
		public void run() {
			filterForComments = isChecked();
			if (memento != null) {
				memento.putBoolean(FILTER_FOR_COMMENTS, filterForComments);
			}
			updateActivations();
		}
	}

	@Override
	protected CommonViewer createCommonViewer(Composite parent) {
		flatLabelProvider = new ReviewsLabelProvider.Flat();
		treeLabelProvider = new ReviewsLabelProvider.Tree();
		final CommonViewer viewer = super.createCommonViewer(parent);
		updateTreeViewer(viewer);
		viewer.addTreeListener(new ITreeViewerListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				if (event.getElement() instanceof IReviewItemSet) {
					IReviewItemSet set = (IReviewItemSet) event.getElement();
					if (set.getItems().size() == 0) {
						RemoteItemSetContentObserver observer = patchSetObservers.get(set);
						observer.getConsumer().retrieve(false);
					}
				}
			}

			public void treeCollapsed(TreeExpansionEvent event) {
			}
		});
		return viewer;
	}

	void updateTreeViewer(CommonViewer viewer) {
		for (TreeColumn column : viewer.getTree().getColumns()) {
			column.dispose();
		}
		if (isShowColumns()) {
			//Artifact top-level
			Tree treeTable = viewer.getTree();

			treeTable.setHeaderVisible(true);
			treeTable.setLinesVisible(false);
			treeTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			currentProvider = null;
			if (isFlat()) {
				currentProvider = flatLabelProvider;
			} else {
				currentProvider = treeLabelProvider;
			}
			if (viewer.getLabelProvider() != currentProvider) {
				viewer.setLabelProvider(currentProvider);
			}
			updateTable(viewer);
		}
	}

	void updateTable(TreeViewer viewer) {
		TableLayout layout = new TableLayout();
		viewer.getTree().setLayout(layout);
		ColumnViewerToolTipSupport.enableFor(viewer);
		for (TableColumnProvider columnProvider : currentProvider.getColumnProviders()) {
			updateColumn(viewer, columnProvider);
		}
	}

	void updateColumn(TreeViewer viewer, final TableColumnProvider provider) {
		TreeColumn column = null;
		TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
		final DelegatingStyledCellLabelProvider styledLabelProvider = new DelegatingStyledCellLabelProvider(provider) {
			@Override
			public String getToolTipText(Object element) {
				//For some reason tooltips are not delegated..
				return provider.getToolTipText(element);
			};
		};
		viewerColumn.setLabelProvider(styledLabelProvider);
		column = viewerColumn.getColumn();
		column.setText(provider.getTitle());
		if (!provider.isFillAvailable()) {
			column.setWidth(provider.getMinimumSize());
		} else {
			int width = viewer.getTree().getClientArea().width;
			if (!viewer.getTree().getVerticalBar().isVisible()) {
				width -= viewer.getTree().getVerticalBar().getSize().x;
			}
			int allWidths = 0;
			for (TableColumnProvider provider2 : currentProvider.getColumnProviders()) {
				if (provider2 != provider) {
					allWidths += provider2.getMinimumSize();
				}
			}
			column.setWidth((width - allWidths));
		}
		TableLayout layout = (TableLayout) viewer.getTree().getLayout();
		layout.addColumnData(new ColumnWeightData(provider.getWeight(), provider.getMinimumSize()));
	}

	@Override
	protected CommonViewer createCommonViewerObject(Composite parent) {
		return new CommonViewer(getViewId(), parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	}

	private Collection<Object> matchingElements(ITreeContentProvider provider, Object parent,
			Collection<Object> toMatch, boolean prune) {
		HashSet<String> matchingLabels = new HashSet<String>();
		for (Object object : toMatch) {
			String label = ((ILabelProvider) getCommonViewer().getLabelProvider()).getText(object);
			matchingLabels.add(label);
		}
		return matchingLabelElements(provider, parent, matchingLabels, prune);
	}

	private Collection<Object> matchingLabelElements(ITreeContentProvider provider, Object parent,
			Collection<String> toMatch, boolean prune) {
		HashSet<Object> matches = new HashSet<Object>();
		String parentLabel = ((ILabelProvider) getCommonViewer().getLabelProvider()).getText(parent);
		if (toMatch.contains(parentLabel)) {
			matches.add(parent);
		}
		Object[] children = provider.getElements(parent);
		for (Object object : children) {
			String childLabel = ((ILabelProvider) getCommonViewer().getLabelProvider()).getText(object);
			if (!prune || toMatch.contains(childLabel)) {
				matches.addAll(matchingLabelElements(provider, object, toMatch, prune));
			}
		}
		return matches;
	}

	/**
	 * @see org.eclipse.ui.navigator.CommonNavigator#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite aParent) {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager manager = actionBars.getToolBarManager();

		if (isSupportsListTree()) {
			showList = false;
			if (getMemento() != null) {
				Boolean value = getMemento().getBoolean(SHOW_VIEW_LIST);
				if (value != null) {
					showList = value;
				}
			}
			ShowTreeAction showTreeAction = new ShowTreeAction();
			showTreeAction.setChecked(!showList);
			ShowListAction showListAction = new ShowListAction();
			showListAction.setChecked(showList);
			manager.add(new Separator(TREE_ACTION_GROUP));
			manager.add(new Separator("presentation")); //$NON-NLS-1$
			manager.appendToGroup("presentation", showTreeAction); //$NON-NLS-1$
			manager.appendToGroup("presentation", showListAction); //$NON-NLS-1$
		}
		filterForComments = false;
		if (getMemento() != null) {
			Boolean value = getMemento().getBoolean(FILTER_FOR_COMMENTS);
			if (value != null) {
				filterForComments = value;
			}
		}
		manager.add(new Separator(FILTER_ACTION_GROUP));
		manager.appendToGroup(FILTER_ACTION_GROUP, new FilterNonCommentsReviewsAction());
		manager.add(new Separator("Separator"));

		super.createPartControl(aParent);

		manager.add(new Separator(REFRESH_ACTION_GROUP));
		refreshAction = new RefreshReviewsAction();
		refreshAction.setEnabled(false);
		manager.appendToGroup(REFRESH_ACTION_GROUP, refreshAction);

		updateActivations();

		pageListener.pageActivated(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
		update();
	}

	protected void updateContentDescription() {
		String title = "(No Selection)";
		if (currentPart != null && currentPart.getTaskEditorInput() != null) {
			ITask task = currentPart.getTaskEditorInput().getTask();
			title = "Change " + task.getTaskId() + ": " + task.getSummary();
		}
		setContentDescription(title);
	}

	protected void update() {
		updateContentDescription();
		if (!getCommonViewer().getControl().isDisposed()) {
			refreshAction.setEnabled(review != null);
			getCommonViewer().setInput(review);
			getCommonViewer().refresh();
		}
	}

	protected void refresh() {
		if (reviewConsumer != null) {
			reviewConsumer.retrieve(true);
			for (RemoteItemSetContentObserver observer : patchSetObservers.values()) {
				observer.getConsumer().retrieve(true);
			}
		}
	}

	protected void updatePerservingSelection() {
		if (!getCommonViewer().getControl().isDisposed()) {
			//Because we don't necessarily have the same backing EMF objects, we need to test on equality. We'd also like to restore selection even if the tree structure changes. In this case, using labels will be most consistent with user expectations..
			Object[] priorExpanded = getCommonViewer().getExpandedElements();
			Object[] priorSelection = new Object[] {};
			if (getCommonViewer().getSelection() instanceof IStructuredSelection) {
				priorSelection = ((IStructuredSelection) getCommonViewer().getSelection()).toArray();
			}
			getCommonViewer().getControl().setRedraw(false);
			update();
			Collection<Object> newExpanded = matchingElements(
					(ITreeContentProvider) getCommonViewer().getContentProvider(), review,
					new HashSet<Object>(Arrays.asList(priorExpanded)), true);
			Collection<Object> newSelection = matchingElements(
					(ITreeContentProvider) getCommonViewer().getContentProvider(), review,
					new HashSet<Object>(Arrays.asList(priorSelection)), false);
			getCommonViewer().setExpandedElements(newExpanded.toArray());
			getCommonViewer().setSelection(new StructuredSelection(newSelection.toArray()), true);
			getCommonViewer().getControl().setRedraw(true);
			getCommonViewer().getControl().redraw();
		}
	}

	public void setReview(IReview review) {
		if (this.review != review) {
			this.review = review;

			for (RemoteEmfObserver<IReviewItemSet, List<IFileItem>, String, Long> observer : patchSetObservers.values()) {
				observer.dispose();
			}
			patchSetObservers.clear();
			if (reviewConsumer != null) {
				reviewConsumer.removeObserver(reviewObserver);
			}
			if (review != null) {
				ReviewsConnector connector = (ReviewsConnector) TasksUiPlugin.getConnector(review.getRepository()
						.getTaskConnectorKind());
				ReviewsClient reviewsClient = connector.getReviewClient(review.getRepository().getTaskRepository());
				factoryProvider = (IReviewRemoteFactoryProvider) reviewsClient.getFactoryProvider();
				reviewConsumer = factoryProvider.getReviewFactory().getConsumerForModel(factoryProvider.getRoot(),
						review);
				reviewConsumer.addObserver(reviewObserver);
				updatePatchSetObservers();
			}

			update();
		}
	}

	/**
	 * @see org.eclipse.ui.navigator.CommonNavigator#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento aMemento) {
		super.saveState(aMemento);
		if (isSupportsListTree()) {
			aMemento.putBoolean(SHOW_VIEW_LIST, showList);
			aMemento.putBoolean(FILTER_ACTION_GROUP, filterForComments);
		}
	}

	public void updatePatchSetObservers() {
		for (IReviewItemSet set : review.getSets()) {
			RemoteItemSetContentObserver client = patchSetObservers.get(set);
			if (client == null) {
				RemoteItemSetContentObserver patchSetObserver = new RemoteItemSetContentObserver();
				RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, ?, ?, Long> consumer = factoryProvider.getReviewItemSetContentFactory()
						.getConsumerForLocalKey(set, set.getId());
				patchSetObserver.setConsumer(consumer);
				patchSetObservers.put(set, patchSetObserver);
			}
		}
	}

	/* (non-Javadoc)
	 * Method declared on IWorkbenchPart.
	 */
	@Override
	public void dispose() {
		super.dispose();
		//Don't hang on to references
		flatLabelProvider.doDispose();
		treeLabelProvider.doDispose();
		currentPart = null;
		setReview(null);
		pageListener.pageActivated(null);
	}

	public boolean isFlat() {
		return isSupportsListTree() && showList;
	}

	protected String getTreeContentId() {
		return ReviewsUiConstants.REVIEW_CONTENT_ID;
	}

	protected String getListContentId() {
		return ReviewsUiConstants.REVIEW_FLAT_CONTENT_ID;
	}

	protected String getViewId() {
		return ReviewsUiConstants.REVIEW_EXPLORER_ID;
	}

	protected final boolean isSupportsListTree() {
		return getListContentId() != null;
	}

	protected boolean isShowColumns() {
		return true;
	}

	public boolean isFilterForComments() {
		return filterForComments;
	}

	protected void updateActivations() {
		INavigatorActivationService activationService = getCommonViewer().getNavigatorContentService()
				.getActivationService();
		if (!isSupportsListTree()) {
			activationService.activateExtensions(new String[] { getTreeContentId() }, false);
		} else {
			if (isFlat()) {
				activationService.deactivateExtensions(new String[] { getTreeContentId() }, false);
				activationService.activateExtensions(new String[] { getListContentId() }, false);
			} else {
				activationService.deactivateExtensions(new String[] { getListContentId() }, false);
				activationService.activateExtensions(new String[] { getTreeContentId() }, false);
			}
		}
		INavigatorFilterService filterService = getCommonViewer().getNavigatorContentService().getFilterService();
		ICommonFilterDescriptor[] visibleDescriptors = filterService.getVisibleFilterDescriptors();
		boolean commentFilterActive = false;
		for (ICommonFilterDescriptor descriptor : visibleDescriptors) {
			if (descriptor.getId().equals(ReviewsUiConstants.REVIEW_FILTER_FOR_COMMENTS)
					&& filterService.isActive(descriptor.getId())) {
				commentFilterActive = true;
				break;
			}
		}
		boolean filtersModified = false;
		if (isFilterForComments() && !commentFilterActive) {
			filterService.setActiveFilterIds(new String[] { ReviewsUiConstants.REVIEW_FILTER_FOR_COMMENTS });
			filtersModified = true;
		} else if (!isFilterForComments() && commentFilterActive) {
			filterService.setActiveFilterIds(new String[] {});
			filtersModified = true;
		}
		if (filtersModified) {
			filterService.persistFilterActivationState();
			ViewerFilter[] visibleFilters = filterService.getVisibleFilters(true);
			getCommonViewer().setFilters(visibleFilters);
		}

		updateTreeViewer(getCommonViewer());
		getCommonViewer().refresh();
	}

	public IWorkbenchPart getCurrentPart() {
		return currentPart;
	}
}
