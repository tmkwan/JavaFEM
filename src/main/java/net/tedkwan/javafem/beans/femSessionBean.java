/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.tedkwan.javafem.beans;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import net.tedkwan.javafem.fem.FEM;
import net.tedkwan.javafem.fem.mtxFun;
import net.tedkwan.javafem.fem.mtxFunBd;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Devils
 */

@ManagedBean(name = "femSessionBean")
@SessionScoped
public class femSessionBean implements Serializable {

    private double x1=0.0;
    private double x2=1.0;
    private double y1=0.0;
    private double y2=1.0;
    private DoubleMatrix u;
    private DoubleMatrix x;
    private DoubleMatrix y;
    private String xar="";
    private String yar="";
    private String uar="";
    private double h=0.125;
    /**
     * Creates a new instance of femSessionBean
     */
    public femSessionBean() {
        calculateFEM();
    }
    
    public void calculateFEM(){
        mtxFun f=new mtxFun();
        mtxFunBd g=new mtxFunBd();
        long startt=System.nanoTime();
        FEM fem=new FEM(x1,x2,y1,y2,h,f,g);
        long endt=System.nanoTime();
        long totalt=endt-startt;
        double fullt= (double) totalt / 1000000000.0;
        System.out.println("Elapsed time is " + fullt +" seconds");
        u=fem.getU();
        DoubleMatrix nodes=fem.getNodes();
        x=nodes.getColumn(0);
        y=nodes.getColumn(1);
        xar=convertMatrix(MatrixFunctions.abs(x));
        yar=convertMatrix(MatrixFunctions.abs(y));
        uar=convertMatrix(u);
    }
    
    private String convertMatrix(DoubleMatrix conv){
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        for(int i=0;i<conv.length-1;i++){
            sb.append(String.format("%.6f",conv.get(i)));
            sb.append(",");
        }
        sb.append(String.format("%.6f",conv.get(conv.length-1)));
        sb.append("]");
        //System.out.println(sb.toString());
        return sb.toString();
    }
    
    public void showPlot() {
        Map<String,Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("modal", true);
        calculateFEM();
        RequestContext context=RequestContext.getCurrentInstance();
        context.openDialog("showPlot", options, null);
    }
    public void onClose(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Plot Closed", "You have closed the plot");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public DoubleMatrix getU() {
        return u;
    }

    public DoubleMatrix getX() {
        return x;
    }

    public DoubleMatrix getY() {
        return y;
    }

    public String getXar() {
        return xar;
    }

    public String getYar() {
        return yar;
    }

    public String getUar() {
        return uar;
    }
    
}
