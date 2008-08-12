/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.textile.core.token;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * 
 * 
 * @author David Green
 */
public class FootnoteReferenceReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(?:\\[(\\d+)\\])";
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new FootnoteReferenceReplacementTokenProcessor();
	}

	private static class FootnoteReferenceReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String footnote = group(1);
			String htmlId = state.getFootnoteId(footnote);
			builder.beginSpan(SpanType.SUPERSCRIPT, new Attributes(null, "footnote", null, null));
			builder.link("#" + htmlId, footnote);
			builder.endSpan();
		}
	}

}
