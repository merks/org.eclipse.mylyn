/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.internal.events.ResourceDelta;
import org.eclipse.core.internal.events.ResourceDeltaInfo;
import org.eclipse.core.internal.resources.ResourceInfo;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.resources.ui.ResourceChangeMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class ResourceChangeMonitorTest extends AbstractResourceContextTest {

	private static class MockResourceDelta extends ResourceDelta {

		protected MockResourceDelta(IPath path, ResourceDeltaInfo deltaInfo) {
			super(path, deltaInfo);

		}

		static MockResourceDelta createMockDelta(String path, String[] childPaths, int status, int resourceType) {
			// create the delta and fill it with information

			ResourceDeltaInfo deltaInfo = new ResourceDeltaInfo((Workspace) ResourcesPlugin.getWorkspace(), null, null);
			MockResourceDelta result = new MockResourceDelta(new Path(path), deltaInfo);

			ResourceInfo info = new ResourceInfo();

			info.setType(resourceType);

			result.setNewInfo(info);
			result.setOldInfo(info);

			Set<MockResourceDelta> children = new HashSet<MockResourceDelta>();

			if (childPaths != null) {
				for (String childPath : childPaths) {
					children.add(createMockDelta(path + childPath, null, status, IResource.FILE));
				}
			}

			result.setChildren(children.toArray(new MockResourceDelta[0]));
			result.setStatus(status);

			return result;
		}

		@Override
		public void setChildren(ResourceDelta[] children) {
			super.setChildren(children);
		}
	}

	private ResourceChangeMonitor changeMonitor;

	private IFolder folder;

	private IFile fileInFolder;

	private IFile file;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		changeMonitor = new ResourceChangeMonitor();
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(true);

		ContextCore.getContextManager().setContextCapturePaused(true);

		file = project.getProject().getFile("test.txt");
		file.create(null, true, null);
		assertTrue(file.exists());

		folder = project.getProject().getFolder("testFolder");
		folder.create(true, true, null);
		assertTrue(folder.exists());

		fileInFolder = folder.getFile("test.txt");
		fileInFolder.create(null, true, null);
		assertTrue(fileInFolder.exists());

		ContextCore.getContextManager().setContextCapturePaused(false);
	}

	@Override
	protected void tearDown() throws Exception {
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(false);
		super.tearDown();
	}

	public void testForcedExclusionPatterns() {
		String pattern = "file:/foo";
		try {
			ResourcesUiPreferenceInitializer.addForcedExclusionPattern(pattern);
			assertTrue(ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns().contains(pattern));
			assertFalse(ResourcesUiPreferenceInitializer.getExcludedResourcePatterns().contains(pattern));
		} finally {
			ResourcesUiPreferenceInitializer.removeForcedExclusionPattern(pattern);
		}
	}

	public void testFileUriExclusionPattern() throws URISyntaxException {
		URI uri = new URI("file:/C:");
		assertTrue(ResourceChangeMonitor.isUriExcluded(uri.toString(), "file:/C:"));

		uri = new URI("file:/C:/foo/bar");
		assertTrue(ResourceChangeMonitor.isUriExcluded(uri.toString(), "file:/C:"));
	}

	public void testSnapshotExclusionPattern() {
		// .*

		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourceChangeMonitor.convertToAntPattern(".*"));

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.foo"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.foo/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.foo"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.foo/foo2"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/foo.test"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/d.foo"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder/d.foo"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder/d.foo/foo2"), null, patterns));

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.foo2"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.foo2/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.foo2/foo3"), null, patterns));
	}

	public void testFileExclusionPattern() {
		// *.doc

		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourceChangeMonitor.convertToAntPattern("*.doc"));

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.doc"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.doc/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc/foo2"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/d.doc"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/~d.doc"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder/d.doc2"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder/d.doc2/foo2"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/document"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/.doc2"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/.doc2/test"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc2/foo3"), null, patterns));
	}

	public void testAllExclusionPattern() {
		// *
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourceChangeMonitor.convertToAntPattern("*"));

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.doc"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.doc2/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/~"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/test"), null, patterns));

	}

	public void testAllFileExclusionPattern() {
		// *.*
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourceChangeMonitor.convertToAntPattern("*.*"));

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.doc"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.doc2/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.test"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/~"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/test"), null, patterns));

	}

	public void testWildcardExclusionPattern() {
		// ~*
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourceChangeMonitor.convertToAntPattern("~*"));

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/~"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/~.doc"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/~.doc2/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/~folder/.doc2/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/~test.doc"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/test"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/test~"), null, patterns));

	}

	public void testNoWildcardExclusionPattern() {
		// folder
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourceChangeMonitor.convertToAntPattern("folder"));

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.doc2/folder/test"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/.doc2/test/folder"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder.test"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/.folder"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder2"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/.doc2folder/test"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/.doc"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/.doc2/test"), null, patterns));
	}

	public void testPathPrefixExclusionPattern() {
		// test/**
		Set<String> patterns = new HashSet<String>();
		patterns.add("/folder/**");

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc2/test"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/.doc2/folder/test"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/.doc2/test/folder"), null, patterns));

	}

	public void testPathPostfixExclusionPattern() {
		// **/test/
		Set<String> patterns = new HashSet<String>();
		patterns.add("**/folder");

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder1/folder"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder1/folder/folder2/test.doc"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder/"), null, patterns));

	}

	public void testComplexExclusionPattern() {
		// **/folder/***.doc
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourceChangeMonitor.convertToAntPattern("**/folder/***.doc"));

		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/test.doc"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder/folder2/test.doc"), null, patterns));
		assertTrue(ResourceChangeMonitor.isExcluded(new Path("/folder1/folder/folder2/test.doc"), null, patterns));

		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder1/folder/folder2/test.docx"), null, patterns));
		assertFalse(ResourceChangeMonitor.isExcluded(new Path("/folder1/folder2/folder3/test.doc"), null, patterns));
	}

	public void testInclusion() {
		IPath path4 = new Path("bla/bla");
		assertFalse(ResourceChangeMonitor.isExcluded(path4, null, new HashSet<String>()));
	}

	public void testCreatedFile() throws CoreException {
		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				new String[] { "/test.txt" }, (IResourceDelta.ADDED | IResourceDelta.CONTENT), IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNotNull(element);
		assertTrue(element.getInterest().isPropagated());
	}

	public void testModifiedFile() throws CoreException {
		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				new String[] { "/test.txt" }, (IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNotNull(element);
		assertTrue(element.getInterest().isPredicted());
	}

	public void testDerrivedFileChanged() throws CoreException {
		fileInFolder.setDerived(true);

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
				new String[] { "/test.txt" }, (IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.PROJECT);
		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(fileInFolder).getHandleIdentifier(fileInFolder);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
	}

	public void testDerrivedFolderChanged() throws CoreException {
		folder.setDerived(true);
		fileInFolder.setDerived(false);

		MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(), null,
				(IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.PROJECT);

		MockResourceDelta child = MockResourceDelta.createMockDelta("/" + project.getProject().getName() + "/"
				+ folder.getName(), new String[] { "/" + folder.getName() + "/test.txt" },
				(IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.FOLDER);

		delta.setChildren(new ResourceDelta[] { child });

		IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
		changeMonitor.resourceChanged(event);
		String handle = ContextCore.getStructureBridge(fileInFolder).getHandleIdentifier(folder);
		assertNotNull(handle);
		IInteractionElement element = context.get(handle);
		assertNull(element);
		handle = ContextCore.getStructureBridge(fileInFolder).getHandleIdentifier(fileInFolder);
		assertNotNull(handle);
		element = context.get(handle);
		assertNull(element);
	}

	public void testExcluded() throws CoreException {
		try {
			ResourcesUiPreferenceInitializer.addForcedExclusionPattern("*.txt");

			MockResourceDelta delta = MockResourceDelta.createMockDelta("/" + project.getProject().getName(),
					new String[] { "/test.txt" }, (IResourceDelta.CHANGED | IResourceDelta.CONTENT), IResource.PROJECT);
			IResourceChangeEvent event = new ResourceChangeEvent(delta, IResourceChangeEvent.POST_CHANGE, 0, delta);
			changeMonitor.resourceChanged(event);
			String handle = ContextCore.getStructureBridge(file).getHandleIdentifier(file);
			assertNotNull(handle);
			IInteractionElement element = context.get(handle);
			assertNull(element);
		} finally {
			ResourcesUiPreferenceInitializer.removeForcedExclusionPattern("*.txt");
		}
	}

}
