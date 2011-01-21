import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.demo.HTMLDocument;
import org.apache.lucene.demo.html.HTMLParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;


public class Indexer {

	private IndexWriter fileWriter;
	private IndexWriter imageWriter;
	
	public Indexer(String fileIndexerDir, String imageIndexerDir) {
		
		//create indizes
		File fileIndexerFile = new File(fileIndexerDir);
		File imageIndexerFile = new File(imageIndexerDir);
		FSDirectory fileDirectory;
		FSDirectory imageDirectory;
		try {
			fileDirectory = FSDirectory.open(fileIndexerFile);
		} catch (IOException e) {
			System.err.println("Unable to open " + fileIndexerFile + "!");
			e.printStackTrace();
			return;
		}
		
		try {
			imageDirectory = FSDirectory.open(imageIndexerFile);
		} catch (IOException e) {
			System.err.println("Unable to open " + imageIndexerFile + "!");
			e.printStackTrace();
			return;
		}
		
		try {
			this.fileWriter = new IndexWriter(fileDirectory, new StandardAnalyzer(Version.LUCENE_29),true, IndexWriter.MaxFieldLength.LIMITED);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.imageWriter = new IndexWriter(imageDirectory, new StandardAnalyzer(Version.LUCENE_29),true, IndexWriter.MaxFieldLength.LIMITED);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void indexFiles(Set<File> files) {
		
		for (File f : files)
			indexFile(f);
		
		try {
			fileWriter.optimize();
			fileWriter.close();
			imageWriter.optimize();
			imageWriter.close();
		} catch (CorruptIndexException e) {
			System.err.println("Error writing and closing image or file index");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error writing and closing image or file index");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a Html file to the file index and 
	 * all it's images to the image index.
	 * @param file Html file to index
	 */
	private void indexFile(File file) {
		
		Document document = new Document();
		
		try {
			WebPageParser parser = new WebPageParser(file);
			String fileName = file.getName().toLowerCase();
			if (fileName.endsWith(".html") || fileName.endsWith(".htm") || fileName.endsWith(".xhtml")) {
				parser.analyze();
				document.add(new Field("path", file.getAbsolutePath(), Field.Store.YES, Field.Index.NO));
				document.add(new Field("title", parser.title, Field.Store.YES, Field.Index.ANALYZED));
				document.add(new Field("content", parser.content, Field.Store.NO, Field.Index.ANALYZED));

				//index images included by the webpage
				for (Entry<String, String> e : parser.imagesAndCaptions.entrySet()) {
					Document imageDoc = new Document();
					imageDoc.add(new Field("path", e.getKey(), Field.Store.YES, Field.Index.NO));
					imageDoc.add(new Field("caption", e.getValue(), Field.Store.YES, Field.Index.ANALYZED));
					imageWriter.addDocument(imageDoc);
					System.out.println(e.getKey() + " was added to image index");
				}
				fileWriter.addDocument(document);
				System.out.println(file + " was added to file index");
			} else
				System.out.println("File " + file + " has wrong type (should be html)");
		} catch (CorruptIndexException e) {
			System.err.println("Can't add " + file);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Can't add " + file);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: java Indexer <fileIndexName> <imageIndexName> file1.html file2.html ...");
			return;
		}
		
		Set<File> files = new HashSet<File>();
		
		for (int i = 2; i < args.length; i++)
			files.add(new File(args[i]));
		
		Indexer indexer = new Indexer(args[0], args[1]);
		indexer.indexFiles(files);
	}
}
