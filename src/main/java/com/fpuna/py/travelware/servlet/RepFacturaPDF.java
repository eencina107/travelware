package com.fpuna.py.travelware.servlet;

import com.fpuna.py.travelware.dao.CobroDao;
import com.fpuna.py.travelware.model.PagCobros;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.ejb.EJB;

public class RepFacturaPDF extends HttpServlet {
    private static final String CONTENT_TYPE = "application/pdf";
    private String user, pwd, usuario, clave;
    private int redondeo = 0;

    //private String sentencia = "";
    private PagCobros cobro; //dmezac+

    @EJB
    private CobroDao cobroEJB; //dmezac+

    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Content-disposition", "inline; filename='sampleName.pdf'");

        //Obtengo datos de la sesion
        HttpSession session = request.getSession();
        user = (String)session.getAttribute("user");
        pwd = (String)session.getAttribute("pwd");
        user = "user";
        pwd = "pwd";

        ServletOutputStream out = response.getOutputStream();
        // leo el parametro Cliente
        //String nombre = request.getParameter("rep");
        String id = request.getParameter("id");
        String tp = request.getParameter("tp");
        if (tp == null)
            tp = "fac";

        String nombre = "Factura";
        if(tp.equals("ncr"))
            nombre = "Nota_Credito";

        this.cobro = cobroEJB.getById(Integer.parseInt(id));
        //this.sentencia = "select a.tac_desc, a.tac_sql from PGE_TABLAS_CAB a where a.tac_nom = upper('"+nombre+"') and a.tac_tip = 'L'";
        //System.out.println(this.sentencia);                                     

        try {
            String tipComprobante = "FACTURA";
            if(tp.equals("ncr"))
                tipComprobante = "NOTA DE CR&Eacute;DITO";

            String nroFactura = cobro.getCobFacNro();
            if(tp.equals("ncr"))
                nroFactura = cobro.getCobNcrNro();

            String desc = "tac_desc";
            String ruc = "80058016-8";
            String nombre1 = "San Rafael Peregrinaciones";
            String nombre2 = "Viajes y Turismo S.R.L.";
            String direccion = "Cruz del Chaco 1703 c/ Alfredo Seiferheld";
            String telefono = "(+595 21) 623 496";
            String celular = "(+595 971) 954 480";
            String email = "sanrafael@hipuu.com.py";
            String actividad = "&nbsp;"; //"BANCO";
            String timbrado = "11293569"; //timbrado
            String vigenciaIni = "01/08/2016"; //inicio vigencia
            String vigenciaFin = "31/08/2017"; //fin vigencia
            String condicion = "CONTADO";

            String fecEmision;
            if(tp.equals("fac")) {
                if (cobro.getCobFecPago() != null)
                    fecEmision = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(cobro.getCobFecPago());
                else
                    fecEmision = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(new Date());
            }
            else {
                if (cobro.getCobFecAnu() != null)
                    fecEmision = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(cobro.getCobFecAnu());
                else
                    fecEmision = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(new Date());
            }

            String nomCliente = (cobro.getPerId().getPerNom() + " " + cobro.getPerId().getPerApe()).toUpperCase();
            String docCliente = cobro.getPerId().getPerNroDoc();
            String dirCliente = cobro.getPerId().getPerDir();

            String concepto = cobro.getViaId().getViaDesc() + " - " + new SimpleDateFormat("dd/MM/yyyy").format(cobro.getCobFecIns())+ " - Cuota " + cobro.getCobNro();
            if (!cobro.getMonId().getMonAbreviatura().equalsIgnoreCase("GS"))
                redondeo = 2;
            BigDecimal monTotal = cobro.getCobMonto().divide(BigDecimal.valueOf(1), redondeo, RoundingMode.HALF_EVEN);
            BigDecimal monIva = cobro.getCobMonto().divide(BigDecimal.valueOf(11), redondeo, RoundingMode.HALF_EVEN);
            String monLetras = cobro.getCobMontoLetras();
            
            //Nombre del archivo generado como adjunto
            SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
            response.setHeader("Content-Disposition", "inline; filename=" + nombre.toUpperCase() + sdf.format(new Date())+".pdf");

/* Operaciones con PDF -------------------------------------------------------------------*/
            // Creamos el documento
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, out);
            //Modificamos los atributos del documento
            document.addAuthor(user);
            document.addCreationDate();
            document.addProducer();
            document.addCreator("Travelware");
            document.addTitle(desc);
            document.setPageSize(PageSize.LETTER);

            document.open();
            //Escribir pdf a partir de un string
/*-----------------------------------------------------------------------------------------------------------*/

            String phtml =  "<html><head>\n" +
                            "<title>" + nombre + "</title>\n" +
                            "<style>\n" +
                            "#subtitulo {\n" +
                            " font-size: 11px;\n" +
                            "}\n" +
                            "#titulo {\n" +
                            " font-size: 18px;\n" +
                            "}\n" +
                            ".celda {\n" +
                            " padding: 4px;\n" +
                            " }\n" +
                            " \n" +
                            ".celda2 {\n" +
                            "    font-family: 'Trebuchet MS', Verdana, Arial;\n" +
                            "    font-size: 8px;\n" +
                            "    padding: 4px;\n" +
                            "    border-top-style: none;\n" +
                            "    border-right-style: solid;\n" +
                            "    border-bottom-style: dotted;\n" +
                            "    border-left-style: solid; \n" +
                            " } \n" +
                            "\n" +
                            "</style>\n" +
                            "<style>\n" +
                            "table, th, td {\n" +
                            "    font-family: 'Trebuchet MS', Verdana, Arial;\n" +
                            "    font-size: 12px;\n" +
                            "}\n" +
                            "th, td {\n" +
                            "    padding: 0px;\n" +
                            "}\n" +
                            "</style>\n" +
                            "\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<table width=700 style=\"margin-top:5px;\" scellspacing=\"0\"   border=0>\n" +
                            "<tr>\n" +
                            "<td width=\"64%\">\n" +
                            "<table width=\"451\" cellspacing=0 border=0 \">\n" +
                            "<tr>\n" +
                            "<td colspan=\"3\"><div><img src=\"#UBI_IMG/lefttop.jpg\" height=\"6\" width=\"6\"></img>\n" +
                            "<img src=\"#UBI_IMG/top.jpg\" height=\"6\" width=\"431\"></img>\n" +
                            "<img src=\"#UBI_IMG/righttop.jpg\" height=\"6\" width=\"6\"></img></div></td>\n" +
                            "</tr>\n" +
                            "<tr width=\"200\">\n" +
                            "<td><img src=\"#UBI_IMG/left.jpg\" height=\"140\" width=\"6\"></img></td>\n" +
                            "<td><table cellspacing=\"0\" border=0><tr>\n" +
                            "<td rowspan=\"3\" width=\"65%\"><img src=\"#UBI_LOGO/logo.png\" height=\"46\" width=\"230\"></img></td>\n" +
                            "<td align=\"center\" bgcolor=\"#ffffff\">&nbsp;</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" bgcolor=\"#ffffff\">" + nombre1 + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"subtitulo\">" + nombre2 + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" colspan=\"2\">&nbsp;</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" colspan=\"2\">&nbsp;</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"subtitulo\" colspan=\"2\">" + direccion + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"subtitulo\" colspan=\"2\">Telefax: " + telefono + "   </td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" colspan=\"2\">Cel.: " + celular + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" colspan=\"2\">E-mail: " + email + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"subtitulo\" colspan=\"2\">Asuncion - Paraguay</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" colspan=\"2\">" + actividad + "</td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</td>\n" +
                            "<td><img src=\"#UBI_IMG/right.jpg\" height=\"140\" width=\"6\"></img></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td colspan=\"3\"><div><img src=\"#UBI_IMG/leftdown.jpg\" height=\"6\" width=\"6\"></img>\n" +
                            "<img src=\"#UBI_IMG/down.jpg\" height=\"6\" width=\"431\"></img>\n" +
                            "<img src=\"#UBI_IMG/rightdown.jpg\" height=\"6\" width=\"6\"></img></div></td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</td><td width=\"35%\">\n" +
                            "<table width=\"242\" cellspacing=0 border=0 \">\n" +
                            "<tr>\n" +
                            "<td colspan=\"3\"><div><img src=\"#UBI_IMG/lefttop.jpg\" height=\"6\" width=\"6\"></img>\n" +
                            "<img src=\"#UBI_IMG/top.jpg\" height=\"6\" width=\"227\"></img>\n" +
                            "<img src=\"#UBI_IMG/righttop.jpg\" height=\"6\" width=\"6\"></img></div></td>\n" +
                            "</tr>\n" +
                            "<tr width=\"200\">\n" +
                            "<td><img src=\"#UBI_IMG/left.jpg\" height=\"141\" width=\"6\"></img></td>\n" +
                            "<td><table cellspacing=\"0\" border=0><tr>\n" +
                            "<td align=\"center\" bgcolor=\"#ffffff\">Timbrado No." + timbrado + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"subtitulo\">Fecha Inicio Vigencia " + vigenciaIni + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"subtitulo\">Fecha Fin Vigencia  " + vigenciaFin + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"titulo\"><b>RUC: " + ruc + "</b></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" bgcolor=\"#ffffff\">&nbsp;</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"titulo\"><b>" + tipComprobante + "</b></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" bgcolor=\"#ffffff\">&nbsp;</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" id=\"titulo\"><b>" + nroFactura + "</b></td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</td>\n" +
                            "<td><img src=\"#UBI_IMG/right.jpg\" height=\"141\" width=\"6\"></img></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td colspan=\"3\"><div><img src=\"#UBI_IMG/leftdown.jpg\" height=\"6\" width=\"6\"></img>\n" +
                            "<img src=\"#UBI_IMG/down.jpg\" height=\"6\" width=\"227\"></img>\n" +
                            "<img src=\"#UBI_IMG/rightdown.jpg\" height=\"6\" width=\"6\"></img></div></td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "<table width=\"693\" cellspacing=0 border=0 style=\"margin-top:3px;\"\">\n" +
                            "<tr>\n" +
                            "<td colspan=\"3\"><div><img src=\"#UBI_IMG/lefttop.jpg\" height=\"6\" width=\"6\"></img>\n" +
                            "<img src=\"#UBI_IMG/top.jpg\" height=\"6\" width=\"680\"></img>\n" +
                            "<img src=\"#UBI_IMG/righttop.jpg\" height=\"6\" width=\"6\"></img></div></td>\n" +
                            "</tr>\n" +
                            "<tr width=\"200\">\n" +
                            "<td><img src=\"#UBI_IMG/left.jpg\" height=\"82\" width=\"6\"></img></td>\n" +
                            "<td><table width=680  cellspacing=\"0\" border=0 style=\"margin-top:5px;\"><tr>\n" +
                            "<td class=celda width=\"20%\">Fecha de Emisi&oacute;n:</td>\n" +
                            "<td align=\"left\" bgcolor=\"#ffffff\">" + fecEmision + "</td>\n" +
                            "<td align=\"right\" class=celda width=\"30%\">Condici&oacute;n de Venta: " + condicion + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td width=100 class=celda bgcolor=\"#ffffff\">R.U.C./C.I.:</td>\n" +
                            "<td class=celda colspan=\"2\">" + docCliente + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td width=100 class=celda bgcolor=\"#ffffff\">Nombre o Raz&oacute;n Social:</td>\n" +
                            "<td class=celda colspan=\"2\">" + nomCliente + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td width=100 class=celda bgcolor=\"#ffffff\">Direcci&oacute;n:</td>\n" +
                            "<td class=celda colspan=\"2\">" + dirCliente + "</td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</td>\n" +
                            "<td><img src=\"#UBI_IMG/right.jpg\" height=\"82\" width=\"6\"></img></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td colspan=\"3\"><div><img src=\"#UBI_IMG/leftdown.jpg\" height=\"6\" width=\"6\"></img>\n" +
                            "<img src=\"#UBI_IMG/down.jpg\" height=\"6\" width=\"680\"></img>\n" +
                            "<img src=\"#UBI_IMG/rightdown.jpg\" height=\"6\" width=\"6\"></img></div></td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "<table width=691 cellspacing=\"0\" border=1  style=\"margin-top:5px; margin-left:10px; border-top-style: none;\">\n" +
                            "<tr>\n" +
                            "<td width=60 class=celda rowspan=\"2\"><b>CANTIDAD</b></td>\n" +
                            "<td width=170 align=\"center\" class=celda rowspan=\"2\"><b>DESCRIPCI&Oacute;N</b></td>\n" +
                            "<td width=85 align=\"center\" class=celda rowspan=\"2\"><b>PRECIO UNITARIO</b></td>\n" +
                            "<td width=255 align=\"center\" class=celda colspan=\"3\"><b>VALOR DE VENTA</b></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td width=85 align=\"center\" class=celda bgcolor=\"#ffffff\"><b>Exentas</b></td>\n" +
                            "<td width=85 align=\"center\" class=celda bgcolor=\"#ffffff\"><b>5%</b></td>\n" +
                            "<td width=85 align=\"center\" class=celda bgcolor=\"#ffffff\"><b>10%</b></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td width=60 class=celda bgcolor=\"#ffffff\">1</td>\n" +
                            "<td width=190 class=celda bgcolor=\"#ffffff\">" + concepto + "</td>\n" +
                            "<td width=85 align=\"right\" class=celda bgcolor=\"#ffffff\">" + monTotal + "</td>\n" +
                            "<td width=85 align=\"right\" class=celda bgcolor=\"#ffffff\">               0</td>\n" +
                            "<td width=85 align=\"right\" class=celda bgcolor=\"#ffffff\">               0</td>\n" +
                            "<td width=85 align=\"right\" class=celda bgcolor=\"#ffffff\">" + monTotal + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td class=celda colspan=\"3\">Sub Total</td>\n" +
                            "<td align=\"right\" class=celda bgcolor=\"#ffffff\">               0</td>\n" +
                            "<td align=\"right\" class=celda bgcolor=\"#ffffff\">               0</td>\n" +
                            "<td align=\"right\" class=celda bgcolor=\"#ffffff\">" + monTotal + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"center\" class=celda colspan=\"1\">Total a Pagar</td>\n" +
                            "<td class=celda colspan=\"4\">" + monLetras + ".-</td>\n" +
                            "<td align=\"right\" class=celda bgcolor=\"#ffffff\">" + monTotal + "</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td class=celda colspan=\"2\">Liquidaci&oacute;n del IVA (5%)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;               0</td>\n" +
                            "<td align=\"left\" class=celda colspan=\"2\">(10%)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + monIva + "</td>\n" +
                            "<td align=\"left\" bgcolor=\"#ffffff\">Total de IVA </td>\n" +
                            "<td align=\"right\" class=celda bgcolor=\"#ffffff\">" + monIva + "</td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "<table width=\"693\" cellspacing=0 border=0 \">\n" +
                            "<tr width=\"200\">\n" +
                            "<td><img src=\"#UBI_IMG/left.jpg\" height=\"40\" width=\"6\"></img></td>\n" +
                            "<td><table width=695 cellspacing=\"0\" border=0\"><tr>\n" +
                            "<td width=500 align=\"left\" id=\"subtitulo\">Habilitaci&oacute;n de la SET N° 1007 - Agosto/2013</td>\n" +
                            "<td width=190 align=\"right\" id=\"subtitulo\">Original: Cliente</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"left\" bgcolor=\"#ffffff\">&nbsp;</td>\n" +
                            "<td width=190 align=\"right\" id=\"subtitulo\">Duplicado: Archivo Tributario</td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td align=\"left\" bgcolor=\"#ffffff\">&nbsp;</td>\n" +
                            "<td width=190 align=\"right\" id=\"subtitulo\">Triplicado: Contabilidad</td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</td>\n" +
                            "<td><img src=\"#UBI_IMG/right.jpg\" height=\"40\" width=\"6\"></img></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                            "<td colspan=\"3\"><div><img src=\"#UBI_IMG/leftdown.jpg\" height=\"6\" width=\"6\"></img>\n" +
                            "<img src=\"#UBI_IMG/down.jpg\" height=\"6\" width=\"680\"></img>\n" +
                            "<img src=\"#UBI_IMG/rightdown.jpg\" height=\"6\" width=\"6\"></img></div></td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</body></html>";        

            phtml = phtml.replaceAll("#UBI_IMG/","http://localhost:8080/py.travelware/resources/img/reppdf/");
            phtml = phtml.replaceAll("#UBI_LOGO/","http://localhost:8080/py.travelware/resources/");
/*-----------------------------------------------------------------------------------------------------------*/
            //System.out.println(phtml);

            //Obtenemos el texto cargado en el String Contenido
            InputStream is = new ByteArrayInputStream(phtml.getBytes());

            //Seteamos el formato
            CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(false);
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());            
            Pipeline<?> pipeline =  new CssResolverPipeline(cssResolver, new HtmlPipeline(htmlContext, new PdfWriterPipeline(document, writer)));
            XMLWorker worker = new XMLWorker(pipeline, true);
            XMLParser p = new XMLParser(worker); 
            p.parse(is);

            //Volcamos el contenido al documento PDF
            //XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);

            //Cerramos el documento
            document.close();
            is.close();
            System.out.println( "PDF Created!!" );
/*-----------------------------------------------------------------------------------------*/            
        } catch (Exception e) {
            e.printStackTrace();
        }
        // cierro la respuesta
        out.close();
    }

    public void doPost(HttpServletRequest request, 
                       HttpServletResponse response) throws ServletException, IOException {response.setContentType(CONTENT_TYPE);
        // invoco al doGet con los parametros recibidos
        doGet(request, response);  
    }

}
