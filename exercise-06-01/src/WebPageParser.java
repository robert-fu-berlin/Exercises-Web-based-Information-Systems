import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;


public class WebPageParser {

	private RandomAccessFile page;
	public String title = "";
	public Map<String, String> imagesAndCaptions;
	public String content = "";
	
	public WebPageParser(File page) throws FileNotFoundException {
		this.page = new RandomAccessFile(page, "r");
		
		imagesAndCaptions = new HashMap<String, String>();
	}
	
	public void analyze() {
		String line;
		try {
			//read whole page
			while ((line = page.readLine()) != null) {
				
				//ignore upper case for searching of tags
				String lowerLine = line.toLowerCase();
				
				//extract title
				if (lowerLine.contains("<title>"))
					title = line.substring(lowerLine.indexOf("<title>") + "<title>".length(), lowerLine.indexOf("</title>"));
				
				//extract image information
				if (lowerLine.contains("<img")) {
					String img = line.substring(lowerLine.indexOf("<img") + "<img".length());
					img = img.substring(1, img.indexOf(">"));
					
					//split attributes
					String[] imgAttr = img.split("\" ");
					String src = "";
					String caption = "";
					for (String s : imgAttr) {
						//extract image source path
						if (s.contains("src="))
							src = extract(s);
						//extract title information
						if (s.contains("title="))
							caption = extract(s);
						//if there is no title use alt-tag content instead
						if (s.contains("alt=") && "".equals(caption))
							caption = extract(s);
					}
					imagesAndCaptions.put(src, caption);
				}
				content += line;
			}
		} catch (IOException e) {
			System.err.println("Error reading " + page);
			e.printStackTrace();
		}
		try {
			page.close();
		} catch (IOException e) {
			System.err.println("Error closing " + page);
			e.printStackTrace();
		}
	}
	
	/**
	 * Extracts the value from html-tag attributes.
	 * E.g. input is: src="exapmle/fun.txt"
	 * output would be: example/fun.txt
	 * @param s An attribute
	 * @return Value of the given attribute
	 */
	private String extract(String s) {
		if ("".equals(s))
			return "";
		String ret = s.substring(s.indexOf("=")+1);
		//remove leading quotes
		if (ret.indexOf("\"") == 0)
			ret = ret.substring(1);
		//remove ending quotes
		if (ret.indexOf("\"") != -1 && ret.indexOf("\"") == ret.length()-1)
			ret = ret.substring(0,ret.length()-1);
		return ret;
	}
}
