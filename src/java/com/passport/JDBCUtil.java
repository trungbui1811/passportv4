/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import oracle.sql.TIMESTAMP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author TrungBH
 */
public class JDBCUtil {
    private static final Log logger = LogFactory.getLog(JDBCUtil.class);

    public static String toXML(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        StringBuilder xml = new StringBuilder();
        xml.append("<Results>");
        while (rs.next()) {
            xml.append("<Row>");
            for (int i = 1; i <= colCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Object value = rs.getObject(i);
                xml.append("<").append(columnName).append(">");
                if (value != null)
                    xml.append(value.toString().trim()); 
                xml.append("</").append(columnName).append(">");
            } 
            xml.append("</Row>");
        } 
        xml.append("</Results>");
        return xml.toString();
    }

    public static Document toDocument(ResultSet rs) throws ParserConfigurationException, SQLException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element results = doc.createElement("Results");
        doc.appendChild(results);
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        while (rs.next()) {
            Element row = doc.createElement("Row");
            results.appendChild(row);
            for (int i = 1; i <= colCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Object value = rs.getObject(i);
                Element node = doc.createElement(columnName);
                node.appendChild(doc.createTextNode(value.toString()));
                row.appendChild(node);
            } 
        } 
        return doc;
    }

    public static Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element results = doc.createElement("Results");
        doc.appendChild(results);
        return doc;
    }

    public static Document add2Document1(ResultSet rs1, ResultSet rs2, Document doc, String rsName) throws ParserConfigurationException, SQLException {
        if (rs1 == null && rs2 == null)
            return doc; 
        Element root = doc.getDocumentElement();
        Element rsElement = doc.createElement(rsName);
        root.appendChild(rsElement);
        if (rs1 != null) {
            ResultSetMetaData rsmd = rs1.getMetaData();
            int colCount = rsmd.getColumnCount();
            while (rs1.next()) {
                Element row = doc.createElement("Row");
                rsElement.appendChild(row);
                try {
                    for (int i = 1; i <= colCount; i++) {
                        String columnName = rsmd.getColumnName(i);
                        Object value = rs1.getObject(i);
                        if (value == null)
                            value = ""; 
                        Element node = doc.createElement(columnName);
                        node.appendChild(doc.createTextNode(value.toString()));
                        row.appendChild(node);
                    } 
                } catch (Exception e) {
                    logger.error(e, e);
                } 
            } 
        } 
        if (rs2 != null) {
            ResultSetMetaData rsmd = rs2.getMetaData();
            int colCount = rsmd.getColumnCount();
            while (rs2.next()) {
                Element row = doc.createElement("Row");
                rsElement.appendChild(row);
                try {
                    for (int i = 1; i <= colCount; i++) {
                        String columnName = rsmd.getColumnName(i);
                        Object value = rs2.getObject(i);
                        if (value == null)
                            value = ""; 
                        Element node = doc.createElement(columnName);
                        node.appendChild(doc.createTextNode(value.toString()));
                        row.appendChild(node);
                    } 
                } catch (Exception e) {
                    logger.error(e, e);
                } 
            } 
        } 
        return doc;
    }

    public static Document add2Document(ResultSet rs, Document doc, String rsName) throws ParserConfigurationException, SQLException {
        if (rs == null)
            return doc; 
        Element root = doc.getDocumentElement();
        Element rsElement = doc.createElement(rsName);
        root.appendChild(rsElement);
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
        while (rs.next()) {
            Element row = doc.createElement("Row");
            rsElement.appendChild(row);
            try {
                for (int i = 1; i <= colCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    Object value = rs.getObject(i);
                    if (value != null) {
                        if (value instanceof TIMESTAMP) {
                            TIMESTAMP tmp = (TIMESTAMP)value;
                            value = fm.format(tmp.dateValue());
                        } 
                        if (value == null)
                            value = ""; 
                        Element node = doc.createElement(columnName);
                        node.appendChild(doc.createTextNode(value.toString()));
                        row.appendChild(node);
                    } 
                } 
            } catch (Exception e) {
                logger.error(e, e);
            } 
        } 
        return doc;
    }

    public static String serialize(Document doc) throws IOException {
        StringWriter writer = new StringWriter();
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(writer, format);
        serializer.serialize(doc);
        return writer.getBuffer().toString();
    }

    public static Document toDoc(ResultSet rs) throws SQLException, ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        String xml = toXML(rs);
        StringReader reader = new StringReader(xml);
        InputSource source = new InputSource(reader);
        return builder.parse(source);
    }
}
