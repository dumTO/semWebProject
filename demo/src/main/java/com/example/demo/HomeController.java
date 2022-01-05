package com.example.demo;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
//import org.apache.jena.rdf.model.Property;
//import org.apache.jena.rdf.model.RDFNode;
//import org.apache.jena.rdf.model.Resource;
//import org.apache.jena.rdf.model.Statement;
//import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
//import org.apache.jena.riot.Lang;
//import org.apache.jena.riot.RDFDataMgr;
//import org.apache.jena.vocabulary.VCARD;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD;
import java.lang.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.opencsv.CSVReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.awt.*;
import java.awt.event.*;







@Controller
@RestController
@CrossOrigin
public class HomeController {
    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/reload")
    public RedirectView ReloadPage(org.springframework.ui.Model model) throws IOException, ParseException {

        
        
        
        model.addAttribute("appName", appName);
        String url = "https://territoire.emse.fr/kg/";
        JsoupGet(url);
        CSVReader reader = new CSVReader(new FileReader("20211116-daily-sensor-measures.csv"), ',' , '"' , 1);
                        
            //Read CSV line by line and use the string array as you want
            String[] nextLine;
            String urldebut="https://territoire.emse.fr/kg/emse/fayol/";
            Model modelSalle = ModelFactory.createDefaultModel();
            Model modelTemperature = ModelFactory.createDefaultModel();
            Model modelBaseTemperature = ModelFactory.createDefaultModel();
            String urlFinal;
            String datasetURL = "http://localhost:3030/dataset10";
            String sparqlEndpoint = datasetURL + "/sparql";
            String sparqlUpdate = datasetURL + "/update";
            String graphStore = datasetURL + "/data";

            RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
            Property name;
            String localName = "";
            StringWriter test = new StringWriter();
                        String test2;
                        PrintWriter out = null;
                        
            Writer output = null;
            Float essai;
            String[] arr;
            FileWriter fw = null;
            modelSalle = ModelFactory.createDefaultModel();
            modelTemperature = ModelFactory.createDefaultModel();
            modelBaseTemperature = ModelFactory.createDefaultModel();


            modelBaseTemperature.createResource(urldebut)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExtProp"), modelBaseTemperature.createResource(urldebut+"TemperatureExt"));

            conneg.load(modelBaseTemperature); // add the content of model to the triplestore

            conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
            int i=0;
            Document doc = Jsoup.connect("https://www.meteociel.fr/temps-reel/obs_villes.php?code2=7475&jour2=16&mois2=10&annee2=2021").get();
                Elements rows = doc.select("tr");
                String retourColonneModel="";
                i=0;
                Float[] table = new Float[24];
                for(Element row :rows){

                    Elements columns = row.select("td");
                    if(row.text().startsWith("23 h")){
                        String sDate1="16.11.2021 23:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");

                                convertDate("16.11.2021 arr[5]:00:00.0000000");
                                
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))
                                        
                                        );
                                        table[23]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));

                                        i=i+1;
                                        retourColonneModel ="";



                    }
                    if(row.text().startsWith("22 h")){
                        String sDate1="16.11.2021 22:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[22]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("21 h")){
                        String sDate1="16.11.2021 21:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[21]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("20 h")){
                        String sDate1="16.11.2021 20:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[20]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("19 h")){
                        String sDate1="16.11.2021 19:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[19]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("18 h")){
                        String sDate1="16.11.2021 28:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[18]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("17 h")){
                        String sDate1="16.11.2021 17:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[17]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("16 h")){
                        String sDate1="16.11.2021 16:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[16]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("15 h")){
                        String sDate1="16.11.2021 15:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[15]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("14 h")){
                        String sDate1="16.11.2021 14:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[14]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("13 h")){
                        String sDate1="16.11.2021 13:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[13]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("12 h")){
                        String sDate1="16.11.2021 12:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[12]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("11 h")){
                        String sDate1="16.11.2021 11:00:00:0000000";   
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[11]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("10 h")){
                        String sDate1="16.11.2021 10:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[10]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("9 h")){
                        String sDate1="16.11.2021 09:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[9]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("8 h")){
                        String sDate1="16.11.2021 08:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[8]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("7 h")){
                        String sDate1="16.11.2021 07:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[7]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("6 h")){
                        String sDate1="16.11.2021 06:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[6]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("5 h")){
                        String sDate1="16.11.2021 05:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[5]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("4 h")){
                        String sDate1="16.11.2021 04:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[4]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("3 h")){
                        String sDate1="16.11.2021 03:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[3]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("2 h")){
                        String sDate1="16.11.2021 02:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[2]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    if(row.text().startsWith("1 h")){
                        String sDate1="16.11.2021 01:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[1]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }if(row.text().startsWith("0 h")){
                        String sDate1="16.11.2021 00:00:00:0000000";  
                        Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS").parse(sDate1);  
                        Instant timestamp = date1.toInstant();
                        System.out.println("" + timestamp.getEpochSecond() + timestamp.getNano());
                        for (Element column:columns){
                            retourColonneModel = retourColonneModel + column.text() +"/";
                            System.out.println("hello "+column.text());
                        }
                            arr = retourColonneModel.split("/");
                                modelTemperature.createResource(urldebut+"TemperatureExt")
                                    .addProperty((ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink")),
                                    modelTemperature.createResource(urldebut+"TemperatureExt"+"/datalink/data/"+i)
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(arr[5]))
                                        .addProperty(ResourceFactory.createProperty(urldebut+"TemperatureExt"+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(timestamp.getEpochSecond() + timestamp.getNano())))                                        
                                        );
                                        table[0]=Float.parseFloat(arr[5].substring(0,arr[5].length()-3));
                                        i=i+1;
                                        retourColonneModel ="";
                    }
                    conneg.load(modelTemperature); // add the content of model to the triplestore

                        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
                    
                    //if(row.text())
                    
                }
                
                i=0;
                String retourTemp = "no data";
            while ((nextLine = reader.readNext()) != null) {


            if (nextLine != null) {
                //Verifying the read data here
                if(!nextLine[7].isEmpty()){

                    arr = nextLine[9].split("/"); 

                        essai = Float.parseFloat(nextLine[7]);
                        if(arr.length>3){
                            i=i+1;
                            Long.valueOf(nextLine[1]);
                            long microseconds = Long.valueOf(nextLine[1]) / 1000;
                            long milliSeconds = microseconds / 1000;
                            
                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(milliSeconds);
                            formatter.format(calendar.getTime());
                            String test1 = ""+formatter.format(calendar.getTime());
                            
                            if( Math.abs(Float.valueOf(table[Integer.parseInt(test1.substring(11, 13))]) - Float.valueOf(nextLine[7])) < 7 ){
                                retourTemp="normal";
                            }
                            else{
                                if( Math.abs(Float.valueOf(table[Integer.parseInt(test1.substring(11, 13))]) - Float.valueOf(nextLine[7])) > 7 && Math.abs(Float.valueOf(table[Integer.parseInt(test1.substring(11, 13))]) - Float.valueOf(nextLine[7])) < 14){
                                    retourTemp="interest";
                                }else{
                                        retourTemp="alarming";

                                    }
                                    
                                }
                            
                            modelSalle.createResource(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1))
                                        .addProperty((ResourceFactory.createProperty(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)+"/datalink")),
                                        modelSalle.createResource(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)+"/datalink/data/"+i)
                                            .addProperty(ResourceFactory.createProperty(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)+"/datalink/data/temp"), ResourceFactory.createTypedLiteral(Float.valueOf(nextLine[7])))
                                            .addProperty(ResourceFactory.createProperty(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)+"/datalink/data/date"), ResourceFactory.createTypedLiteral(Long.valueOf(nextLine[1])))
                                            .addProperty(ResourceFactory.createProperty(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)+"/datalink/data/ext"), ResourceFactory.createTypedLiteral(Float.valueOf(table[Integer.parseInt(test1.substring(11, 13))])))
                                            .addProperty(ResourceFactory.createProperty(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)+"/datalink/data/state"), ResourceFactory.createTypedLiteral(retourTemp))
                                            );
                            

                        // modelSalle.createResource(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)).addProperty(ResourceFactory.createProperty(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)+"/temperature/"+i),ResourceFactory.createTypedLiteral(new Float(nextLine[7])))
                        // .addProperty(ResourceFactory.createProperty(urldebut+arr[2].substring(1)+"ET"+"/"+arr[3].substring(1)+"/date/"+i), ResourceFactory.createTypedLiteral(new Float(nextLine[1])));


                        }

                }
                

            }
            }
            /*
            RDFDataMgr.write(test, modelSalle, Lang.NT);
                        //System.out.println(modelSalle);
                        
                        try {
                            File file = new File("test.nt");
                            fw=new FileWriter(file);
                            output = new BufferedWriter(fw);  //clears file every time
                            out = new PrintWriter(output);
                            System.out.println(test.toString());
                            out.println(test.toString());
                            //output.append(test.toString());
                            output.close();
                            out.close();
                            
                            //FileWriter fileWriter = new FileWriter(file);
                            //fileWriter.write(test.toString());
                            //fileWriter.flush();
                            //fileWriter.close();

                        } catch (IOException e) {
                        
                            e.printStackTrace();
                        }



                        finally {
                            if(out != null)
                                out.close();
                            try {
                                if(output != null)
                                    output.close();
                            } catch (IOException e) {
                                //exception handling left as an exercise for the reader
                            }
                            try {
                                if(fw != null)
                                    fw.close();
                            } catch (IOException e) {
                                //exception handling left as an exercise for the reader
                            }
                        }*/



                        /*coomenter ou decommenter selon si on veux charger notre nt*/
                        //Model model = ModelFactory.createDefaultModel();
                        //model.read("C:/Users/Users.DESKTOP-SOJULLM/Documents/M2-DSC/sem_web/SemanticWebProject/test.nt");
                        //System.out.println(model.toString());

                        conneg.load(modelSalle); // add the content of model to the triplestore

                        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore

                        

                        /*"<https://territoire.emse.fr/kg/emse/fayol/4ET/431F>"+ "\n" +*/

                        //1. Create the frame.



                //ResultSetFormatter.out(System.out, results1, query) ;
            
                        


                

            System.out.println( "Hello World!" );
            
            return new RedirectView("/");
    }

    @GetMapping("/")
    public String homePage(org.springframework.ui.Model model) throws IOException {
        System.out.println( "Hello World!" ); 
        

        

        model.addAttribute("appName", appName);
        
                    
                


            
            String retourHTML = "<html>\n" + "<header><title>Welcome</title></header>\n"+ "<body>\n";
            List<String> test34 = QueryFusekiPredicate("<https://territoire.emse.fr/kg/emse/fayol/>");

            //List<String> test43 = QueryFusekiPredicate(test34.get(0));
            //List<String> test44 = test43;
            //System.out.println(test43);
            //test34.remove(0);
            //test34.addAll(test43);
            while(!test34.isEmpty()){
            //System.out.println("test intron"+test34.get(0));

            //System.out.println( "new loop "+test34.get(0)); 
            //test43 = QueryFusekiPredicate(test34.get(0));
            if(test34.get(0).contains("https://")){
                retourHTML = retourHTML + "<a href='http://localhost:8085/" + test34.get(0).substring(9, test34.get(0).length()-1) + "'>" + test34.get(0).substring(1, test34.get(0).length()-1) + "</a></br>\n";
            }else{
                retourHTML = retourHTML + test34.get(0).substring(1, test34.get(0).length()-1) + "</br>\n";
            }
            
            test34.remove(0);
            //test44 = test43;
            //test34.addAll(test43);
            //
            }
            retourHTML = retourHTML + "</body>\n" + "</html>";
            return retourHTML;
        

    }

    public static void JsoupGet(String url) throws IOException{
    


        Document document = Jsoup.connect(url).get();
        Elements links = document.select("a[href]");
        
        for (Element link : links) {
    
                if(link.text().contains(".nt")){
                    Model model = ModelFactory.createDefaultModel();
                    model.read(url+link.text());
                    String datasetURL = "http://localhost:3030/dataset10";
                    String sparqlEndpoint = datasetURL + "/sparql";
                    String sparqlUpdate = datasetURL + "/update";
                    String graphStore = datasetURL + "/data";
                    RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
                    conneg.load(model); // add the content of model to the triplestore
                    conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
                }
        }
        for (Element link : links) {
            if(link.text().contains("/")){
                String url2 = url+link.text();
                JsoupGet(url2);
            }
        }
    }
    
    
    public static List<String> QueryFusekiPredicate(String url) throws IOException{
        List<String> retour = new ArrayList<String>();
        String s2 = " PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>" + "\n" +
        "SELECT ?subject ?predicate ?object "+"\n"+"WHERE { " +"\n" +
        url+ "?predicate ?object" + "\n" +
        "}" + "\n" + 
        "LIMIT 1000" ;
        
        Query query = QueryFactory.create(s2); //s2 = the query above
        //QueryExecution qExe = QueryExecutionFactory.sparqlService("http://localhost:3030/dataset/sparql", query );
    
        //ResultSet results1 = results.rewindable();
        QueryEngineHTTP qexec = new QueryEngineHTTP("http://localhost:3030/dataset10/sparql", query );
    
        ResultSet results = qexec.execSelect();  
        java.util.List result = ResultSetFormatter.toList(results);
        //System.out.println("hello retour "+results);
        for(int j = 0 ;j<result.size();j++){
            
            if(result.get(j).toString().split(" ")[3].equals("<https://w3id.org/bot#hasStorey>")){

                retour.add(result.get(j).toString().split(" ")[8]);
            }
            
        if(result.get(j).toString().split(" ")[3].equals("<https://w3id.org/bot#hasSpace>")){
            retour.add(result.get(j).toString().split(" ")[8]);
        }
        if(result.get(j).toString().split(" ")[3].equals("<https://territoire.emse.fr/kg/emse/fayol/TemperatureExtProp>")){
            retour.add(result.get(j).toString().split(" ")[8]);
        }
    
        
        if(result.get(j).toString().split(" ")[3].contains("datalink")){
            //System.out.println("hello retour ");
                
                if(result.get(j).toString().split(" ")[3].contains("date")){
                    System.out.println("hello retour ici"+result.get(j).toString().split("=")[2].substring(0, result.get(j).toString().split("=")[2].length()-2)+" ");
                    retour.add(" date : "+result.get(j).toString().split("=")[2].substring(2, result.get(j).toString().split("=")[2].length()-13)+" ");
                }
                else{
                    if(result.get(j).toString().split(" ")[3].contains("temp")){
                        retour.add(" temperature : "+result.get(j).toString().split("\"")[1]+" ");
                    }else{
                        if(result.get(j).toString().split(" ")[3].contains("ext")){
                            System.out.println("hello retour ici"+result.get(j).toString().split("=")[2].substring(0, result.get(j).toString().split("=")[2].length()-1)+" ");
                            retour.add(" temperature extérieur : "+result.get(j).toString().split("=")[2].substring(2, result.get(j).toString().split("=")[2].length()-14)+" ");
                        }else{
                            retour.add(result.get(j).toString().split(" ")[8]);
                        }
                        
                    }
                }
                
                
    
            //<https://territoire.emse.fr/kg/emse/fayol/4ET/431H/datalink>
            //System.out.println("hello retour 2"+ouesh.get(j).toString().split(" ")[3]);
            //System.out.println("hello retour 2"+ouesh.get(j).toString().split(" ")[8]);
        }

        if(result.get(j).toString().split(" ")[3].contains("comment")){
            //System.out.println("hello retour ");
                //System.out.println("hello retour ici"+ouesh.get(j).toString().split(" ")[3]);
                //retour.add(ouesh.get(j).toString().split(" ")[8]);
                retour.add(" "+result.get(j).toString().split("\"")[1]+" ");
            //<https://territoire.emse.fr/kg/emse/fayol/4ET/431H/datalink>
            //System.out.println("hello retour 2"+ouesh.get(j).toString().split(" ")[3]);
            //System.out.println("hello retour 2"+ouesh.get(j).toString().split(" ")[8]);
        }

        if(result.get(j).toString().split(" ")[3].contains("label")){
            //System.out.println("hello retour ");
                
                System.out.println("hello retour ici "+result.get(j).toString().split("\"")[1]);
                
                retour.add(" "+result.get(j).toString().split("\"")[1]+" ");
    
            //<https://territoire.emse.fr/kg/emse/fayol/4ET/431H/datalink>
            //System.out.println("hello retour 2"+ouesh.get(j).toString().split(" ")[3]);
            //System.out.println("hello retour 2"+ouesh.get(j).toString().split(" ")[8]);
        }
        
    
    
    
    }
        qexec.close();
        return retour;
    
    }

    @GetMapping("/**")
    public String searchPage(org.springframework.ui.Model model, HttpServletRequest request) throws IOException, ParseException {
        
      
        request.getRequestURL().toString();
        String retourHTML = "<html>\n" + "<header><title>Welcome</title></header>\n"+ "<body>\n";
            List<String> test34 = QueryFusekiPredicate("<https://" + request.getRequestURL().toString().substring(22, request.getRequestURL().toString().length()) + ">");
            System.out.println("test intron "+request.getRequestURL().toString().substring(22, request.getRequestURL().toString().length()));

            //List<String> test43 = QueryFusekiPredicate(test34.get(0));
            //List<String> test44 = test43;
            //System.out.println(test43);
            //test34.remove(0);
            //test34.addAll(test43);
            while(!test34.isEmpty()){
            System.out.println("test intron"+test34.get(0));

            //System.out.println( "new loop "+test34.get(0)); 
            //test43 = QueryFusekiPredicate(test34.get(0));
            if(test34.get(0).contains("https://")){
                retourHTML = retourHTML + "<a href='http://localhost:8085/" + test34.get(0).substring(9, test34.get(0).length()-1) + "'>" + test34.get(0).substring(1, test34.get(0).length()-1) + "</a></br>\n";
            }else{
                retourHTML = retourHTML + test34.get(0).substring(1, test34.get(0).length()-1) + "</br>\n";
            }
            
            test34.remove(0);
            //test44 = test43;
            //test34.addAll(test43);
            //
            }
            retourHTML = retourHTML + "</body>\n" + "</html>";
            return retourHTML;
    }
    private String convertDate(String cdate)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSSSSS");
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate;
        try
        {
            convertedDate = dateFormat.parse(cdate);
            cdate = postFormater.format(convertedDate);
        }
        catch (ParseException e)
        {
           
        }
        return cdate;
    }
    
    

}
/*String url2 = "https://www.w3.org/ns/sosa/";
                Model model = ModelFactory.createDefaultModel();
                            model.read(url2);
                            String datasetURL = "http://localhost:3030/ds";
                            String sparqlEndpoint = datasetURL + "/sparql";
                            String sparqlUpdate = datasetURL + "/update";
                            String graphStore = datasetURL + "/data";
                            RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
                            conneg.load(model); // add the content of model to the triplestore
                            conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
            */

            /*
                url2 = "https://w3c-lbd-cg.github.io/bot/bot.ttl";
                model = ModelFactory.createDefaultModel();
                    model.read(url2);
                    datasetURL = "http://localhost:3030/ds";
                    sparqlEndpoint = datasetURL + "/sparql";
                    sparqlUpdate = datasetURL + "/update";
                    graphStore = datasetURL + "/data";
                    conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
                    conneg.load(model); // add the content of model to the triplestore
                    conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore

            */
                    /*
                    url2 = "https://doc.realestatecore.io/3.3/asset.rdf";
                    model = ModelFactory.createDefaultModel();
                        model.read(url2);
                        datasetURL = "http://localhost:3030/ds";
                        sparqlEndpoint = datasetURL + "/sparql";
                        sparqlUpdate = datasetURL + "/update";
                        graphStore = datasetURL + "/data";
                        conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
                        conneg.load(model); // add the content of model to the triplestore
                        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
            */


