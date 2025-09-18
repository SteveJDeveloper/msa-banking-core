package com.pichincha.dm.service.impl;

import com.pichincha.dm.domain.Account;
import com.pichincha.dm.domain.dto.responses.MovementResponseDto;
import com.pichincha.dm.service.AccountService;
import com.pichincha.dm.service.MovementService;
import com.pichincha.dm.service.ReportService;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;
import org.openpdf.text.Image;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final MovementService movementService;
  private final AccountService accountService;

  @Override
  public String generateReportPdf(Long accountId, LocalDateTime from, LocalDateTime to) {
    List<MovementResponseDto> movements = movementService.getMovementsByAccountId(accountId, from, to);
    Account account = accountService.findById(accountId);
    return generatePdfBase64Report(movements, account, from, to);
  }

  private static String generatePdfBase64Report(List<MovementResponseDto> movements, Account account, LocalDateTime from, LocalDateTime to) {
    Document document = new Document();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, out);
    document.open();

    Font sectionFont = getSectionFont();
    Font separatorFont = getSeparatorFont();
    Color yellow = new Color(255, 221, 51);
    LineSeparator separator = new LineSeparator();
    separator.setLineColor(yellow);

    addHeader(document, sectionFont, separator);
    addSectionTitle(document, "Información del cliente", sectionFont, separator);
    addClientInfoTable(document, account, from, to);
    addSectionTitle(document, "Información de la cuenta", sectionFont, separator);
    addAccountInfoTable(document, account);
    addSectionTitle(document, "Movimientos", sectionFont, separator);
    addMovementsTable(document, movements);

    document.close();
    byte[] pdfBytes = out.toByteArray();
    return Base64.getEncoder().encodeToString(pdfBytes);
  }

  private static Font getSectionFont() {
    return new Font(Font.HELVETICA, 18, Font.BOLD, new Color(33, 54, 139));
  }

  private static Font getSeparatorFont() {
    return new Font(Font.HELVETICA, 10, Font.BOLD);
  }

  private static void addHeader(Document document, Font sectionFont, LineSeparator separator) {
    try {
      Image logo = Image.getInstance("src/main/resources/images/banco_pichincha_portada.jpg");
      logo.scaleToFit(240, 120);
      logo.setAlignment(Image.ALIGN_LEFT);
      PdfPTable headerTable = new PdfPTable(2);
      headerTable.setWidthPercentage(100);
      headerTable.setWidths(new float[]{1, 3});
      PdfPCell logoCell = new PdfPCell(logo, false);
      logoCell.setBorder(PdfPCell.NO_BORDER);
      logoCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      logoCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
      Paragraph reportTitle = new Paragraph("Reporte de Movimientos", sectionFont);
      reportTitle.setAlignment(Paragraph.ALIGN_RIGHT);
      PdfPCell titleCell = new PdfPCell();
      titleCell.addElement(reportTitle);
      titleCell.setBorder(PdfPCell.NO_BORDER);
      titleCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      titleCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
      headerTable.addCell(logoCell);
      headerTable.addCell(titleCell);
      document.add(headerTable);
    } catch (Exception e) {
      e.printStackTrace();
    }
    document.add(Chunk.NEWLINE);
  }

  private static void addSectionTitle(Document document, String title, Font sectionFont, LineSeparator separator) {
    Paragraph subtitle = new Paragraph(title, sectionFont);
    subtitle.setAlignment(Paragraph.ALIGN_LEFT);
    document.add(subtitle);
    document.add(new Chunk(separator));
  }

  private static void addClientInfoTable(Document document, Account account, LocalDateTime from, LocalDateTime to) {
    PdfPTable clientTable = new PdfPTable(3);
    clientTable.setWidthPercentage(100);
    clientTable.setSpacingBefore(2f);
    clientTable.setSpacingAfter(2f);
    clientTable.setWidths(new float[]{1, 1, 1});
    Font infoFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
    Font infoBold = new Font(Font.HELVETICA, 12, Font.BOLD);
    addTableHeaderRow(clientTable, new String[]{"Nombre", "Desde", "Hasta"}, infoBold);
    addTableValueRow(clientTable, new String[]{account.getClient().getName(),
      from.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
      to.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}, infoFont);
    document.add(clientTable);
  }

  private static void addAccountInfoTable(Document document, Account account) {
    PdfPTable accountTable = new PdfPTable(3);
    accountTable.setWidthPercentage(100);
    accountTable.setSpacingBefore(10f);
    accountTable.setSpacingAfter(10f);
    accountTable.setWidths(new float[]{2, 2, 2});
    Font accountHeaderFont = new Font(Font.HELVETICA, 12, Font.BOLD);
    Font accountValueFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
    addTableHeaderRow(accountTable, new String[]{"Número de cuenta", "Tipo de Cuenta", "Balance"}, accountHeaderFont);
    addTableValueRow(accountTable, new String[]{account.getAccountNumber().toString(),
      account.getAccountType().name(), "$ " + account.getBalance().toPlainString()}, accountValueFont);
    document.add(accountTable);
  }

  private static void addTableHeaderRow(PdfPTable table, String[] headers, Font font) {
    for (String header : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(header, font));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
      table.addCell(cell);
    }
  }

  private static void addTableValueRow(PdfPTable table, String[] values, Font font) {
    for (String value : values) {
      PdfPCell cell = new PdfPCell(new Phrase(value, font));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
      table.addCell(cell);
    }
  }

  private static void addMovementsTable(Document document, List<MovementResponseDto> movements) {
    PdfPTable table = new PdfPTable(5);
    table.setWidthPercentage(100);
    addMovementsTableHeader(table);
    addMovementsRows(table, movements);
    document.add(table);
  }

  private static void addMovementsTableHeader(PdfPTable table) {
    String[] headers = {"Fecha", "Número de cuenta", "Tipo Movimiento", "Valor", "Balance"};
    Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    for (String header : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
      table.addCell(cell);
    }
  }

  private static void addMovementsRows(PdfPTable table, List<MovementResponseDto> movements) {
    Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
    for (MovementResponseDto movement : movements) {
      addTableValueRow(table, new String[]{
        movement.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        movement.getAccountNumber().toString(),
        movement.getMovementType().name(),
        "$ " + movement.getValue().toPlainString(),
        "$ " + movement.getBalance().toPlainString()
      }, valueFont);
    }
  }

}
