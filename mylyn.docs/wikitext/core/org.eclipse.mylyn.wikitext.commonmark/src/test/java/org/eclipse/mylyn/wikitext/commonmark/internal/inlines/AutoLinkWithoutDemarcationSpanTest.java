/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static org.eclipse.mylyn.wikitext.commonmark.internal.inlines.Cursors.createCursor;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AutoLinkWithoutDemarcationSpanTest extends AbstractSourceSpanTest {

	public AutoLinkWithoutDemarcationSpanTest() {
		super(new AutoLinkWithoutDemarcationSpan());
	}

	@Test
	public void createInline() {
		assertNoInline(createCursor("httpx://example.com sdf"));
		assertNoInline(createCursor("http:/"));
		assertLink(0, 28, "http://example.com:8080/#see", "http://example.com:8080/#see",
				createCursor("http://example.com:8080/#see one"));
		assertLink(0, 23, "https://example.com/foo", "https://example.com/foo",
				createCursor("https://example.com/foo\\"));
		assertLink(1, 23, "https://example.com/foo", "https://example.com/foo",
				createCursor("(https://example.com/foo)", 1));
	}

	private void assertLink(int offset, int length, String linkHref, String text, Cursor cursor) {
		Link link = assertInline(Link.class, offset, length, cursor);
		assertEquals(linkHref, link.getHref());
		assertEquals(1, link.getContents().size());
		assertEquals(Characters.class, link.getContents().get(0).getClass());
		assertEquals(text, ((Characters) link.getContents().get(0)).getText());
	}
}
