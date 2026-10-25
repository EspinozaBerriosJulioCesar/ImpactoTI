package com.impactoti.service;

import com.impactoti.model.Incidencia;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelExportService {

    public byte[] exportarIncidencias(List<Incidencia> incidencias)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            Sheet hoja = workbook.createSheet("Incidencias");

            String[] columnas = {
                "ID", "Título", "Descripción", "Nivel de impacto",
                "Impacto en ventas (S/)", "Prioridad", "Estado"
            };

            Row encabezado = hoja.createRow(0);

            for (int i = 0; i < columnas.length; i++) {
                encabezado.createCell(i).setCellValue(columnas[i]);
            }

            int numeroFila = 1;

            for (Incidencia incidencia : incidencias) {
                Row fila = hoja.createRow(numeroFila++);

                fila.createCell(0).setCellValue(
                    incidencia.getId() == null ? 0 : incidencia.getId());
                fila.createCell(1).setCellValue(texto(incidencia.getTitulo()));
                fila.createCell(2).setCellValue(texto(incidencia.getDescripcion()));
                fila.createCell(3).setCellValue(texto(incidencia.getNivelImpacto()));
                fila.createCell(4).setCellValue(
                    incidencia.getImpactoVentas() == null
                        ? 0
                        : incidencia.getImpactoVentas().doubleValue());
                fila.createCell(5).setCellValue(texto(incidencia.getPrioridad()));
                fila.createCell(6).setCellValue(texto(incidencia.getEstado()));
            }

            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }

            workbook.write(salida);
            return salida.toByteArray();
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
