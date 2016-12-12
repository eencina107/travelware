/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.CobroDao;
import com.fpuna.py.travelware.dao.PasajeroDao;
import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.dao.PrecioViajeDao;
import com.fpuna.py.travelware.dao.ViajeDao;
import com.fpuna.py.travelware.dao.PasaporteDao;
import com.fpuna.py.travelware.dao.GastoDao;
import com.fpuna.py.travelware.dao.ViajeDetDao;
import com.fpuna.py.travelware.model.PagCobros;
import com.fpuna.py.travelware.model.PgePersonas;
import com.fpuna.py.travelware.model.ViaPasajeros;
import com.fpuna.py.travelware.model.ViaPreViajes;
import com.fpuna.py.travelware.model.ViaViajes;
import com.fpuna.py.travelware.model.ViaPasaportes;
import com.fpuna.py.travelware.model.ViaGastos;
import com.fpuna.py.travelware.model.ViaViajesDet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "pasajeroBean")
@ViewScoped
public class PasajeroBean implements Serializable {

    private List<ViaPasajeros> pasajeros;
    private List<ViaViajes> viajes;
    private List<PgePersonas> personas;
    private List<ViaPreViajes> precViajes;
    private List<ViaPasaportes> pasaportes;
    private List<ViaGastos> gastos;
    private List<ViaViajesDet> viajesDet;

    private ViaPasajeros pasajeroSelected;
    private ViaViajes viajeSelected;
    
    @EJB
    private PasajeroDao pasajeroEJB;
    @EJB
    private ViajeDao viajeEJB;
    @EJB
    private PersonaDao personaEJB;
    @EJB
    private PrecioViajeDao precViajeEJB;
    @EJB
    private PasaporteDao pasaporteEJB;
    @EJB
    private GastoDao gastoEJB;
    @EJB
    private ViajeDetDao viajeDetEJB;
    @EJB
    private CobroDao cobroEJB;

    private LoginBean loginBean;
    
    private boolean habilitado;
    private int canCuotas;
    
    //crea una nueva instancia de Pasajero
    public PasajeroBean() {

    }

    @PostConstruct
    public void init() {
        this.clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        this.loginBean = (LoginBean) session.getAttribute("loginBean");
        this.pasajeroSelected = new ViaPasajeros();
        this.viajeSelected = new ViaViajes();
        this.pasajeros = pasajeroEJB.getAll();
        this.viajes = viajeEJB.getAllDisp();
        this.personas = personaEJB.getAll();
        this.gastos = gastoEJB.getAll(this.pasajeroSelected);
        this.habilitado = true;
    }

    private void clean() {
        this.pasajeroSelected = new ViaPasajeros();
        this.viajeSelected = new ViaViajes();
    }

    public void buttonAction(ActionEvent actionEvent) {
        this.pasajeroSelected = new ViaPasajeros();
        habilitado = true;
    }

    public void buttonAction2(ViaViajes viaje) {
        this.pasajeroSelected = new ViaPasajeros();
        this.pasajeroSelected.setViaId(viaje);
        habilitado = true;
    }

    public void addPasajero() {
        boolean nuevo = false;
        FacesContext context = FacesContext.getCurrentInstance();
        if (pasajeros != null) {
            for (ViaPasajeros pas : pasajeros) {
                if (pas.getPerId().equals(this.pasajeroSelected.getPerId()) && pas.getViaId().equals(this.pasajeroSelected.getViaId()) && this.pasajeroSelected.getPviId() == null ) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                             "Advertencia. " + "Esta persona ya se encuentra registrada para el viaje "+pas.getViaId().getViaDesc(), ""));
                    //this.clean();
                    return;
                }
            }
        }

        this.pasaportes = pasaporteEJB.getAll();
        Integer cantPas = pasaporteEJB.getCantPasaportes(this.pasajeroSelected.getPerId());
        if (cantPas.equals(0)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                     "Advertencia. " + this.pasajeroSelected.getPerId().getPerNom()+" "+this.pasajeroSelected.getPerId().getPerApe()+" no posee pasaporte. Verifique.", ""));
            //this.clean();
            return;
        }

        if(!viajeEJB.isDisponible(this.pasajeroSelected.getViaId())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. "+this.pasajeroSelected.getViaId().getViaDesc()+" no dispoble para la venta. Verifique.", ""));
            return;
        }

        ViaPasajeros pasajero = new ViaPasajeros();
        if (this.pasajeroSelected.getPviId() != null) { //modificado
            pasajero.setPviId(this.pasajeroSelected.getPviId());
            pasajero.setPviUsuIns(this.pasajeroSelected.getPviUsuIns());
            pasajero.setPviFecIns(this.pasajeroSelected.getPviFecIns());
            pasajero.setPviUsuMod(loginBean.getUsername());
            pasajero.setPviFecMod(new Date());
        } else {
            nuevo = true;
            pasajero.setPviUsuIns(loginBean.getUsername());
            pasajero.setPviFecIns(new Date());
        }
        pasajero.setViaId(this.pasajeroSelected.getViaId());
        pasajero.setPerId(this.pasajeroSelected.getPerId());
        pasajero.setPasPrefAsi(this.pasajeroSelected.getPasPrefAsi());
        pasajero.setPasPrefComi(this.pasajeroSelected.getPasPrefComi());
        pasajero.setPasRel(this.pasajeroSelected.getPasRel());
        pasajeroEJB.update(pasajero);

        if (nuevo) {
            Integer cantVend = this.pasajeroSelected.getViaId().getViaCantVend();
            //Actualizamos la cantidad de pasajes vendidos en el viaje
            pasajero.getViaId().setViaCantVend(++cantVend);
            viajeEJB.update(pasajero.getViaId());

            pasajero = pasajeroEJB.getByViaIdPerId(pasajero.getViaId(), pasajero.getPerId());

            //Agregamos los gastos del pasajero
            this.viajesDet = viajeDetEJB.getAll(pasajero.getViaId());
            for (ViaViajesDet vd :this.viajesDet) {
                if (vd.getVidTip() == 'I') {
                    ViaGastos gasto = new ViaGastos();
                    gasto.setPviId(pasajero);
                    gasto.setGasMonto(vd.getVidMonto());
                    gasto.setGasNro(vd.getVidId());
                    gasto.setConId(vd.getConId());
                    gasto.setGasTip(vd.getConId().getConDesc().substring(0, 3));
                    gasto.setMonId(vd.getMonId());
                    gastoEJB.update(gasto);
                }
            }

            int canCuotas = this.getCanCuotas(), redondeo = 0;
            if (!pasajero.getViaId().getMonId().getMonAbreviatura().equalsIgnoreCase("GS"))
                redondeo = 2;
            BigDecimal montoCuota = pasajero.getViaId().getViaCost().divide(BigDecimal.valueOf(canCuotas), redondeo, RoundingMode.HALF_EVEN);
            //Agregamos las cuotas del pasajero
            for (int i=1; i<=canCuotas; i++) {
                PagCobros cobro = new PagCobros();

                cobro.setCobUsuIns(loginBean.getUsername());
                cobro.setCobFecIns(new Date());

                cobro.setCobMonto(montoCuota);
                cobro.setMonId(pasajero.getViaId().getMonId());
                cobro.setCobNro(Integer.toString(i));
                cobro.setCobEstado('I'); //Ingresado se actualiza en el momento del pago
                cobro.setCobForPago("N"); //se actualiza en el momento del pago
                //cobro.setCobCambio(this.cobroSelected.getCobCambio()); //se actualiza en el momento del pago
                //cobro.setCobMontoLetras(""); //se actualiza en el momento del pago
                //cobro.setCobObservacion(""); //se actualiza en el momento del pago
                //cobro.setCobFecPago(""); //se actualiza en el momento del pago
                //cobro.setCobFacNro(""); //se actualiza en el momento del pago
                //cobro.setCobNcrNro(""); //se actualiza en el momento de la anulacion del pago
                if (i == canCuotas)
                    cobro.setCobTipo("CAN");
                else
                    cobro.setCobTipo("CTA");
                cobro.setViaId(pasajero.getViaId());
                cobro.setCobAnulado('N');
                cobro.setPerId(pasajero.getPerId());
                cobroEJB.update(cobro);
            }
        }
        context.addMessage("Mensaje", new FacesMessage("Felicidades! El pasajero fue guardado con éxito", ""));
        pasajeros = pasajeroEJB.getAll();
        viajes = viajeEJB.getAllDisp();
        RequestContext.getCurrentInstance().update("pasajero-form");
        RequestContext.getCurrentInstance().update("viaje-form");
        this.clean();
        RequestContext.getCurrentInstance().execute("PF('dlgPasajeroAdd').hide();");
    }

    public void deletePasajero() {
        FacesContext context = FacesContext.getCurrentInstance();
        BigDecimal montoPagado = cobroEJB.getMontoPagado(this.pasajeroSelected.getPerId(), this.pasajeroSelected.getViaId());
        if (montoPagado.equals(BigDecimal.ZERO)) {
            //Borramos los gastos del pasajero
            this.gastos = gastoEJB.getAll(this.pasajeroSelected);
            for (ViaGastos gasto :this.getGastos()) {
                gastoEJB.delete(gasto);
            }
            this.gastos = gastoEJB.getAll(this.pasajeroSelected);

            //Borramos el pasajero
            pasajeroEJB.delete(this.pasajeroSelected);
            context.addMessage(null, new FacesMessage("Felicidades! El pasajero fue borrado con éxito.", ""));

            Integer cantVend = this.pasajeroSelected.getViaId().getViaCantVend();
            //Actualizamos la cantidad de pasajes vendidos en el viaje
            this.pasajeroSelected.getViaId().setViaCantVend(--cantVend);
            viajeEJB.update(this.pasajeroSelected.getViaId());

            pasajeros = pasajeroEJB.getAll();
            viajes = viajeEJB.getAllDisp();
            RequestContext.getCurrentInstance().update("pasajero-form");
            this.clean();
            RequestContext.getCurrentInstance().execute("PF('dlgPasajeroAdd').hide();");
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia. El pasajero tiene pagos realizados para "+this.pasajeroSelected.getViaId().getViaDesc()+". Verifique.", ""));
            return;
        }
    }

    public void onRowSelect(SelectEvent event) {
        this.pasajeroSelected = (ViaPasajeros) event.getObject();
        RequestContext.getCurrentInstance().update("pasajero-form:dtPasajero");
    }

    public List<ViaPasajeros> getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(List<ViaPasajeros> pasajeros) {
        this.pasajeros = pasajeros;
    }

    public List<ViaViajes> getViajes() {
        return viajes;
    }

    public void setViajes(List<ViaViajes> viajes) {
        this.viajes = viajes;
    }

    public List<ViaGastos> getGastos() {
        return gastos;
    }

    public void setGastos(List<ViaGastos> gastos) {
        this.gastos = gastos;
    }

    public ViaPasajeros getPasajeroSelected() {
        return pasajeroSelected;
    }

    public void setPasajeroSelected(ViaPasajeros pasajeroSelected) {
        this.pasajeroSelected = pasajeroSelected;
    }

    public ViaViajes getViajeSelected() {
        return viajeSelected;
    }

    public void setViajeSelected(ViaViajes viajeSelected) {
        this.viajeSelected = viajeSelected;
        pasajeros = pasajeroEJB.getAll(viajeSelected);
        RequestContext.getCurrentInstance().update("pasajero-form:dtPasajero");
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public List<ViaPreViajes> getPrecViajes() {
        precViajes = precViajeEJB.getAll(this.viajeSelected);
        return precViajes;
    }

    public void setPrecViajes(List<ViaPreViajes> precViajes) {
        this.precViajes = precViajes;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }   

    public String getSimpleDateFormat(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }

    /**
     * @return the canCuotas
     */
    public int getCanCuotas() {
        return canCuotas;
    }

    /**
     * @param canCuotas the canCuotas to set
     */
    public void setCanCuotas(int canCuotas) {
        this.canCuotas = canCuotas;
    }
}
