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

import java.util.Optional;

public class AllCharactersSpan extends SourceSpan {

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		return Optional.of(
				new Characters(cursor.getLineAtOffset(), cursor.getOffset(), 1, Character.toString(cursor.getChar())));
	}

}
