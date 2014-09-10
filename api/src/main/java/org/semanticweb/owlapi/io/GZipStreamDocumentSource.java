/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * An ontology document source which can read from a GZIP stream.
 * 
 * @author ignazio
 * @since 3.4.8
 */
public class GZipStreamDocumentSource extends OWLOntologyDocumentSourceBase {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GZipStreamDocumentSource.class);
    private byte[] buffer;

    /**
     * Constructs an input source which will read an ontology from a
     * representation from the specified file.
     * 
     * @param is
     *        The stream that the ontology representation will be read from.
     */
    public GZipStreamDocumentSource(@Nonnull InputStream is) {
        super("gzipinputstream:ontology", null, null);
        readIntoBuffer(is);
    }

    /**
     * Constructs an input source which will read an ontology from a
     * representation from the specified stream.
     * 
     * @param stream
     *        The stream that the ontology representation will be read from.
     * @param documentIRI
     *        The document IRI
     * @param format
     *        ontology format
     * @param mime
     *        mime type
     */
    public GZipStreamDocumentSource(@Nonnull InputStream stream,
            @Nonnull IRI documentIRI, @Nullable OWLDocumentFormat format,
            @Nullable String mime) {
        super(documentIRI, format, mime);
        readIntoBuffer(stream);
    }

    private void readIntoBuffer(@Nonnull InputStream reader) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int length = 100000;
            byte[] tempBuffer = new byte[length];
            int read = 0;
            do {
                read = reader.read(tempBuffer, 0, length);
                if (read > 0) {
                    bos.write(tempBuffer, 0, read);
                }
            } while (read > 0);
            buffer = bos.toByteArray();
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    @Override
    public Optional<InputStream> getInputStream() {
        if (buffer == null) {
            return Optional.absent();
        }
        try {
            return Optional.of(DocumentSources.wrap(new GZIPInputStream(
                    new ByteArrayInputStream(buffer))));
        } catch (IOException e) {
            LOGGER.error("Buffer cannot be opened", e);
            failedOnStreams.set(true);
            return Optional.absent();
        }
    }
}
