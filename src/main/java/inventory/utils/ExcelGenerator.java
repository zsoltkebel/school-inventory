package inventory.utils;

import inventory.model.Item;
import inventory.model.Lesson;
import inventory.model.Reservation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ExcelGenerator {

    private static String[] reservationColumns = {"name", "for date", "for lesson", "item name", "item description", "comment"};

    public static void generateTableReservations(File toFile, LocalDate from, LocalDate to) {
        generateTableReservations(toFile, Database.getInstance().queryAll(from, to, false, null));

    }

    public static void generateTableReservations(File toFile, List<Reservation> reservations) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reservations");

        XSSFRow headerRow = sheet.createRow(0);

        for (int i = 0; i < reservationColumns.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(reservationColumns[i]);
        }

        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            XSSFRow row = sheet.createRow(i + 1);

            Item item = reservation.getItem();
            Lesson lesson = reservation.getLesson();
            row.createCell(0).setCellValue(reservation.getName());      // name
            row.createCell(1).setCellValue(reservation.getDate());      // for date
            row.createCell(2).setCellValue(lesson.getNo());             // for lesson
            row.createCell(3).setCellValue(item.getName());             // item name
            row.createCell(4).setCellValue(item.getDescription());      // item description
            row.createCell(5).setCellValue(reservation.getComment());   // reservation comment

            // set style to display date
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            XSSFCreationHelper createHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd"));
            Cell cell = row.getCell(1);
            cell.setCellStyle(cellStyle);
        }

        // Resize all columns to fit the content
        for (int i = 0; i < reservationColumns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            if (!toFile.exists()) {
                File parent = toFile.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                toFile.createNewFile();

                FileOutputStream fileOut = new FileOutputStream(toFile);
                workbook.write(fileOut);
                workbook.close();
                fileOut.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        FileOutputStream fileOut = new FileOutputStream("contacts.xlsx");
//        workbook.write(fileOut);
//        fileOut.close();
    }
}
