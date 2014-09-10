package org.semanticweb.owlapi.io;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

import com.google.common.base.Optional;

/**
 * Base class for OWLOntologyDocumentSource.
 * 
 * @author ignazio
 * @since 4.0.0
 */
public abstract class OWLOntologyDocumentSourceBase implements
        OWLOntologyDocumentSource {

    private static final AtomicLong COUNTER = new AtomicLong();

    /**
     * @param prefix
     *        prefix for result
     * @return a fresh IRI
     */
    @Nonnull
    public static IRI getNextDocumentIRI(String prefix) {
        return IRI.create(prefix + COUNTER.incrementAndGet());
    }

    private final IRI documentIRI;
    private final Optional<OWLDocumentFormat> format;
    private final Optional<String> mimeType;
    protected final AtomicBoolean failedOnStreams = new AtomicBoolean(false);
    protected final AtomicBoolean failedOnIRI = new AtomicBoolean(false);

    /**
     * Constructs an ontology input source using the specified file.
     * 
     * @param iri
     *        document IRI
     * @param format
     *        ontology format. If null, it is considered unspecified
     * @param mime
     *        mime type. If null or empty, it is considered unspecified.
     */
    public OWLOntologyDocumentSourceBase(@Nonnull IRI iri,
            @Nullable OWLDocumentFormat format, @Nullable String mime) {
        this.format = Optional.fromNullable(format);
        mimeType = Optional.fromNullable(mime);
        documentIRI = checkNotNull(iri, "document iri cannot be null");
    }

    /**
     * Constructs an ontology input source using the specified file.
     * 
     * @param iriPrefix
     *        document IRI prefix - used to generate a new IRI
     * @param format
     *        ontology format. If null, it is considered unspecified
     * @param mime
     *        mime type. If null or empty, it is considered unspecified.
     */
    public OWLOntologyDocumentSourceBase(@Nonnull String iriPrefix,
            @Nullable OWLDocumentFormat format, @Nullable String mime) {
        this(getNextDocumentIRI(iriPrefix), format, mime);
    }

    @Override
    public final IRI getDocumentIRI() {
        return documentIRI;
    }

    @Override
    public boolean hasAlredyFailedOnStreams() {
        return failedOnStreams.get();
    }

    @Override
    public boolean hasAlredyFailedOnIRIResolution() {
        return failedOnIRI.get();
    }

    @Override
    public void setIRIResolutionFailed(boolean value) {
        failedOnIRI.set(value);
    }

    @Override
    public Optional<OWLDocumentFormat> getFormat() {
        return format;
    }

    @Override
    public Optional<String> getMIMEType() {
        return mimeType;
    }
}
