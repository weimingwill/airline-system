/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean.aas;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import managedbean.application.AasNavController;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Tongtong
 */
@Named(value = "reportController")
@RequestScoped
public class reportController {

    @Inject
    AasNavController aasNavController;

    private StreamedContent content;
    private DashboardModel reportDashboard;
    private File file;

    /**
     * Creates a new instance of reportController
     */
    public reportController() {
    }

    @PostConstruct
    public void init() {
        System.out.println("reportController is started");
        createReportDashboard();
    }

    public void createReportDashboard() {

        reportDashboard = new DefaultDashboardModel();

        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        DashboardColumn column3 = new DefaultDashboardColumn();

        column1.addWidget("revenueReports");

        column2.addWidget("costReports");

        column3.addWidget("finReports");

        reportDashboard.addColumn(column1);
        reportDashboard.addColumn(column2);
        reportDashboard.addColumn(column3);

    }

    public String generateRevenueReport() {
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
        return aasNavController.redirectToViewRevenueReport();
    }

    public String generateCostReport() {

        generateFeulCostReport();
        generateFleetCostReport();
        generateFlightOpsCostReport();
        generateMktCostReport();
        generatePayrollReport();

        try {
            List<InputStream> pdfs = new ArrayList<InputStream>();
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FeulCostReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FleetCostReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FlightOperationCostReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/PayrollReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/MarketingCostReport.pdf"));

            OutputStream output = new FileOutputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/CostReport.pdf");
            concatPDFs(pdfs, output, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return aasNavController.redirectToViewCostReport();
    }

    public String generateFinancialReport() {
        generateCostReport();
        generateRevenueReport();

        try {
            List<InputStream> pdfs = new ArrayList<InputStream>();
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FeulCostReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FleetCostReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FlightOperationCostReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/PayrollReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/MarketingCostReport.pdf"));
            pdfs.add(new FileInputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/revenue_report/RevenueReport.pdf"));
            System.out.println("P1");
            OutputStream output = new FileOutputStream("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/financial_report/FinancialAccountingReport.pdf");
            System.out.println("P2");
            concatPDFs(pdfs, output, true);
            System.out.println("P3");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return aasNavController.redirectToViewFinancialReport();
    }

    public String generateFeulCostReport() {
        try {
            Connection connection;
            connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/mas", "root", "root");
            System.out.println("start to generate feul cost report");
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/src/java/reports/feulCost.jrxml");
                System.out.println("step 1 done");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), connection);
                System.out.println("step 2 done");
                String exportPath = "/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FeulCostReport.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                System.out.println("Report is generated");

            } catch (JRException ex) {
                Logger.getLogger(reportController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(reportController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return aasNavController.redirectToViewFeulCostReport();
    }

    public String generateFleetCostReport() {
        try {
            Connection connection;
            connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/mas", "root", "root");
            System.out.println("start to generate cost report");
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/src/java/reports/fleetCostReport.jrxml");
                System.out.println("step 1 done");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), connection);
                System.out.println("step 2 done");
                String exportPath = "/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FleetCostReport.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                System.out.println("Report is generated");

            } catch (JRException ex) {
                Logger.getLogger(reportController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(reportController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return aasNavController.redirectToViewFleetCostReport();
    }

    public String generateFlightOpsCostReport() {
        try {
            Connection connection;
            connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/mas", "root", "root");
            System.out.println("start to generate cost report");
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/src/java/reports/flightOpsCostReport.jrxml");
                System.out.println("step 1 done");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), connection);
                System.out.println("step 2 done");
                String exportPath = "/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/FlightOperationCostReport.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                System.out.println("Report is generated");

            } catch (JRException ex) {
                Logger.getLogger(reportController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(reportController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return aasNavController.redirectToViewFlightOpsCostReport();
    }

    public String generatePayrollReport() {
        try {
            Connection connection;
            connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/mas", "root", "root");
            System.out.println("start to generate cost report");
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/src/java/reports/hrReport.jrxml");
                System.out.println("step 1 done");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), connection);
                System.out.println("step 2 done");
                String exportPath = "/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/PayrollReport.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                System.out.println("Report is generated");

            } catch (JRException ex) {
                Logger.getLogger(reportController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(reportController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return aasNavController.redirectToViewHrCostReport();
    }

    public String generateMktCostReport() {
        try {
            Connection connection;
            connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/mas", "root", "root");
            System.out.println("start to generate cost report");
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/src/java/reports/mktCostReport.jrxml");
                System.out.println("step 1 done");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), connection);
                System.out.println("step 2 done");
                String exportPath = "/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/MarketingCostReport.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                System.out.println("Report is generated");

            } catch (JRException ex) {
                Logger.getLogger(reportController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(reportController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return aasNavController.redirectToViewMktCostReport();
    }

    public static void concatPDFs(List<InputStream> streamOfPDFFiles, OutputStream outputStream, boolean paginate) {

        Document document = new Document();
        try {
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            // Create Readers for the pdfs.
            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }
            // Create a writer for the outputstream
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
            // data

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            // Loop through the PDF files and add to the output.
            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                // Create a new page in the target for each source page.
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader,
                            pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);

                    // Code for pagination.
                    if (paginate) {
                        cb.beginText();
                        cb.setFontAndSize(bf, 9);
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, ""
                                + currentPageNumber + " of " + totalPages, 520,
                                5, 0);
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public StreamedContent getContent() {
        return content;
    }

    public void setContent(StreamedContent content) {
        this.content = content;
    }

    public DashboardModel getReportDashboard() {
        return reportDashboard;
    }

    public void setReportDashboard(DashboardModel reportDashboard) {
        this.reportDashboard = reportDashboard;
    }

    public void onPrerender(ComponentSystemEvent event) {

        file = new File("/Users/Tongtong/Documents/IS3102/MAS/MAS-war/web/resources/cost_report/CostReport.pdf");

    }

    public AasNavController getAasNavController() {
        return aasNavController;
    }

    public void setAasNavController(AasNavController aasNavController) {
        this.aasNavController = aasNavController;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
