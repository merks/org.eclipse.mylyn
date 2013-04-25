/**
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.edit.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers. The adapters generated by this
 * factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}. The adapters
 * also support Eclipse property sheets. Note that most of the adapters are shared among multiple instances. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class ReviewsItemProviderAdapterFactory extends ReviewsAdapterFactory implements ComposeableAdapterFactory,
		IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ReviewsItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IChange} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ChangeItemProvider changeItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IChange}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createChangeAdapter() {
		if (changeItemProvider == null) {
			changeItemProvider = new ChangeItemProvider(this);
		}

		return changeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IReview} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewItemProvider reviewItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IReview}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createReviewAdapter() {
		if (reviewItemProvider == null) {
			reviewItemProvider = new ReviewItemProvider(this);
		}

		return reviewItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IUser} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected UserItemProvider userItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IUser}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createUserAdapter() {
		if (userItemProvider == null) {
			userItemProvider = new UserItemProvider(this);
		}

		return userItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IRepository}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected RepositoryItemProvider repositoryItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IRepository}. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createRepositoryAdapter() {
		if (repositoryItemProvider == null) {
			repositoryItemProvider = new RepositoryItemProvider(this);
		}

		return repositoryItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IComment} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected CommentItemProvider commentItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IComment}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createCommentAdapter() {
		if (commentItemProvider == null) {
			commentItemProvider = new CommentItemProvider(this);
		}

		return commentItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IFileItem}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FileItemItemProvider fileItemItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IFileItem}. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createFileItemAdapter() {
		if (fileItemItemProvider == null) {
			fileItemItemProvider = new FileItemItemProvider(this);
		}

		return fileItemItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewItemSetItemProvider reviewItemSetItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet}. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createReviewItemSetAdapter() {
		if (reviewItemSetItemProvider == null) {
			reviewItemSetItemProvider = new ReviewItemSetItemProvider(this);
		}

		return reviewItemSetItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.ILineLocation}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected LineLocationItemProvider lineLocationItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.ILineLocation}. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createLineLocationAdapter() {
		if (lineLocationItemProvider == null) {
			lineLocationItemProvider = new LineLocationItemProvider(this);
		}

		return lineLocationItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.ILineRange}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected LineRangeItemProvider lineRangeItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.ILineRange}. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createLineRangeAdapter() {
		if (lineRangeItemProvider == null) {
			lineRangeItemProvider = new LineRangeItemProvider(this);
		}

		return lineRangeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IFileVersion}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FileVersionItemProvider fileVersionItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IFileVersion}. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createFileVersionAdapter() {
		if (fileVersionItemProvider == null) {
			fileVersionItemProvider = new FileVersionItemProvider(this);
		}

		return fileVersionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IApprovalType}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ApprovalTypeItemProvider approvalTypeItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IApprovalType}. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createApprovalTypeAdapter() {
		if (approvalTypeItemProvider == null) {
			approvalTypeItemProvider = new ApprovalTypeItemProvider(this);
		}

		return approvalTypeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link java.util.Map.Entry} instances. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected UserApprovalsMapItemProvider userApprovalsMapItemProvider;

	/**
	 * This creates an adapter for a {@link java.util.Map.Entry}. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createUserApprovalsMapAdapter() {
		if (userApprovalsMapItemProvider == null) {
			userApprovalsMapItemProvider = new UserApprovalsMapItemProvider(this);
		}

		return userApprovalsMapItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewerEntryItemProvider reviewerEntryItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry}. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createReviewerEntryAdapter() {
		if (reviewerEntryItemProvider == null) {
			reviewerEntryItemProvider = new ReviewerEntryItemProvider(this);
		}

		return reviewerEntryItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link java.util.Map.Entry} instances. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ApprovalValueMapItemProvider approvalValueMapItemProvider;

	/**
	 * This creates an adapter for a {@link java.util.Map.Entry}. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createApprovalValueMapAdapter() {
		if (approvalValueMapItemProvider == null) {
			approvalValueMapItemProvider = new ApprovalValueMapItemProvider(this);
		}

		return approvalValueMapItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected RequirementEntryItemProvider requirementEntryItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry}. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createRequirementEntryAdapter() {
		if (requirementEntryItemProvider == null) {
			requirementEntryItemProvider = new RequirementEntryItemProvider(this);
		}

		return requirementEntryItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link java.util.Map.Entry} instances. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewRequirementsMapItemProvider reviewRequirementsMapItemProvider;

	/**
	 * This creates an adapter for a {@link java.util.Map.Entry}. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createReviewRequirementsMapAdapter() {
		if (reviewRequirementsMapItemProvider == null) {
			reviewRequirementsMapItemProvider = new ReviewRequirementsMapItemProvider(this);
		}

		return reviewRequirementsMapItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.mylyn.reviews.core.model.IRequirementReviewState} instances. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected RequirementReviewStateItemProvider requirementReviewStateItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.IRequirementReviewState}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createRequirementReviewStateAdapter() {
		if (requirementReviewStateItemProvider == null) {
			requirementReviewStateItemProvider = new RequirementReviewStateItemProvider(this);
		}

		return requirementReviewStateItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.mylyn.reviews.core.model.ISimpleReviewState}
	 * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected SimpleReviewStateItemProvider simpleReviewStateItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.mylyn.reviews.core.model.ISimpleReviewState}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createSimpleReviewStateAdapter() {
		if (simpleReviewStateItemProvider == null) {
			simpleReviewStateItemProvider = new SimpleReviewStateItemProvider(this);
		}

		return simpleReviewStateItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>) type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void dispose() {
		if (changeItemProvider != null)
			changeItemProvider.dispose();
		if (reviewItemProvider != null)
			reviewItemProvider.dispose();
		if (commentItemProvider != null)
			commentItemProvider.dispose();
		if (userItemProvider != null)
			userItemProvider.dispose();
		if (repositoryItemProvider != null)
			repositoryItemProvider.dispose();
		if (fileItemItemProvider != null)
			fileItemItemProvider.dispose();
		if (reviewItemSetItemProvider != null)
			reviewItemSetItemProvider.dispose();
		if (lineLocationItemProvider != null)
			lineLocationItemProvider.dispose();
		if (lineRangeItemProvider != null)
			lineRangeItemProvider.dispose();
		if (fileVersionItemProvider != null)
			fileVersionItemProvider.dispose();
		if (approvalTypeItemProvider != null)
			approvalTypeItemProvider.dispose();
		if (userApprovalsMapItemProvider != null)
			userApprovalsMapItemProvider.dispose();
		if (reviewerEntryItemProvider != null)
			reviewerEntryItemProvider.dispose();
		if (approvalValueMapItemProvider != null)
			approvalValueMapItemProvider.dispose();
		if (requirementEntryItemProvider != null)
			requirementEntryItemProvider.dispose();
		if (reviewRequirementsMapItemProvider != null)
			reviewRequirementsMapItemProvider.dispose();
		if (requirementReviewStateItemProvider != null)
			requirementReviewStateItemProvider.dispose();
		if (simpleReviewStateItemProvider != null)
			simpleReviewStateItemProvider.dispose();
	}

}
