/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aas;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Tongtong
 */
@Named(value = "reportController")
@RequestScoped
public class reportController {
    
    private StreamedContent content;

    /**
     * Creates a new instance of reportController
     */
    public reportController() {
    }

    @PostConstruct
    public void init() {
        System.out.println("reportController is started");
    }

    public void generateRevenueReport() {
        try {
            Connection connection;
            connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/mas", "root", "root");
            System.out.println("start to generate revenue report");
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/src/java/reports/revenueReport.jrxml");
                System.out.println("step 1 done");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), connection);
                System.out.println("step 2 done");
                String exportPath = "/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/revenue_report/RevenueReport.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                System.out.println("Report is generated");

            } catch (JRException ex) {
                Logger.getLogger(reportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(reportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
