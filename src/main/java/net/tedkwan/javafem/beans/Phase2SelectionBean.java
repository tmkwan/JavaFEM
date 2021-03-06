package net.tedkwan.javafem.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import net.tedkwan.javafem.entity.Ode2D;
import net.tedkwan.javafemjni.TwoDimPhase;
import org.jblas.DoubleMatrix;

/**
 * Managed Bean for 2D phase planes.
 *
 *
 * This managed bean is the bean which creates the list of 2D ODEs and then
 * applies the RK45 method in C++.
 *
 * @author Ted Kwan
 */
@ManagedBean(name = "phase2SelectionBean")
@ViewScoped
public class Phase2SelectionBean implements Serializable {

    Ode2D odefun;
    List<Ode2D> odelist;
    @PersistenceContext(unitName = "net.tedkwan_JavaFEM_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    /**
     * Default Constructor, makes the first plot available.
     */
    public Phase2SelectionBean() {
        makeDuffingFirst();
    }

    /**
     * Initialization routine.
     *
     * This sets up the ODE list and then allows for selection.
     */
    @PostConstruct
    public void init() {
        Query query = em.createNamedQuery("Ode2D.findAll");
        odelist = (List<Ode2D>) query.getResultList();
        odefun = odelist.get(0);
    }

    private String xarpp = "";
    private String yarpp = "";

    /**
     * Persist to save to mysql.
     *
     * Not implemented currently.
     *
     * @param object object to persist.
     */
    private void persist(Object object) {
        em.persist(object);
    }

    /**
     * Creation of 2D phase plane.
     *
     * This method creates the strings needed to plot the phase plane in 2D.
     * This is public so that it can be called by the refresh button.
     *
     */
    public void makeDuffing() {
        // Open the JNI connection.
        TwoDimPhase phase = new TwoDimPhase();
        String odename = odefun.getName();
        double[] initc;
        // Choose correct initial conditions and parameters.
        if (odename.contains("olterr")) {
            double[] initcv = {0.05, odefun.getA().doubleValue(),
                odefun.getB().doubleValue(),
                odefun.getC().doubleValue(), odefun.getD().doubleValue(), 30.0, 1.0 + Math.random(),
                1.0 + Math.random()};
            initc = initcv;

        } else {
            double[] initc2 = {0.05, odefun.getA().doubleValue(),
                odefun.getB().doubleValue(),
                odefun.getC().doubleValue(), odefun.getD().doubleValue(), 30.0, Math.random(),
                Math.random()};
            initc = initc2;
        }
        // Run the JNI method to solve ODE.
        double[] res = phase.rk4(initc, odename);
        // Clean up output and store.
        int r = res.length / 3;
        DoubleMatrix outmtx = new DoubleMatrix(r, 3, res);
        DoubleMatrix xpp = outmtx.getColumn(0);
        DoubleMatrix ypp = outmtx.getColumn(1);
        // Convert vectors to JSON arrays.
        xarpp = convertMatrix((xpp));
        yarpp = convertMatrix((ypp));
        System.out.println("Done.");
    }

    /**
     * Construct initial phase plane.
     *
     * This method constructs the phase plane that is seen when the page loads.
     * It makes a duffing oscillator.
     */
    private void makeDuffingFirst() {
        // Initialize JNI connector.
        TwoDimPhase phase = new TwoDimPhase();
        double[] initc = {0.05, 0.8,
            1.0, 0.3, -1.0, 20.0, 0.7, 0.6};
        // Call JNI method.
        double[] res = phase.rk4(initc, "Duffing");
        // Clean up output.
        int r = res.length / 3;
        DoubleMatrix outmtx = new DoubleMatrix(r, 3, res);
        DoubleMatrix xpp = outmtx.getColumn(0);
        DoubleMatrix ypp = outmtx.getColumn(1);
        // Convert vectors to JSON array.
        xarpp = convertMatrix((xpp));
        yarpp = convertMatrix((ypp));
    }

    /**
     * ConvertMatrix function.
     *
     * This function creates a string JSON representation of a DoubleMatrix. It
     * ensures that the output string is able to be read by plotly in
     * javascript.
     *
     *
     * @param conv String to convert.
     * @return String representation of DoubleMatrix in JSON array.
     */
    private String convertMatrix(DoubleMatrix conv) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < conv.length - 1; i++) {

            sb.append(String.format("%.6f", conv.get(i)));
            sb.append(",");
        }
        sb.append(String.format("%.6f", conv.get(conv.length - 1)));
        sb.append("]");
        return sb.toString();
    }

    /**
     * ConvertMatrix function.
     *
     * This function creates a string JSON representation of a DoubleMatrix. It
     * ensures that the output string is able to be read by plotly in
     * javascript.
     *
     *
     * @param conv String to convert.
     * @return String representation of DoubleMatrix in JSON array.
     */
    private String convertMatrices(DoubleMatrix conv) {
        StringBuilder sb = new StringBuilder();
        String nextline = System.lineSeparator();

        for (int i = 0; i < conv.rows; i++) {
            sb.append("[");
            for (int j = 0; j < conv.columns - 1; j++) {
                sb.append(String.format("%.6f", conv.get(i, j)));
                if (j < 3) {
                    sb.append(",");
                } else {
                    sb.append(",");
                    sb.append(String.format("%.6f", conv.get(i, j)));
                }
            }
            sb.append("],");
            if (i < conv.rows - 1) {
                sb.append(nextline);
            }
        }
        return sb.toString();
    }

    public String getXarpp() {
        return xarpp;
    }

    public String getYarpp() {
        return yarpp;
    }

    public Ode2D getOdefun() {
        return odefun;
    }

    public void setOdefun(Ode2D odefun) {
        this.odefun = odefun;
    }

    public List<Ode2D> getOdelist() {
        return odelist;
    }
}
