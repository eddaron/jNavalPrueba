
package org.ecp.naval;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.ecp.resources.Imagenes;

/**
 *
 * edson
 */
public class JBatallaNaval extends javax.swing.JFrame {

    Image portada;
    Image tablero;
    int nEstado=0;

    int miTablero[][]=new int[8][8];
    boolean bMiTablero[][]=new boolean[8][8];
    int suTablero[][]=new int[8][8];
    boolean bSuTablero[][]=new boolean[8][8];

    int pFila=0;
    int pCol=0;
    int pTam=5;
    int pHor=0;
   

 /** constructor
     */
    public JBatallaNaval() {
        Imagenes i =new Imagenes();
        portada=i.cargar("portada.jpg");
        tablero=i.cargar("tablero.jpg");
        initComponents();
        setBounds(0,0,800,600);
        
        
        addMouseListener(
            new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                    if (e.getModifiers() == MouseEvent.BUTTON3_MASK && nEstado==1){
                        pHor=1-pHor;
                        rectificarBarcoPoner();
                        repaint();
                        return;
                    }
                    if (nEstado==0){
                        nEstado=1;
                        iniciarPartida();
                        repaint();
                    }else if (nEstado==1){
                        if (puedePonerBarco()){
                            int pDF=0;
                            int pDC=0;
                            if (pHor==1){
                                pDF=1;
                            }else{
                                pDC=1;
                            }
                            for (int m=pFila;m<=pFila+(pTam-1)*pDF;m++){
                                for (int n=pCol;n<=pCol+(pTam-1)*pDC;n++){
                                    miTablero[m][n]=pTam;
                                }
                            }
                            pTam--;
                            if (pTam==0){
                                nEstado=2;
                                repaint();
                            }
                        }
                    }else if (nEstado==2){
                        int f=(e.getY()-200)/30;
                        int c=(e.getX()-450)/30;
                        if (f!=pFila || c!=pCol){
                            pFila=f;
                            pCol=c;
                            if (celdaEstaEnTablero(f, c)){
                                if (bSuTablero[f][c]==false){
                                    bSuTablero[f][c]=true;
                                    repaint();
                                    if (victoria(suTablero, bSuTablero)){
                                        JOptionPane.showMessageDialog(null, "Has ganado");
                                        nEstado=0;
                                    }
                                    dispararEl();
                                    repaint();
                                    if (victoria(miTablero, bMiTablero)){
                                        JOptionPane.showMessageDialog(null, "Has perdido");
                                        nEstado=0;
                                    }
                                    repaint();
                                }
                            }
                        }
                    }
                }
            }
        );
        addMouseMotionListener(
            new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int x=e.getX();
                    int y=e.getY();
                    if (nEstado==1 && x>=100 && y>=200 && x<100+30*8 && y<200+30*8){
                        int f=(y-200)/30;
                        int c=(x-100)/30;
                        if (f!=pFila || c!=pCol){
                            pFila=f;
                            pCol=c;
                            rectificarBarcoPoner();
                            repaint();
                        }
                    }
                }
            }
        );
    }

    
    
    public boolean celdaEstaEnTablero(int f, int c){
        if (f<0) return false;
        if (c<0) return false;
        if (f>=8) return false;
        if (c>=8) return false;
        return true;
    }

    public boolean puedePonerBarco(int tab[][], int tam, int f, int c, int hor){
        int df=0,dc=0;
        if (hor==1) df=1;
        else dc=1;
        for (int c2=c;c2<=c+tam*dc;c2++){
            for (int f2=f;f2<=f+tam*df;f2++){
                if (!celdaEstaEnTablero(f2, c2)){
                    return false;
                }
            }
        }
        for (int f2=f-1;f2<=f+1+df*tam;f2++){
            for (int c2=c-1;c2<=c+1+dc*tam;c2++){
                if (celdaEstaEnTablero(f2,c2)){
                    if (tab[f2][c2]!=0){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void ponerBarco(int tab[][], int tam){

        int f,c,hor;
        do{
            f=(int)(Math.random()*8);
            c=(int)(Math.random()*8);
            hor=(int)(Math.random()*2);
        }while(!puedePonerBarco(tab, tam, f, c, hor));
        int df=0,dc=0;
        if (hor==1) df=1;
        else dc=1;
        for (int f2=f;f2<=f+(tam-1)*df;f2++){
            for (int c2=c;c2<=c+(tam-1)*dc;c2++){
                tab[f2][c2]=tam;
            }
        }
    }

    public void iniciarPartida(){
        for (int n=0;n<8;n++){
            for (int m=0;m<8;m++){
                miTablero[n][m]=0;
                suTablero[n][m]=0;
                bMiTablero[n][m]=false;
                bSuTablero[n][m]=false;
                
            }
        }
        
        //creando barcos
        for (int tam=5;tam>=1;tam--){
            ponerBarco(suTablero, tam);
            
        }
        pTam=5;
    }


    public void rectificarBarcoPoner(){
        int pDF=0;
        int pDC=0;
        if (pHor==1){
            pDF=1;
        }else{
            pDC=1;
        }
        if (pFila+pTam*pDF>=8){
            pFila=7-pTam*pDF;
        }
        if (pCol+pTam*pDC>=8){
            pCol=7-pTam*pDC;
        }
    }

    public boolean puedePonerBarco(){
        return puedePonerBarco(miTablero, pTam, pFila, pCol, pHor);
    }

    public boolean victoria(int tab[][], boolean bTab[][]){
        for (int n=0;n<8;n++){
            for (int m=0;m<8;m++){
                if (bTab[n][m]==false && tab[n][m]!=0){
                    return false;
                }
            }
        }
        return true;
    }

    public void dispararEl(){
        int f,c;
        do{
            f=(int)(Math.random()*8);
            c=(int)(Math.random()*8);
        }while(bMiTablero[f][c]==true);
        bMiTablero[f][c]=true;
    }
    
   

    public boolean noHayInvisible(int tab[][], int valor, boolean bVisible[][]){
        for (int n=0;n<8;n++){
            for (int m=0;m<8;m++){
                if (bVisible[n][m]==false){
                    if (tab[n][m]==valor){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void pintarTablero(Graphics g, int tab[][], int x, int y, boolean bVisible[][]){
        
        for (int n=0;n<8;n++){
            for (int m=0;m<8;m++){
                if (tab[n][m]>0 && bVisible[n][m]){
                    g.setColor(Color.green);
                    if (noHayInvisible(tab, tab[n][m], bVisible)){
                        g.setColor(Color.red);
                    }
                    g.fillRect(x+m*30, y+n*30, 30, 30);
                }
                if (tab[n][m]==0 && bVisible[n][m]){
                    g.setColor(Color.cyan);
                    g.fillRect(x+m*30, y+n*30, 30, 30);
                }
                if (tab[n][m]>0 && tab==miTablero && !bVisible[n][m]){
                    g.setColor(Color.gray);
                    g.fillRect(x+m*30, y+n*30, 30, 30);
                }
                g.setColor(Color.black);
                g.drawRect(x+m*30, y+n*30, 30, 30);
                if (nEstado==1 && tab==miTablero){
                    int pDF=0;
                    int pDC=0;
                    if (pHor==1){
                        pDF=1;
                    }else{
                        pDC=1;
                    }
                    if (n>=pFila && m>=pCol && n<=pFila+(pTam-1)*pDF && m<=pCol+(pTam-1)*pDC){
                        g.setColor(Color.green);
                        g.fillRect(x+m*30, y+n*30, 30, 30);
                    }
                }
            }
        }
    }

    boolean bPrimeraVez=true;
    public void paint(Graphics g){
        
        if (bPrimeraVez){
            g.drawImage(portada, 0,0,1,1,this);
            g.drawImage(tablero, 0,0,1,1,this);
            bPrimeraVez=false;
        }
        if (nEstado==0){
            g.drawImage(portada, 0, 0, this);
        }else {
            g.drawImage(tablero, 0, 0, this);
            pintarTablero(g, miTablero, 100, 200, bMiTablero);
            pintarTablero(g, suTablero, 450, 200, bSuTablero);
           
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        jLabel1 = new javax.swing.JLabel();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("battleship");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(343, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        jLabel1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JBatallaNaval().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    // End of variables declaration//GEN-END:variables

}
