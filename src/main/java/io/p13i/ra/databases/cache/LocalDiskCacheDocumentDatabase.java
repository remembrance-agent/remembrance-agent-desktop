package io.p13i.ra.databases.cache;

import io.p13i.ra.databases.DocumentDatabase;
import io.p13i.ra.databases.localdisk.LocalDiskDocument;
import io.p13i.ra.models.Document;
import io.p13i.ra.utils.FileIO;
import io.p13i.ra.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalDiskCacheDocumentDatabase implements DocumentDatabase, LocalDiskCache {

    private final String cacheLocalDirectory;
    private List<CachableDocument> cachableDocuments;
    private List<Document> documents;

    public LocalDiskCacheDocumentDatabase(String cacheLocalDirectory) {
        this.cacheLocalDirectory = cacheLocalDirectory;
    }

    @Override
    public String getName() {
        return LocalDiskCacheDocumentDatabase.class.getSimpleName() +
                "(" + StringUtils.truncateBeginningWithEllipse(this.cacheLocalDirectory, 20)+ ")";
    }

    @Override
    public void loadDocuments() {
        this.loadDocumentsFromCache();
    }

    @Override
    public void loadDocumentsFromCache() {
        this.documents = new ArrayList<>();
        for (String cachedFilePath : FileIO.listFiles(this.cacheLocalDirectory)) {
            String content = FileIO.read(cachedFilePath);
            Document document = new LocalDiskDocument(content, cachedFilePath, FileIO.getFileName(cachedFilePath), FileIO.getLastModifiedDate(cachedFilePath));
            this.documents.add(document);
        }
    }

    public void saveDocumentsToCache(List<CachableDocument> cachableDocuments) {
        // Delete all documents already in cache
        List<String> documentsInCache = FileIO.listFiles(this.cacheLocalDirectory);
        for (String documentPath : documentsInCache) {
            FileIO.delete(documentPath);
        }

        this.cachableDocuments = cachableDocuments;
        for (CachableDocument cachableDocument : cachableDocuments) {
            String cacheFileName = this.cacheLocalDirectory + File.separator + cachableDocument.getCacheHashCode() + ".txt";
            FileIO.write(cacheFileName, cachableDocument.getContent());
        }
    }

    @Override
    public List<Document> getAllDocuments() {
        return this.documents;
    }
}
