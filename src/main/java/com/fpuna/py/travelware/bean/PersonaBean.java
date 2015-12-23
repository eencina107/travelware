/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.py.travelware.bean;

import com.fpuna.py.travelware.dao.PaisDao;
import com.fpuna.py.travelware.dao.PersonaDao;
import com.fpuna.py.travelware.dao.ProfesionDao;
import com.fpuna.py.travelware.model.PgePaises;
import com.fpuna.py.travelware.model.PgePersonas;
import com.fpuna.py.travelware.model.PgeProfesiones;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import static org.hibernate.internal.util.io.StreamCopier.BUFFER_SIZE;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author eencina
 */
@Named(value = "personaBean")
@SessionScoped
public class PersonaBean implements Serializable{
    private List<PgePersonas> personas;
    private List<PgeProfesiones> profesiones;
    private List<PgePaises> paises;
    private PgePersonas personaSelected;
    @EJB
    private PersonaDao personaEJB;
    @EJB
    private ProfesionDao profesionEJB;
    @EJB
    private PaisDao paisEJB;
    
    private LoginBean loginBean;
    
    private PgePaises pais;
    private PgeProfesiones profesion;
    
    //Utilizados en Agregar
    private String nom;
    private String ape;
    private String nroDoc;
    private Date fecNac;
    private String lugNac;
    private String email;
    private String docImg;
    
    //crea una nueva instancia de Persona
    public PersonaBean(){
        
    }
    
    @PostConstruct
    public void init(){
        clean();
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loginBean = (LoginBean) session.getAttribute("loginBean");
        personaSelected = new PgePersonas();
        profesion = new PgeProfesiones();
        personaSelected.setPrfId(profesion);
        pais = new PgePaises();
        personaSelected.setPaiId(pais);
        paises = paisEJB.getAll();
        profesiones = profesionEJB.getAll();
        personas= personaEJB.getAll();
    }

    private void clean() {
        this.personaSelected = new PgePersonas();
    }
    
    public void buttonAction(ActionEvent actionEvent){
        personaSelected = new PgePersonas();
    }
    
    public void addPersona(){
        FacesContext context = FacesContext.getCurrentInstance();
        String mensaje;
        for (PgePersonas per:personas){
            if (per.getPerNroDoc().equals(this.personaSelected.getPerNroDoc()) && this.personaSelected.getPerId()==null){
                context.addMessage(null, new FacesMessage("Advertencia", "Ya existe una persona con este documento: "+this.personaSelected.getPerNroDoc()));
                this.clean();
                return;
              
            }
        }
        
        PgePersonas persona = new PgePersonas();
        persona.setPerNroDoc(this.personaSelected.getPerNroDoc());
        if (this.personaSelected.getPerId()!=null){
            persona.setPerUsuMod(loginBean.getUsername());
            persona.setPerFecMod(new Date());
            persona.setPerDoc(this.personaSelected.getPerDoc());
            persona.setPerId(this.personaSelected.getPerId());
            persona.setPerUsuIns(this.personaSelected.getPerUsuIns());
            persona.setPerFecIns(this.personaSelected.getPerFecIns());
        }
        else{
            persona.setPerUsuIns(loginBean.getUsername());
            persona.setPerFecIns(new Date());
            persona.setPerDoc(docImg);
        }
        persona.setPaiId(this.personaSelected.getPaiId());
        persona.setPrfId(this.personaSelected.getPrfId());
        persona.setPerNom(this.personaSelected.getPerNom());
        persona.setPerApe(this.personaSelected.getPerApe());
        persona.setPerNroDoc(this.personaSelected.getPerNroDoc());
        persona.setPerLugNac(this.personaSelected.getPerLugNac());
        persona.setPerFecNac(this.personaSelected.getPerFecNac());
        persona.setPerEmail(this.personaSelected.getPerEmail());
        personaEJB.update(persona);
        context.addMessage(null, new FacesMessage("Felicidades!", persona.getPerNom()+" "+persona.getPerApe()+" fue guardado con éxito"));
        personas = personaEJB.getAll();
        RequestContext.getCurrentInstance().update("personas-form:dtPersona");
        this.clean();
    }
    
    public void deletePersona(){
        personaEJB.delete(personaSelected);
        personas = personaEJB.getAll();
        RequestContext.getCurrentInstance().update("persona-form:dtPersona");
    }
    
    public void onRowSelect(SelectEvent event){
        this.personaSelected = (PgePersonas) event.getObject();
        RequestContext.getCurrentInstance().update("personas-form:dtPersona");
    }
    
    public void handleFileUpload(FileUploadEvent event) {
         ExternalContext extContext = javax.faces.context.FacesContext.getCurrentInstance().getExternalContext();
         Boolean existeDir; 
         File targetFolder;
         File result;
            try {
                targetFolder = new File("/opt/py.travelware/perDoc/");
                result = new File("/opt/py.travelware/perDoc/"+event.getFile().getFileName());
                existeDir = targetFolder.canRead();
                if (!existeDir){
                    targetFolder.mkdirs();
                }
                //this.getClass().getResource("/").getPath(); 
                
                System.out.println("/opt/py.travelware/perDoc/" + event.getFile().getFileName()+" subido");
               } catch (Exception e) {
                   return;
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(result);

                byte[] buffer = new byte[BUFFER_SIZE];

                int bulk;
                InputStream inputStream = event.getFile().getInputstream();
                while (true) {
                    bulk = inputStream.read(buffer);
                    if (bulk < 0) {
                        break;
                    }
                    fileOutputStream.write(buffer, 0, bulk);
                    fileOutputStream.flush();
                }

                fileOutputStream.close();
                inputStream.close();

                FacesMessage msg = 
                            new FacesMessage("Propiedades", "Nombre: " +
                            event.getFile().getFileName() + " Tamaño: " + 
                            event.getFile().getSize() / 1024 + 
                            " Kb Tipo: " + 
                            event.getFile().getContentType() + 
                                    " Subido correctamente.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                this.docImg = event.getFile().getFileName();

            } catch (IOException e) {

                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                               "Error al subir", "");
                FacesContext.getCurrentInstance().addMessage(null, error);
            }     
    }

    public List<PgePersonas> getPersonas() {
        return personas;
    }

    public void setPersonas(List<PgePersonas> personas) {
        this.personas = personas;
    }

    public List<PgeProfesiones> getProfesiones() {
        return profesiones;
    }

    public void setProfesiones(List<PgeProfesiones> profesiones) {
        this.profesiones = profesiones;
    }

    public List<PgePaises> getPaises() {
        return paises;
    }

    public void setPaises(List<PgePaises> paises) {
        this.paises = paises;
    }

    public PgePersonas getPersonaSelected() {
        return personaSelected;
    }

    public void setPersonaSelected(PgePersonas personaSelected) {
        this.personaSelected = personaSelected;
    }

    public PgePaises getPais() {
        return pais;
    }

    public void setPais(PgePaises pais) {
        this.pais = pais;
    }

    public PgeProfesiones getProfesion() {
        return profesion;
    }

    public void setProfesion(PgeProfesiones profesion) {
        this.profesion = profesion;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getApe() {
        return ape;
    }

    public void setApe(String ape) {
        this.ape = ape;
    }

    public String getNroDoc() {
        return nroDoc;
    }

    public void setNroDoc(String nroDoc) {
        this.nroDoc = nroDoc;
    }

    public Date getFecNac() {
        return fecNac;
    }

    public void setFecNac(Date fecNac) {
        this.fecNac = fecNac;
    }

    public String getLugNac() {
        return lugNac;
    }

    public void setLugNac(String lugNac) {
        this.lugNac = lugNac;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocImg() {
        return docImg;
    }

    public void setDocImg(String docImg) {
        this.docImg = docImg;
    }
    
    
}
