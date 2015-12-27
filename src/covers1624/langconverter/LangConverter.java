package covers1624.langconverter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

public class LangConverter {

	public static File curFolder = new File(System.getProperty("user.dir"));
	private static File outFolder;
	private static String outFileName;
	private static File inFile;
	private static boolean debug;

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			switch (arg) {
			case "-help":
			case "--help":
				System.out.println("Help for LangConverter:");
				System.out.println("How args work: <Required> [Optional]");
				System.out.println("-in <FileName> : Specifies the input file. it will only grab input files from the current directory.");
				System.out.println("All converted Language Localization files will be placed in ./out/");
				System.exit(0);
				break;
			case "-in":
				inFile = new File(curFolder, args[i + 1]);
				break;
			case "-debug":
				debug = true;
				break;
			}
		}

		if (inFile == null || !inFile.exists()) {
			System.err.println("Please specify a file that exists in the current directory with the Commandline Arg: -in <FileName> \n Use --help for more info.");
			System.exit(1);
		}
		outFolder = new File(curFolder, "out/");
		if (!outFolder.exists()) {
			if (!outFolder.mkdir()) {
				throw new IllegalAccessError("Unable to create folder /out/ in current directory. Please run with elevated privileges or move to a unprotected folder.");
			}
		}
		outFileName = inFile.getName().substring(0, inFile.getName().lastIndexOf(".")) + ".lang";
		LinkedList<LocalizationEntry> list = doConversion();
		for (LocalizationEntry entry : list) {
			System.out.println(String.format("Key: %s Value: %s", entry.unloc, entry.loc));
		}
		writeToFile(list);
	}

	private static LinkedList<LocalizationEntry> doConversion() {
		LinkedList<LocalizationEntry> list = new LinkedList<LocalizationEntry>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(inFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (document == null) {
			System.err.println("Invalid Document.");
			System.exit(1);
		}

		NodeList nodeList = document.getDocumentElement().getElementsByTagName("entry");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			LocalizationEntry entry = new LocalizationEntry();
			entry.unloc = element.getAttribute("key");
			entry.loc = element.getFirstChild().getNodeValue();
			list.addLast(entry);
		}
		return list;
	}

	private static void writeToFile(LinkedList<LocalizationEntry> list){
		try {
			PrintWriter writer = new PrintWriter(outFileName, "UTF-8");
			for (LocalizationEntry entry : list){
				writer.println(entry.unloc + "=" + entry.loc);
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class LocalizationEntry {
		public String unloc;
		public String loc;
	}
}
