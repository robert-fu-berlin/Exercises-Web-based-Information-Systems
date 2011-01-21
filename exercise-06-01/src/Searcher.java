import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Searcher {

	IndexSearcher fileSearcher = null;
	IndexSearcher imageSearcher = null;
	
	public Searcher(String fileIndexDir, String imageIndexDir) {
		
		if (fileIndexDir != null && !"".equals(fileIndexDir)) {
			//open file index
			FSDirectory fileIndDirectory;
			try {
				fileIndDirectory = FSDirectory.open(new File(fileIndexDir));
			} catch (IOException e) {
				System.err.println("Can't open " + fileIndexDir);
				e.printStackTrace();
				return;
			}
			try {
				fileSearcher = new IndexSearcher(fileIndDirectory, true);
			} catch (CorruptIndexException e) {
				System.err.println("Error index " + fileIndexDir + " is corrupted");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Error can't load " + fileIndexDir);
				e.printStackTrace();
			}
		}
		
		
		if (imageIndexDir != null && !"".equals(imageIndexDir)) {
			//open file index
			FSDirectory imageIndDirectory;
			try {
				imageIndDirectory = FSDirectory.open(new File(imageIndexDir));
			} catch (IOException e) {
				System.err.println("Can't open " + imageIndexDir);
				e.printStackTrace();
				return;
			}
			try {
				imageSearcher = new IndexSearcher(imageIndDirectory, true);
			} catch (CorruptIndexException e) {
				System.err.println("Error index " + imageIndexDir + " is corrupted");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Error can't load " + imageIndexDir);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Searches for queryString in the file index.
	 * @param queryString
	 * @return resulting documents or null if search fails
	 */
	public TopDocs fileQuery(String field, String queryString) {
		if (fileSearcher == null)
			throw new IllegalStateException("Error fileSearcher was not initialized");
		
		WildcardQuery query = new WildcardQuery(new Term(field, queryString));
		TopDocs results = null;
		try {
			results = fileSearcher.search(query, 10);
		} catch (IOException e) {
			System.err.println("Error searching for file");
			e.printStackTrace();
		}
		return results;
	}

	public TopDocs imageQuery(String field, String queryString) {
		
		if (imageSearcher == null)
			throw new IllegalStateException("Error imageSearcher was not initialized");
		
		WildcardQuery query = new WildcardQuery(new Term(field, queryString));
		TopDocs results = null;
		try {
			results = imageSearcher.search(query, 10);
		} catch (IOException e) {
			System.err.println("Error searching for image");
			e.printStackTrace();
		}
		return results;
	}
	
	public Document getFileDoc(int doc) {
		
		if (fileSearcher == null)
			throw new IllegalStateException("Error fileSearcher was not initialized");
		
		try {
			return fileSearcher.doc(doc);
		} catch (CorruptIndexException e) {
			System.err.println("Error file index is corrupted");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("Error reading file index");
			e.printStackTrace();
			return null;
		}
	}
	
	public Document getImageDoc(int doc) {
		
		if (imageSearcher == null)
			throw new IllegalStateException("Error imageSearcher was not initialized");
		
		try {
			return imageSearcher.doc(doc);
		} catch (CorruptIndexException e) {
			System.err.println("Error image index is corrupted");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("Error reading image index");
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: \n java Searcher -f <fileIndex> <searchTerm> \n or \n java Searcher -i <imageIndex> <searchTerm>");
			return;
		}
		
		//file search
		if ("-f".equals(args[0])) {
			Searcher searcher = new Searcher(args[1], null);
			TopDocs docs = searcher.fileQuery("content",args[2].toLowerCase());
			System.out.println("Searching for " + args[2] + " in file index...");
			System.out.println(docs.totalHits + (docs.totalHits == 1 ? " result\n": " results\n"));
			
			if (docs.totalHits == 0)
				return;
			
			ScoreDoc[] results = docs.scoreDocs;
			for (int i = 0; i < results.length; i++) {
				System.out.println(i+1 + ": " + searcher.getFileDoc(results[i].doc).getField("title").stringValue());
				System.out.println(searcher.getFileDoc(results[i].doc).getField("path").stringValue() + "\n");
			}
		}
		
		//image search
		if ("-i".equals(args[0])) {
			Searcher searcher = new Searcher(null, args[1]);
			TopDocs docs = searcher.imageQuery("caption", args[2].toLowerCase());
			System.out.println("Searching for " + args[2] + " in image index...");
			System.out.println(docs.totalHits + (docs.totalHits == 1 ? " result\n": " results\n"));
			
			if (docs.totalHits == 0)
				return;
			
			ScoreDoc[] results = docs.scoreDocs;
			for (int i = 0; i < results.length; i++) {
				System.out.println(i+1 + ": " + searcher.getImageDoc(results[i].doc).getField("caption").stringValue());
				System.out.println(searcher.getImageDoc(results[i].doc).getField("path").stringValue() + "\n");
			}
		}
	}
}
