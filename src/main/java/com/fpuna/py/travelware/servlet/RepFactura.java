package com.fpuna.py.travelware.servlet;

import com.fpuna.py.travelware.model.ComProveedores; //dmezac+
import com.fpuna.py.travelware.dao.ProveedorDao; //dmezac+
import com.fpuna.py.travelware.model.ComFacturas; //dmezac+
import com.fpuna.py.travelware.dao.FacturaDao; //dmezac+

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

public class RepFactura extends HttpServlet {
    private static final String CONTENT_TYPE = "application/vnd.ms-excel";
    private String user, pwd, usuario, clave;

    private WritableCellFormat formatoCab;
    private WritableCellFormat formatoTexto;
    private WritableCellFormat formatoNumero;
    private WritableCellFormat formatoFecha;
    private WritableCellFormat formatoTexto2;
    private WritableCellFormat formatoNumero2;
    private WritableCellFormat formatoFecha2;

    private WritableCellFormat fTitulo;
    private WritableCellFormat fPie;
    private WritableCellFormat fTexto; //dmezac+
    private WritableCellFormat fNumero; //dmezac+
    private WritableCellFormat fFecHor;
    private WritableCellFormat fFecha;
    private WritableCellFormat fHora;

    //private String sentencia = "";

    private ComProveedores proveedor; //dmezac+
    private List<ComFacturas> facturas; //dmezac+

    @EJB
    private ProveedorDao proveedorEJB; //dmezac+
    @EJB
    private FacturaDao facturaEJB; //dmezac+

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
        String nombre = "FACTURAS";
        String id = request.getParameter("id");

        this.proveedor = proveedorEJB.getById(Integer.parseInt(id)); //dmezac+
        this.facturas = facturaEJB.getAllProv(proveedor); //dmezac+

            try {
            String desc = "Listado de Facturas"; //dmezac+

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

            this.addLabel(excelSheet, 1, 0, "Proveedor:", fFecHor);
            this.addLabel(excelSheet, 2, 0, this.proveedor.getProDesc(), fTexto);

            this.addLabel(excelSheet, 1, 1, "Timbrado:", fFecHor);
            this.addLabel(excelSheet, 2, 1, this.proveedor.getProNroTim(), fTexto);

            this.addLabel(excelSheet, cantColumnas-1, 0, "Fecha:", fFecHor);
            this.addDate(excelSheet, cantColumnas, 0, fecha, fFecha);

            this.addLabel(excelSheet, cantColumnas-1, 1, "Hora:", fFecHor);
            this.addDate(excelSheet, cantColumnas, 1, fecha, fHora);

            //Cargamos la cabecera 
            this.addLabel(excelSheet, 0, ++fila, "", formatoCab); //dmezac+
            this.addLabel(excelSheet, 1, fila, "Nro. de Factura", formatoCab); //dmezac+
            this.addLabel(excelSheet, 2, fila, "Fecha de Emisión", formatoCab); //dmezac+
            this.addLabel(excelSheet, 3, fila, "Descripción", formatoCab); //dmezac+
            this.addLabel(excelSheet, 4, fila, "Condición", formatoCab); //dmezac+
            this.addLabel(excelSheet, 5, fila, "Moneda", formatoCab); //dmezac+
            this.addLabel(excelSheet, 6, fila, "Tipo de Cambio", formatoCab); //dmezac+
            this.addLabel(excelSheet, 7, fila, "Total", formatoCab); //dmezac+
            this.addLabel(excelSheet, 8, fila, "Saldo", formatoCab); //dmezac+
            this.addLabel(excelSheet, 9, fila, "Estado", formatoCab); //dmezac+

            excelSheet.setColumnView(8, cv);

            //Inmovilizamos paneles
            excelSheet.getSettings().setVerticalFreeze(fila+1);

            //Cargamos el detalle de las filas
            Iterator<ComFacturas> facturaIter = facturas.iterator();

            int row = 0;
            while (facturaIter.hasNext())
            {
                ComFacturas factura = facturaIter.next();

                //Cargamos las columnas //dmezac+ ini
                if(fila%2 == 0) //si es par
                {
                    //Procedimientos --> addLabel, addNumber, addDate
                    //Formatos --> formatoTexto, formatoNumero, formatoFecha
                    this.addNumber(excelSheet, 0, ++fila, ++row, formatoTexto2);
                    this.addLabel (excelSheet, 1, fila, factura.getFacNro(), formatoTexto2);
                    this.addDate  (excelSheet, 2, fila, factura.getFacFecha(), formatoFecha2);
                    this.addLabel (excelSheet, 3, fila, factura.getFacDesc(), formatoTexto2);
                    this.addLabel (excelSheet, 4, fila, factura.getFacCond().toString().replace("C", "Contado").replace("R", "Crédito"), formatoTexto2);
                    this.addLabel (excelSheet, 5, fila, factura.getMonId().getMonDesc(), formatoTexto2);
                    if(factura.getFacCambio() != null)
                        this.addNumber(excelSheet, 6, fila, factura.getFacCambio().doubleValue(), formatoNumero2);
                    else
                        this.addLabel (excelSheet, 6, fila, "", formatoNumero2);
                    this.addNumber(excelSheet, 7, fila, factura.getFacTotal().doubleValue(), formatoNumero2);
                    this.addNumber(excelSheet, 8, fila, factura.getFacSaldo().doubleValue(), formatoNumero2);
                    this.addLabel (excelSheet, 9, fila, factura.getFacEst().toString().replace("I", "Ingresado").replace("P", "Pagado"), formatoTexto2);
                }
                else //si no es par
                {
                    this.addNumber(excelSheet, 0, ++fila, ++row, formatoTexto);
                    this.addLabel (excelSheet, 1, fila, factura.getFacNro(), formatoTexto);
                    this.addDate  (excelSheet, 2, fila, factura.getFacFecha(), formatoFecha);
                    this.addLabel (excelSheet, 3, fila, factura.getFacDesc(), formatoTexto);
                    this.addLabel (excelSheet, 4, fila, factura.getFacCond().toString().replace("C", "Contado").replace("R", "Crédito"), formatoTexto);
                    this.addLabel (excelSheet, 5, fila, factura.getMonId().getMonDesc(), formatoTexto);
                    if(factura.getFacCambio() != null)
                        this.addNumber(excelSheet, 6, fila, factura.getFacCambio().doubleValue(), formatoNumero);
                    else
                        this.addLabel (excelSheet, 6, fila, "", formatoNumero);
                    this.addNumber(excelSheet, 7, fila, factura.getFacTotal().doubleValue(), formatoNumero);
                    this.addNumber(excelSheet, 8, fila, factura.getFacSaldo().doubleValue(), formatoNumero);
                    this.addLabel (excelSheet, 9, fila, factura.getFacEst().toString().replace("I", "Ingresado").replace("P", "Pagado"), formatoTexto);
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
