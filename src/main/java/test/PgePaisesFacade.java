/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.fpuna.py.travelware.model.PgePaises;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author eencina
 */
@Stateless
public class PgePaisesFacade extends AbstractFacade<PgePaises> {
    @PersistenceContext(unitName = "com.fpuna_py.travelware_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PgePaisesFacade() {
        super(PgePaises.class);
    }
    
}
