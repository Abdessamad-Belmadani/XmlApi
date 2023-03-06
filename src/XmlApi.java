import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.List;

public class XmlApi<T> {
	

    private final Class<T> clazz;
    private final String rootElementName;
    private final Field[] fieldNames;

    

    public XmlApi(Class<T> clazz) throws SecurityException, ClassNotFoundException {
        this.clazz = clazz;
        this.rootElementName = capitalize(clazz.getSimpleName())+"s";
        this.fieldNames = Class.forName(clazz.getName()).getDeclaredFields();
    }
    
    
    public void save(List<T> data, String filePath) throws  ParserConfigurationException, IOException, TransformerException  {
    	 DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

         // Create the root element
         Document doc = docBuilder.newDocument();
         Element rootElement = doc.createElement(rootElementName);
         doc.appendChild(rootElement);

         for (T obj : data) {
             // Create the child element
        	 //getting child name from parent (Persons=>Person)
             Element childElement = doc.createElement(rootElementName.substring(0, rootElementName.length() - 1));
             
             rootElement.appendChild(childElement);

             // Add the object's fields as child elements
             for (Field fieldName : fieldNames) {
            	 
            	 if(fieldName.getName().contains("$")) {
            		 continue;
            	 }
            	 System.out.println(fieldName.getName());
                 Element fieldElement = doc.createElement(fieldName.getName());
                 try {
                     fieldElement.setTextContent(obj.getClass().getMethod("get" + capitalize(fieldName.getName())).invoke(obj).toString());
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
                 childElement.appendChild(fieldElement);
             }
         }

         // Write the document to a file
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(doc);
         FileOutputStream fos = new FileOutputStream(new File(filePath));
         StreamResult result = new StreamResult(fos);
         transformer.transform(source, result);
         fos.close();
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public List<T> load( Class<T> clazz,String filePath) {
        List<T> objects = new Vector<>();
        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            Element root = document.getDocumentElement();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    T object = clazz.getDeclaredConstructor(new Class<?>[0]).newInstance();
                    Element element = (Element) node;
                    // TODO: parse the element and set object properties accordingly
                    for (Field field : clazz.getDeclaredFields()) {
                        String fieldName = field.getName();
                        String fieldValue = element.getAttribute(fieldName);
                        if (!fieldValue.isEmpty()) {
                            String setterName = "set" + capitalize(fieldName);
                            Method setter = clazz.getMethod(setterName, field.getType());
                            Object parsedValue = parseValue(field.getType(), fieldValue);
                            setter.invoke(object, parsedValue);
                        }
                    }
                    objects.add(object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    private Object parseValue(Class<?> type, String value) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == String.class) {
            return value;
        } else {
            // TODO: add support for other types if needed
            return null;
        }
    }

    

}