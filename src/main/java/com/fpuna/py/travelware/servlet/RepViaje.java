package com.fpuna.py.travelware.servlet;

import com.fpuna.py.travelware.model.ViaViajes; //dmezac+
import com.fpuna.py.travelware.dao.ViajeDao; //dmezac+
import com.fpuna.py.travelware.model.ViaPasajeros; //dmezac+
import com.fpuna.py.travelware.dao.PasajeroDao; //dmezac+

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;

import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
//import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.NumberFormats;

import jxl.write.biff.RowsExceededException;

public class RepViaje extends HttpServlet {
    private static final String CONTENT_TYPE = "application/vnd.ms-excel";
    private String user, pwd, usuario, clave;

    private WritableCellFormat formatoCab;
    private WritableCellFormat formatoTexto, formatoNumero, formatoFecha;
    private WritableCellFormat formatoTexto2, formatoNumero2, formatoFecha2;
    private WritableCellFormat formActTexto, formActNumero, formActFecha;

    private WritableCellFormat fTitulo, fPie;
    private WritableCellFormat fTexto, fNumero; //dmezac+
    private WritableCellFormat fFecHor, fFecha, fHora;

    //private String sentencia = "";

    private ViaViajes viaje; //dmezac+
    private List<ViaPasajeros> pasajeros; //dmezac+

    @EJB
    private ViajeDao viajeEJB; //dmezac+
    @EJB
    private PasajeroDao pasajeroEJB; //dmezac+

    @Override
    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Content-Disposition", "attachment; filename=sampleName.xls");

        //Obtengo datos de la sesion
        HttpSession session = request.getSession();
        user = (String)session.getAttribute("user");
        pwd = (String)session.getAttribute("pwd");

        ServletOutputStream out = response.getOutputStream();
        // leo el parametro Cliente
        //String nombre = request.getParameter("rep");
        String nombre = "VIAJE";
        String id = request.getParameter("id");

        this.viaje = viajeEJB.getById(Integer.parseInt(id)); //dmezac+
        this.pasajeros = pasajeroEJB.getAll(viaje); //dmezac+

            try {
            String desc = "PASSPORT LIST"; //dmezac+

            // obtengo imagen de la empresa
            //byte[] logo = new byte[1]; //dmezac-
            //String sql_logo = "select org_logo CONTENIDO from pge_organizaciones where org_id = fnt_obt_parametro('MI_ENTIDAD')"; //dmezac-
            //logo = bd.leerBLOB(sql_logo); //dmezac-
            /*-----------------------------------------------------------------------------------------*/

            //Nombre del archivo generado como adjunto
            Date fecha = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
            response.setHeader("Content-Disposition", "attachment; filename=" + nombre + sdf.format(fecha)+".xls");

            //int cantColumnas = rs2.getMetaData().getColumnCount(); //dmezac-
            final int cantColumnas = 9; //dmezac+
            System.out.println("cantColumnas : " + cantColumnas);

/* Operaciones con Excel ------------------------------------------------------------------*/
            //Definimos las propiedades
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));

            //Leemos el libro de plantilla
            //C:\\Oracle\\Middleware\\user_projects\\domains\\damian --> ubicacion de plantilla
            //Workbook plantilla = Workbook.getWorkbook(new File("http://localhost:7001/travelware/plantilla.xls"));
            //Creamos el libro
            WritableWorkbook workbook = Workbook.createWorkbook(out, /*plantilla,*/ wbSettings);
            //Obtenemos la hoja
            //WritableSheet excelSheet = workbook.getSheet(0);
            WritableSheet excelSheet = workbook.createSheet(nombre, 0);
            excelSheet.setName(nombre);

            this.createFormat();

            CellView cv = new CellView();
            //cv.setFormat(calibri);
            //cv.setAutosize(true); //dmezac- OJO VER

            int fila = 3;
            //Cargamos el titulo
            excelSheet.mergeCells(1,fila,cantColumnas,fila);
            this.addLabel(excelSheet, 1, fila++, desc, fTitulo);

            //Cargamos el logo
            //excelSheet.addImage(new WritableImage(1,0,1,3,logo)); //dmezac-

            this.addLabel(excelSheet, 1, 0, "Viaje:", fFecHor);
            this.addLabel(excelSheet, 2, 0, this.viaje.getViaDesc(), fTexto);

            this.addLabel(excelSheet, 1, 1, "Salida:", fFecHor);
            this.addDate(excelSheet, 2, 1, this.viaje.getViaFecSali(), fFecha);

            this.addLabel(excelSheet, cantColumnas-1, 0, "Fecha:", fFecHor);
            this.addDate(excelSheet, cantColumnas, 0, fecha, fFecha);

            this.addLabel(excelSheet, cantColumnas-1, 1, "Hora:", fFecHor);
            this.addDate(excelSheet, cantColumnas, 1, fecha, fHora);

            //Cargamos la cabecera 
            this.addLabel(excelSheet, 0, ++fila, "", formatoCab); //dmezac+
            this.addLabel(excelSheet, 1, fila, "FAMILY NAME", formatoCab); //dmezac+
            this.addLabel(excelSheet, 2, fila, "FIRST NAME", formatoCab); //dmezac+
            this.addLabel(excelSheet, 3, fila, "PASSPORT", formatoCab); //dmezac+
            this.addLabel(excelSheet, 4, fila, "DATE OF BIRTH", formatoCab); //dmezac+
            this.addLabel(excelSheet, 5, fila, "PLACE OF BIRTH", formatoCab); //dmezac+
            this.addLabel(excelSheet, 6, fila, "NATIONALITY", formatoCab); //dmezac+
            this.addLabel(excelSheet, 7, fila, "SEX", formatoCab); //dmezac+
            this.addLabel(excelSheet, 8, fila, "DATE OF ISSUE", formatoCab); //dmezac+
            this.addLabel(excelSheet, 9, fila, "EXPIRY", formatoCab); //dmezac+

            excelSheet.setColumnView(8, cv);

            //Inmovilizamos paneles
            excelSheet.getSettings().setVerticalFreeze(fila+1);

            //Cargamos el detalle de las filas
            Iterator<ViaPasajeros> pasajeroIter = pasajeros.iterator();

            int row = 0;
            while (pasajeroIter.hasNext())
            {
                ViaPasajeros pasajero = pasajeroIter.next();
                //Cargamos las columnas //dmezac+ ini
                if(fila%2 == 0) //si es par
                {
                    formActTexto = formatoTexto2;
                    formActNumero = formatoNumero2;
                    formActFecha = formatoFecha2;
                }
                else //si no es par
                {
                    formActTexto = formatoTexto;
                    formActNumero = formatoNumero;
                    formActFecha = formatoFecha;
                }
                //Procedimientos --> addLabel, addNumber, addDate
                //Formatos --> formActTexto, formActNumero, formActFecha
                this.addNumber(excelSheet, 0, ++fila, ++row, formActTexto);
                this.addLabel (excelSheet, 1, fila, pasajero.getPerId().getPerApe(), formActTexto);
                this.addLabel (excelSheet, 2, fila, pasajero.getPerId().getPerNom(), formActTexto);
                if(pasajero.getPerId().getViaPasaportes() != null)
                    this.addLabel (excelSheet, 3, fila, pasajero.getPerId().getViaPasaportes().getPatNroPas(), formActTexto);
                else
                    this.addLabel (excelSheet, 3, fila, null, formActTexto);
                this.addDate  (excelSheet, 4, fila, pasajero.getPerId().getPerFecNac(), formActFecha);
                this.addLabel (excelSheet, 5, fila, pasajero.getPerId().getPerLugNac(), formActTexto);
                this.addLabel (excelSheet, 6, fila, pasajero.getPerId().getPaiId().getPaiNac(), formActTexto);
                this.addLabel (excelSheet, 7, fila, pasajero.getPerId().getPerSex().toString(), formActTexto);
                if(pasajero.getPerId().getViaPasaportes() != null) {
                    this.addDate  (excelSheet, 8, fila, pasajero.getPerId().getViaPasaportes().getPatFecEmi(), formActFecha);
                    this.addDate  (excelSheet, 9, fila, pasajero.getPerId().getViaPasaportes().getPatFecVen(), formActFecha);
                }
                else {
                    this.addDate  (excelSheet, 8, fila, null, formActFecha);
                    this.addDate  (excelSheet, 9, fila, null, formActFecha);
                }
                //dmezac+ fin
            }
            //Cargamos el pie de pagina
            excelSheet.mergeCells(1, ++fila, cantColumnas, fila);
            this.addLabel(excelSheet, 1, fila, "* * * FIN LISTADO * * *", fPie);
            this.addLabel(excelSheet, 0, fila, null, fPie);

            //Escribimos el archivo y cerramos
            workbook.write();
            workbook.close();
/*-----------------------------------------------------------------------------------------*/            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // cierro la respuesta
        out.close();
    }

    @Override
    public void doPost(HttpServletRequest request, 
                       HttpServletResponse response) throws ServletException, IOException {response.setContentType(CONTENT_TYPE);
        // invoco al doGet con los parametros recibidos
        doGet(request, response);  
    }

    private void createFormat()
        throws WriteException {

        // Creamos las fuentes
        WritableFont calibri10ptBold = new WritableFont(WritableFont.createFont("Calibri"), 10, WritableFont.BOLD);
        WritableFont calibri10pt = new WritableFont(WritableFont.createFont("Calibri"), 10);
        WritableFont calibri09ptBold = new WritableFont(WritableFont.createFont("Calibri"), 9, WritableFont.BOLD);
        calibri09ptBold.setColour(Colour.WHITE);

        //Creamos los formatos
        DateFormat df = new DateFormat("dd/MM/yyyy");
        DateFormat hf = new DateFormat("HH:mm:ss");
        
        DecimalFormat nf = new DecimalFormat();

        // Creamos los fomatos de celda
        formatoTexto = new WritableCellFormat(calibri10pt);
        formatoTexto.setBackground(Colour.WHITE);
        formatoTexto.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
        formatoTexto.setBorder(Border.TOP, BorderLineStyle.THIN);

        formatoTexto2 = new WritableCellFormat(calibri10pt);
        formatoTexto2.setBackground(Colour.IVORY);
        formatoTexto2.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
        formatoTexto2.setBorder(Border.TOP, BorderLineStyle.THIN);

        formatoNumero = new WritableCellFormat(calibri10pt, NumberFormats.FORMAT7);
        formatoNumero.setBackground(Colour.WHITE);
        formatoNumero.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
        formatoNumero.setBorder(Border.TOP, BorderLineStyle.THIN);

        formatoNumero2 = new WritableCellFormat(calibri10pt, NumberFormats.FORMAT7);
        formatoNumero2.setBackground(Colour.IVORY);
        formatoNumero2.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
        formatoNumero2.setBorder(Border.TOP, BorderLineStyle.THIN);

        formatoFecha =  new WritableCellFormat(calibri10pt, df);
        formatoFecha.setBackground(Colour.WHITE);
        formatoFecha.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
        formatoFecha.setBorder(Border.TOP, BorderLineStyle.THIN);

        formatoFecha2 =  new WritableCellFormat(calibri10pt, df);
        formatoFecha2.setBackground(Colour.IVORY);
        formatoFecha2.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
        formatoFecha2.setBorder(Border.TOP, BorderLineStyle.THIN);

        formatoCab = new WritableCellFormat(calibri09ptBold);
        formatoCab.setBackground(Colour.BROWN);
        formatoCab.setVerticalAlignment(VerticalAlignment.CENTRE);
        formatoCab.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM);
        formatoCab.setBorder(Border.TOP, BorderLineStyle.MEDIUM);
        formatoCab.setWrap(true);

        fTitulo =  new WritableCellFormat(calibri10ptBold);
        fTitulo.setAlignment(Alignment.CENTRE);
        fTitulo.setWrap(true);

        fPie =  new WritableCellFormat(calibri10pt);
        fPie.setBorder(Border.TOP, BorderLineStyle.MEDIUM);
        fPie.setAlignment(Alignment.CENTRE);
        fPie.setWrap(true);

        fTexto = new WritableCellFormat(calibri10pt); //dmezac+
        fNumero = new WritableCellFormat(calibri10pt, NumberFormats.FORMAT7); //dmezac+

        fFecHor =  new WritableCellFormat(calibri10ptBold);
        fFecHor.setBackground(Colour.GREY_25_PERCENT);
        fFecHor.setAlignment(Alignment.RIGHT);

        fFecha =  new WritableCellFormat(calibri10pt, df);
        fFecha.setAlignment(Alignment.LEFT);

        fHora =  new WritableCellFormat(calibri10pt, hf);
        fHora.setAlignment(Alignment.LEFT);
}

    private void addNumber(WritableSheet sheet, int column, int row, double num, WritableCellFormat f)
        throws WriteException, RowsExceededException {
        Number number;
        number = new Number(column, row, num, f);
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s, WritableCellFormat f)
        throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, f);
        sheet.addCell(label);
    }

    private void addDate(WritableSheet sheet, int column, int row, Date d, WritableCellFormat f)
        throws WriteException, RowsExceededException {
        if (!(d == null)) {
            DateTime date;
            date = new DateTime(column, row, d, f );
            sheet.addCell(date);
        } else
            this.addLabel(sheet, column, row, "", f);
    }
}
