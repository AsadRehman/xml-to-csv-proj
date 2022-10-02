package xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** * * Java program to compare two XML files using XMLUnit example * @author Javin Paul */ 
public class XMLtoCSV {

    public static void main(String args[]) throws Exception {

    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document sourceDoc = db.parse("source.xml");
        Document targetDoc = db.parse("target.xml");
        String source = docToString(sourceDoc);
        String target = docToString(targetDoc);
        getCSV(source,target);
    }
    
    private static String docToString(Document sourceDoc) throws TransformerException {
    	DOMSource domSource = new DOMSource(sourceDoc);
    	StringWriter writer = new StringWriter();
    	StreamResult result = new StreamResult(writer);
    	TransformerFactory tf = TransformerFactory.newInstance();
    	Transformer transformer = tf.newTransformer();
    	transformer.transform(domSource, result);
    	return writer.toString();
	}

    
	public static void getCSV(String source, String target) {
    	File file = new File("output.csv");
    	PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        String headerList = "Invoice Number,"
        		+ "Document Code(From XML1), "
        		+ "Document Code(From XML2),"
        		+ "Document Date(From XML1),"
        		+ "Document Date(From XML2),"
        		+ "Document Status(From XML 1),"
        		+ "Document Status(From XML2),"
        		+ "Total Amount(From XML1),"
        		+ "Total Amount(From XML2),"
        		+ "Total Taxable(From XML1),"
        		+ "Total Taxable(From XML2),"
        		+ "Total Tax(From XML1),"
        		+ "Total Tax(From XML2),"
        		+ "Messages";
        builder.append(headerList +"\n");

        Document sourceDoc = strToDoc(source);
        Document targetDoc = strToDoc(target);
        
    	Element docEleSource = sourceDoc.getDocumentElement();
    	Element docEleTarget = targetDoc.getDocumentElement();
    	
        NodeList nlSource = docEleSource.getChildNodes();
        NodeList nlTarget = docEleTarget.getChildNodes();
        
        int lengthSource = nlSource.getLength();
        int lengthTarget = nlTarget.getLength();
       
        
        String[] allSource = new String[lengthSource+2];
        String[] allTarget = new String[lengthTarget+2];
        String message = "";
        
        List<String> src = new ArrayList<>();
        List<String> trgts = new ArrayList<>();
        
        int invoiceNumber = 1;
        
        for (int i = 0; i < lengthSource; i++) {
            if (nlSource.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nlSource.item(i);
                if (el.getNodeName().contains("row")) {
                	allSource[0] = invoiceNumber + "";
                    allSource[1] = el.getElementsByTagName("DocCode").item(0).getTextContent();
                    allSource[2] = el.getElementsByTagName("DocDate").item(0).getTextContent();
                    allSource[3] = el.getElementsByTagName("DocStatus").item(0).getTextContent();
                    allSource[4] = el.getElementsByTagName("TotalAmount").item(0).getTextContent();
                    allSource[5] = el.getElementsByTagName("TotalTaxable").item(0).getTextContent();
                    allSource[6] = el.getElementsByTagName("TotalTax").item(0).getTextContent();
                    src.add(allSource[1]);
                }
            }
           
            for (int j = 0; j < lengthTarget; j++) {
            if (nlTarget.item(j).getNodeType() == Node.ELEMENT_NODE) {
                Element elR = (Element) nlTarget.item(j);
                trgts.add(elR.getElementsByTagName("DocCode").item(0).getTextContent());
                if (elR.getNodeName().contains("row") && elR.getElementsByTagName("DocCode").item(0).getTextContent().equals(allSource[1])) {
                	allTarget[0] =  "";
                	allTarget[1] = elR.getElementsByTagName("DocCode").item(0).getTextContent();
                	allTarget[2] = elR.getElementsByTagName("DocDate").item(0).getTextContent();
                	allTarget[3] = elR.getElementsByTagName("DocStatus").item(0).getTextContent();
                	allTarget[4] = elR.getElementsByTagName("TotalAmount").item(0).getTextContent();
                	allTarget[5] = elR.getElementsByTagName("TotalTaxable").item(0).getTextContent();
                	allTarget[6] = elR.getElementsByTagName("TotalTax").item(0).getTextContent();
                	break;
                }
              }
            
            }
            
            if(allSource[1] !=null && allTarget[1] !=null) {
            	if(allSource[1].equals(allTarget[1])) {
            		message += "DATA FOUND";
            	} else {
            	message += "DATA NOT FOUND";
            	}
            }
            if(allSource[1] !=null && allTarget[1] !=null) {
            	if(allSource[4] !=null &&  allSource[4].equals(allTarget[4])) {
            		message += " AMOUNT MATCHED ";
            	} else {
            		message += " AMOUNT NOT MATCHED ";
            	}
            }
            if((allSource[1] !=null && allTarget[1] !=null)) {
            builder.append(invoiceNumber + "," 
            		+ allSource[1] + ","
            		+ allTarget[1] + ","
            		+ allSource[2] + "," 
            		+ allTarget[2] + "," 
            		+ allSource[3] + ","
            		+ allTarget[3] + "," 
            		+ allSource[4] + "," 
            		+ allTarget[4] + "," 
            		+ allSource[5] + "," 
            		+ allTarget[5] + "," 
            		+ allSource[6] + "," 
            		+ allTarget[6] + ","
            		+ message +
            "\n"); 
            invoiceNumber++;
            } 
            
            message = "";
            allSource = new String[lengthSource+2];
            allTarget = new String[lengthTarget+2];
            
        }
        /*
        List<String> srclistWithoutDuplicates = new ArrayList<>(new HashSet<>(src));
        List<String> targetlistWithoutDuplicates = new ArrayList<>(new HashSet<>(trgts));
        List<String> union = new ArrayList<String>(srclistWithoutDuplicates);
        union.addAll(targetlistWithoutDuplicates);
        // Prepare an intersection
        List<String> intersection = new ArrayList<String>(srclistWithoutDuplicates);
        intersection.retainAll(targetlistWithoutDuplicates);
        // Subtract the intersection from the union
        union.removeAll(intersection);
        if(srclistWithoutDuplicates.size() > targetlistWithoutDuplicates.size()) {
        	targetlistWithoutDuplicates = new ArrayList<>();
        	srclistWithoutDuplicates = union;
        } else {
        	srclistWithoutDuplicates = new ArrayList<>();
        	targetlistWithoutDuplicates = union;

        }
        
        
        if(srclistWithoutDuplicates.size() > targetlistWithoutDuplicates.size()) {
        	for (int i = 0; i < lengthSource; i++) {
                if (nlSource.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nlSource.item(i);
                    if (srclistWithoutDuplicates.contains(el.getElementsByTagName("DocCode").item(0).getTextContent()) && el.getNodeName().contains("row")) {
                    	allSource[0] = "";
                        allSource[1] = el.getElementsByTagName("DocCode").item(0).getTextContent();
                        allSource[2] = el.getElementsByTagName("DocDate").item(0).getTextContent();
                        allSource[3] = el.getElementsByTagName("DocStatus").item(0).getTextContent();
                        allSource[4] = el.getElementsByTagName("TotalAmount").item(0).getTextContent();
                        allSource[5] = el.getElementsByTagName("TotalTaxable").item(0).getTextContent();
                        allSource[6] = el.getElementsByTagName("TotalTax").item(0).getTextContent();
                        builder.append(invoiceNumber + "," 
                        		+ allSource[1] + ","
                        		+ "-" + ","
                        		+ allSource[2] + "," 
                        		+ "-" + ","
                        		+ allSource[3] + ","
                        		+ "-" + "," 
                        		+ allSource[4] + "," 
                        		+ "-" + "," 
                        		+ allSource[5] + "," 
                        		+ "-" + ","
                        		+ allSource[6] + "," 
                        		+ "-" + ","
                        		+ "DATA NOT FOUND" +
                        "\n"); 
                        allSource = new String[lengthSource+2];
                        invoiceNumber++;
                    }
                }
        	}
        }
        
        if(srclistWithoutDuplicates.size() < targetlistWithoutDuplicates.size()) {
        	for (int j = 0; j < lengthTarget ; j++) {
        		if (nlTarget.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    Element elR = (Element) nlTarget.item(j);
                    if (targetlistWithoutDuplicates.contains(elR.getElementsByTagName("DocCode").item(0).getTextContent()) && elR.getNodeName().contains("row")) {
                    	allTarget[0] = invoiceNumber + "";
                    	allTarget[1] = elR.getElementsByTagName("DocCode").item(0).getTextContent();
                    	allTarget[2] = elR.getElementsByTagName("DocDate").item(0).getTextContent();
                    	allTarget[3] = elR.getElementsByTagName("DocStatus").item(0).getTextContent();
                    	allTarget[4] = elR.getElementsByTagName("TotalAmount").item(0).getTextContent();
                    	allTarget[5] = elR.getElementsByTagName("TotalTaxable").item(0).getTextContent();
                    	allTarget[6] = elR.getElementsByTagName("TotalTax").item(0).getTextContent();
                    	builder.append(invoiceNumber + "," 
                    			+ "-" + ","
                        		+ allTarget[1] + ","
                        		+ "-" + ","
                        		+ allTarget[2] + "," 
                        		+ "-" + ","
                        		+ allTarget[3] + "," 
                        		+ "-" + "," 
                        		+ allTarget[4] + "," 
                        		+ "-" + ","
                        		+ allTarget[5] + "," 
                        		+ "-" + "," 
                        		+ allTarget[6] + ","
                        		+ "DATA NOT FOUND" +
                        "\n"); 
                        allTarget = new String[lengthTarget+2];
                        invoiceNumber++;
                    }
                  }
        	}
        }*/
        pw.write(builder.toString());
        pw.close();
        System.out.println("data has been written!");
    }

	private static Document strToDoc(String source) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        Document doc = null;
        try 
        {  
            builder = factory.newDocumentBuilder();  
            doc = builder.parse( new InputSource( new StringReader( source )) ); 

        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return doc; 
	}
    
}


