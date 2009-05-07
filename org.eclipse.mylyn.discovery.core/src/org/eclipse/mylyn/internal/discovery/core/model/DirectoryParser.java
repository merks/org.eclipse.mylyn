/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.core.model;

import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;

import org.eclipse.mylyn.internal.discovery.core.model.Directory.Entry;
import org.eclipse.mylyn.internal.discovery.core.util.DefaultSaxErrorHandler;
import org.eclipse.mylyn.internal.discovery.core.util.IOWithCauseException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A parser for {@link Directory directories}.
 * 
 * @author David Green
 */
public class DirectoryParser {
	/**
	 * parse the contents of a directory. The caller must close the given reader.
	 * 
	 * @param directoryContents
	 *            the contents of the directory
	 * @return a directory with 0 or more entries
	 * @throws IOException
	 *             if the directory cannot be read.
	 */
	public Directory parse(Reader directoryContents) throws IOException {
		XMLReader xmlReader;
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			throw new IOWithCauseException(e.getMessage(), e);
		}
		xmlReader.setErrorHandler(new DefaultSaxErrorHandler());

		DirectoryContentHandler contentHandler = new DirectoryContentHandler();
		xmlReader.setContentHandler(contentHandler);

		try {
			xmlReader.parse(new InputSource(directoryContents));
		} catch (SAXException e) {
			throw new IOWithCauseException(e.getMessage(), e);
		}

		if (contentHandler.directory == null) {
			throw new IOException("Unexpected content: no directory available");
		}

		return contentHandler.directory;
	}

	private class DirectoryContentHandler implements ContentHandler {

		Directory directory;

		public void characters(char[] ch, int start, int length) throws SAXException {
			// ignore
		}

		public void endDocument() throws SAXException {
			// ignore
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			// ignore
		}

		public void endPrefixMapping(String prefix) throws SAXException {
			// ignore
		}

		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			// ignore
		}

		public void processingInstruction(String target, String data) throws SAXException {
			// ignore
		}

		public void setDocumentLocator(Locator locator) {
			// ignore
		}

		public void skippedEntity(String name) throws SAXException {
			// ignore
		}

		public void startDocument() throws SAXException {
			// ignore
		}

		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if ("directory".equals(localName)) { //$NON-NLS-1$
				if (directory != null) {
					unexpectedElement(localName);
				}
				directory = new Directory();
			} else if (directory != null && "entry".equals(localName)) { //$NON-NLS-1$
				String url = atts.getValue("", "url"); //$NON-NLS-1$ //$NON-NLS-2$
				if (url != null && url.length() > 0) {
					Entry entry = new Entry();
					entry.setLocation(url);
					directory.getEntries().add(entry);
				}
			}
			// else ignore
		}

		private void unexpectedElement(String localName) throws SAXException {
			throw new SAXException(MessageFormat.format("Unexpected element ''{0}''", localName));
		}

		public void startPrefixMapping(String prefix, String uri) throws SAXException {
			// ignore
		}
	}
}
