package com.fpuna.py.travelware.servlet;

import com.fpuna.py.travelware.dao.MovCajaDao; //dmezac+
import com.fpuna.py.travelware.model.ComMovCajas; //dmezac+
import com.fpuna.py.travelware.dao.CajaChicaDao; //dmezac+
import com.fpuna.py.travelware.model.ComCajas; //dmezac+

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

//import java.sql.ResultSet;
//import java.sql.Statement;
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

//import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class GeneraExcel extends HttpServlet {
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

    private ComCajas caja; //dmezac+
    private List<ComMovCajas> movCajas; //dmezac+

    @EJB
    private MovCajaDao movCajaEJB; //dmezac+
    @EJB
    private CajaChicaDao cajaChicaEJB; //dmezac+

    @Override
    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Content-Disposition", "attachment; filename=sampleName.xls");

        //BaseDatos bd = new BaseDatos(); //dmezac-

        //Obtengo datos de la sesion
        HttpSession session = request.getSession();
        user = (String)session.getAttribute("user");
        pwd = (String)session.getAttribute("pwd");
        //usuario = (String)session.getAttribute("usuario"); //dmezac-
        //clave = (String)session.getAttribute("clave"); //dmezac-
        //setting session to expiry ipasswordn 30 mins
        //session.setMaxInactiveInterval(30*60);

        ServletOutputStream out = response.getOutputStream();
        // leo el parametro Cliente
        String nombre = request.getParameter("rep");
        String id = request.getParameter("id");
        //this.sentencia = "select upper(a.tac_desc) tac_desc, a.tac_sql from PGE_TABLAS_CAB a where a.tac_nom = upper('"+nombre+"') and a.tac_tip = 'L'"; //dmezac-
        //System.out.println(this.sentencia);                                     

        //Statement st = null; //dmezac-
        //ResultSet rs2 = null; //dmezac-
        this.caja = cajaChicaEJB.getById(Integer.parseInt(id)); //dmezac+
        this.movCajas = movCajaEJB.getAll(); //dmezac+

        try {
            // me conecto a la base de datos
            //bd.conectar(usuario, clave); //dmezac-
            //bd.ejecutarDML("begin pkg_sis.p_set_usuario('" + user + "'); end;");  // setea el usuario //dmezac-

            // obtengo los datos de la consulta
            //st = bd.conn.createStatement(); //dmezac-
            //rs2 = st.executeQuery(sentencia); //dmezac-
            //rs2.next(); //dmezac-
            //String desc = rs2.getString("tac_desc"); //dmezac-
            //String sql = rs2.getString("tac_sql"); //dmezac-
            //String pfiltro = ""; //dmezac-
            //String[] pfiltro2 = new String[5]; //dmezac-
            String desc = "Detalle de Movimientos de Caja Chica"; //dmezac+

            // obtengo imagen de la empresa
            //byte[] logo = new byte[1]; //dmezac-
            //String sql_logo = "select org_logo CONTENIDO from pge_organizaciones where org_id = fnt_obt_parametro('MI_ENTIDAD')"; //dmezac-
            //logo = bd.leerBLOB(sql_logo); //dmezac-
            /*-----------------------------------------------------------------------------------------*/
            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // datos de archivo adjunto
            //String fieldname = "";

            /*List items = upload.parseRequest(request); //dmezac- ini
            Iterator iter = items.iterator();
            //boolean primero = true;
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                fieldname = item.getFieldName();
                String[] fcampo = fieldname.split("\\.");
                String psigno = "";
                if (item.isFormField()) {
                    // Process regular form field (input type="text|radio|checkbox|etc", select, etc). 
                    String fieldvalue = item.getString(); 
                    //String filedtype = item.getContentType();
                    System.out.println("Campo " + fieldname + " " + fieldvalue);
                    if (fieldvalue.trim().length() > 0 && !(fieldname.equals("Generar Excel")) ) {

                        if (fieldname.indexOf("_FEC")>0) //campo fecha
                            fieldvalue = "to_date('" + fieldvalue + "','YYYY-MM-DD')";
                        else if (!(fieldname.indexOf(".HID")>0))
                            fieldvalue = "'" + fieldvalue + "'";
    
                        try {
                            psigno = this.getSign(fcampo[1]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            psigno = "=";
                        }

                        if (fieldname.indexOf(".MULTIPLE")>0) { //lista multiple (solo UNA lista multiple por listado)
                            int j = Integer.parseInt(fcampo[3]) - 1 ; //obtenemos el indice del filtro
                            if (pfiltro2[j] == null)
                                pfiltro2[j] = fcampo[0].replaceAll("\\^", "\\.") + psigno + fieldvalue;
                            else
                                pfiltro2[j] = pfiltro2[j] + " or " + fcampo[0].replaceAll("\\^", "\\.") + psigno + fieldvalue;
                        }
                        else {
                            if (pfiltro == "")
                                pfiltro = pfiltro + fcampo[0].replaceAll("\\^", "\\.") + psigno + fieldvalue;
                            else
                                pfiltro = pfiltro + " and " + fcampo[0].replaceAll("\\^", "\\.") + psigno + fieldvalue;
                        }
                    }
                }
            }
            */  //dmezac- fin
            /*-----------------------------------------------------------------------------------------*/
            //Agregamos los filtros multiples al filtro principal
            /*for(int k=0; k<pfiltro2.length; k++) //dmezac- ini
                if (!(pfiltro2[k] == null)) 
                        pfiltro = pfiltro + " and (" + pfiltro2[k] + ")";

            System.out.println("Filtro: " + pfiltro);
            sql = sql.replaceAll("&P_FILTROS", pfiltro);
            System.out.println(sql);                                     
            st.close(); 
            rs2.close();
            */ //dmezac- fin
            
            //Nombre del archivo generado como adjunto
            Date fecha = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
            response.setHeader("Content-Disposition", "attachment; filename=" + nombre + sdf.format(fecha)+".xls");

            // ejecuto la consulta a la bd 
            //st = bd.conn.createStatement(); //dmezac-
            //rs2 = st.executeQuery(sql); //dmezac-

            //int cantColumnas = rs2.getMetaData().getColumnCount(); //dmezac-
            final int cantColumnas = 7; //dmezac+
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

            this.addLabel(excelSheet, 1, 0, "Caja:", fFecHor);
            this.addLabel(excelSheet, 2, 0, this.caja.getCajDesc(), fTexto);

            this.addLabel(excelSheet, 1, 1, "Saldo:", fFecHor);
            this.addNumber(excelSheet, 2, 1, this.caja.getCajSaldo().doubleValue(), fNumero);

            this.addLabel(excelSheet, cantColumnas-1, 0, "Fecha:", fFecHor);
            this.addDate(excelSheet, cantColumnas, 0, fecha, fFecha);

            this.addLabel(excelSheet, cantColumnas-1, 1, "Hora:", fFecHor);
            this.addDate(excelSheet, cantColumnas, 1, fecha, fHora);

            //Cargamos la cabecera 
            this.addLabel(excelSheet, 0, ++fila, "", formatoCab); //dmezac+
            //this.addLabel(excelSheet, 1, fila, "Caja", formatoCab); //dmezac+
            this.addLabel(excelSheet, 1, fila, "Tipo", formatoCab); //dmezac+
            this.addLabel(excelSheet, 2, fila, "Fecha", formatoCab); //dmezac+
            this.addLabel(excelSheet, 3, fila, "Moneda", formatoCab); //dmezac+
            this.addLabel(excelSheet, 4, fila, "Tipo de Cambio", formatoCab); //dmezac+
            this.addLabel(excelSheet, 5, fila, "Forma Pago", formatoCab); //dmezac+
            this.addLabel(excelSheet, 6, fila, "Monto", formatoCab); //dmezac+
            this.addLabel(excelSheet, 7, fila, "Observacion", formatoCab); //dmezac+

            excelSheet.setColumnView(8, cv);
            /*for(int j=0; j<=cantColumnas; j++) //dmezac- ini
            {
                if (!(j==0))
                    //System.out.println (rs2.getMetaData().getColumnName(j) + " tipo: " + rs2.getMetaData().getColumnType(j));
                    this.addLabel(excelSheet, j, fila, rs2.getMetaData().getColumnName(j), formatoCab);
                else
                    this.addLabel(excelSheet, j, ++fila, "", formatoCab);

                //Ajustar ancho automaticamente
                excelSheet.setColumnView(j, cv);
            } */ //dmezac- fin

            //Inmovilizamos paneles
            excelSheet.getSettings().setVerticalFreeze(fila+1);

            //Cargamos el detalle de las filas
            Iterator<ComMovCajas> movCajaIter = movCajas.iterator(); 
            int row = 0;
            while (movCajaIter.hasNext())
            {
                ComMovCajas movCaja = movCajaIter.next();
                
                //Cargamos las columnas //dmezac+ ini
                if(fila%2 == 0) //si es par
                {
                    this.addNumber(excelSheet, 0, ++fila, ++row, formatoTexto2);
                    //this.addLabel (excelSheet, 1, fila, movCaja.getCajId().getCajDesc(), formatoTexto2);
                    this.addLabel (excelSheet, 1, fila, movCaja.getMovTip().toString().replace("C", "Reposición").replace("D", "Egreso"), formatoTexto2);
                    this.addDate  (excelSheet, 2, fila, movCaja.getMovFecIns(), formatoFecha2);
                    this.addLabel (excelSheet, 3, fila, movCaja.getMonId().getMonAbreviatura(), formatoTexto2);
                    this.addNumber(excelSheet, 4, fila, movCaja.getMovCambio().doubleValue(), formatoNumero2);
                    this.addLabel (excelSheet, 5, fila, movCaja.getMovForPago(), formatoTexto2);
                    this.addNumber(excelSheet, 6, fila, movCaja.getMovMonto().doubleValue(), formatoNumero2);
                    this.addLabel (excelSheet, 7, fila, movCaja.getMovObservacion(), formatoTexto2);
                }
                else //si no es par
                {
                    this.addNumber(excelSheet, 0, ++fila, ++row, formatoTexto);
                    //this.addLabel (excelSheet, 1, fila, movCaja.getCajId().getCajDesc(), formatoTexto);
                    this.addLabel (excelSheet, 1, fila, movCaja.getMovTip().toString().replace("C", "Reposición").replace("D", "Egreso"), formatoTexto);
                    this.addDate  (excelSheet, 2, fila, movCaja.getMovFecIns(), formatoFecha);
                    this.addLabel (excelSheet, 3, fila, movCaja.getMonId().getMonAbreviatura(), formatoTexto);
                    this.addNumber(excelSheet, 4, fila, movCaja.getMovCambio().doubleValue(), formatoNumero);
                    this.addLabel (excelSheet, 5, fila, movCaja.getMovForPago(), formatoTexto);
                    this.addNumber(excelSheet, 6, fila, movCaja.getMovMonto().doubleValue(), formatoNumero);
                    this.addLabel (excelSheet, 7, fila, movCaja.getMovObservacion(), formatoTexto);
                }
                //dmezac+ fin

                //Recorremos las columnas
                /*for(int j=1; j<=cantColumnas; j++) //dmezac- ini
                {
                    //System.out.println (rs2.getString(j));
                    if (rs2.getMetaData().getColumnType(j) == 2) // 2 --> tipo numerico
                        if(fila%2 != 0) //si no es par
                            this.addNumber(excelSheet, j, fila, rs2.getInt(j), formatoTexto2);
                        else
                            this.addNumber(excelSheet, j, fila, rs2.getInt(j), formatoTexto);
                    else if (rs2.getMetaData().getColumnType(j) == 93) // 93 --> tipo fecha
                        if(fila%2 != 0) //si no es par
                            this.addDate(excelSheet, j, fila, rs2.getDate(j), formatoFecha2);
                        else
                            this.addDate(excelSheet, j, fila, rs2.getDate(j), formatoFecha);
                    else    // 12 --> tipo texto
                        if(fila%2 != 0) //si no es par
                            this.addLabel(excelSheet, j, fila, rs2.getString(j), formatoTexto2);
                        else
                            this.addLabel(excelSheet, j, fila, rs2.getString(j), formatoTexto);
                }
                */ //dmezac- fin
            }
            //Cargamos el pie de pagina
            excelSheet.mergeCells(1, ++fila, cantColumnas, fila);
            this.addLabel(excelSheet, 1, fila, "* * * FIN LISTADO * * *", fPie);
            this.addLabel(excelSheet, 0, fila, null, fPie);

            //Creamos la 2da hoja con el detalle del SQL y Filtro
            /* //dmezac- ini
            WritableSheet excelSheet2 = workbook.createSheet("ORIGEN", 1);
            this.addLabel(excelSheet2, 0, 0, "Listado:", fFecHor);
            this.addLabel(excelSheet2, 1, 0, nombre, fFecha);

            this.addLabel(excelSheet2, 0, 1, "Det. SQL:", fFecHor);
            this.addLabel(excelSheet2, 1, 1, sql, fFecha);

            this.addLabel(excelSheet2, 0, 2, "Filtro:", fFecHor);
            if (pfiltro.trim().length() > 0)
                this.addLabel(excelSheet2, 1, 2, pfiltro, fFecha);
            else
                this.addLabel(excelSheet2, 1, 2, "(Sin filtro)", fFecha);
            */ //dmezac- fin

            //Escribimos el archivo y cerramos
            workbook.write();
            workbook.close();
/*-----------------------------------------------------------------------------------------*/            
            //st.close(); //dmezac-
            //rs2.close(); //dmezac-

            //bd.cerrar(); //dmezac-
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally  {
            //rs2 = null; //dmezac-
            //st = null;  //dmezac-
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

    private String getSign(String pclave) {
        String valRetorno = "";
        switch (pclave) {
            case "MAYOR_IGUAL":
                valRetorno = ">="; break;
            case "MENOR_IGUAL":
                valRetorno = "<="; break;
            case "MAYOR":
                valRetorno = ">"; break;
            case "MENOR":
                valRetorno = "<"; break;
            case "IGUAL":
                valRetorno = "="; break;
        }
        return valRetorno;
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
