/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.controler;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.ucompensar.veterinaria.DTO.VeterinarioDTO;
import com.ucompensar.veterinaria.service.VeterinarioService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@WebServlet("/api/export/veterinarios/*")
public class VeterinarioExportController extends HttpServlet {

    private final VeterinarioService veterinarioService = new VeterinarioService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        String path = request.getPathInfo(); // /excel o /pdf

        if (path == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<VeterinarioDTO> veterinario = veterinarioService.listarTodosDTO();

        switch (path) {
            case "/excel" -> exportarExcel(veterinario, response);
            case "/pdf" -> exportarPDF(veterinario, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    

    private void exportarExcel(List<VeterinarioDTO> veterinarios,
                           HttpServletResponse response) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Veterinarios");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Apellidos");
        header.createCell(3).setCellValue("Tipo Documento");
        header.createCell(4).setCellValue("Número Documento");
        header.createCell(5).setCellValue("Teléfono");
        header.createCell(6).setCellValue("Email");
        header.createCell(7).setCellValue("Dirección");

        int rowNum = 1;

        for (VeterinarioDTO v : veterinarios) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(v.getId());
            row.createCell(1).setCellValue(v.getNombres());
            row.createCell(2).setCellValue(v.getApellidos());
            row.createCell(3).setCellValue(v.getTipoDocumento());
            row.createCell(4).setCellValue(v.getNumeroDocumento());
            row.createCell(5).setCellValue(v.getTelefono());
            row.createCell(6).setCellValue(v.getEmail());
            row.createCell(7).setCellValue(v.getEstado());
        }

        response.setContentType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=veterinarios.xlsx"
        );

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void exportarPDF(List<VeterinarioDTO> veterinarios,
                             HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=veterinarios.pdf"
        );

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("LISTADO DE VETERINARIOS\n\n"));

        for (VeterinarioDTO v : veterinarios) {

            document.add(new Paragraph(
                    "ID: " + v.getId()
            ));
            document.add(new Paragraph(
                    "Nombre: " + v.getNombres()
            ));
            document.add(new Paragraph(
                    "Apellidos: " + v.getApellidos()
            ));
            document.add(new Paragraph(
                    "Tipo documento: " + v.getTipoDocumento()
            ));
            document.add(new Paragraph(
                    "Número documento: " + v.getNumeroDocumento()
            ));
            document.add(new Paragraph(
                    "Teléfono: " + v.getTelefono()
            ));
            document.add(new Paragraph(
                    "Email: " + v.getEmail()
            ));
            document.add(new Paragraph(
                    "Dirección: " + v.getEstado()
            ));
            document.add(new Paragraph("-----------------------------"));
        }

        document.close();
    }
}

