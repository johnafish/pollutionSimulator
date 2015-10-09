package pollutionsimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JFrame;

/**
 * @author John Fish <john@johnafish.ca>
 */

public class PollutionSimulator extends JFrame {
    static int width = 768;
    static int height = 480;
    double mutationFactor = 0.1;
    double deviation = 0.9;
    double[][] peopleValues = new double[width][height];
    double[][] atmosphericValues = new double[width][height];
    
    public void setInitial(){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                peopleValues[i][j] = ThreadLocalRandom.current().nextDouble(1-deviation, 1+deviation);
                atmosphericValues[i][j] = 5;
            }
        }
    }
    public double applyKernel(double[][] kernel, int x, int y, int divisor, boolean people){
        double accumulator = 0;
        for (int i = 0; i < kernel[0].length; i++) {
            for (int j = 0; j < kernel[0].length; j++) {
                int xPos = (x+i-1)%width;
                int yPos = (y+j-1)%height;
                if (xPos<0){
                    xPos = width+xPos;
                }
                if (yPos<0){
                    yPos = height+yPos;
                }
                if(people){
                    accumulator += peopleValues[xPos][yPos]*kernel[i][j];
                } else {
                    accumulator += atmosphericValues[xPos][yPos]*kernel[i][j];
                }
            }
        }

        return (double) accumulator/divisor;
    }
    public void evolvePeople(){
        double[][] newPeopleValues = new double[width][height];
        double[][] kernelToApply = {{1, 1, 1},
                                    {1, 1, 1},
                                    {1, 1, 1}};
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newPeopleValues[i][j] = applyKernel(kernelToApply, i, j, 9, true);
            }
        }
        peopleValues = newPeopleValues;
    }
    public void evolvePollution(){
        double[][] newAtmosphericValues = new double[width][height];
        double[][] kernelToApply = {{1, 2, 1},
                                    {2, 4, 2}, 
                                    {1, 2, 1}};

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newAtmosphericValues[i][j] = applyKernel(kernelToApply, i, j, 16, false);
            }
        }
        atmosphericValues = newAtmosphericValues;
        
    }
    public void addPollution(){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                atmosphericValues[i][j]*=peopleValues[i][j];
                if (atmosphericValues[i][j]>255){
                    atmosphericValues[i][j]=255;
                }
            }
        }
    }
    public void mutatePeople(){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                peopleValues[i][j] += ThreadLocalRandom.current().nextDouble(-mutationFactor, mutationFactor);
                if (peopleValues[i][j]<(1-deviation)){
                    peopleValues[i][j] = 1-deviation;
                } else if (peopleValues[i][j]>(1+deviation)){
                    peopleValues[i][j] = 1+deviation;
                }
            }
        }
    }
    public void nextGeneration(){
        mutatePeople();
        addPollution();
        evolvePeople();
        evolvePollution();
    }
    
    public BufferedImage getImage(){
        BufferedImage peopleImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage pollutionImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage finalImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int peopleTrans = 55;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int atmosphericValue = (int)Math.round(atmosphericValues[i][j]);
                if (atmosphericValue>255){
                    System.out.println(atmosphericValue);
                }
                Color atmosColor = new Color(0,0,0,atmosphericValue);

                int peopleColorValue = (int) Math.round((255*peopleValues[i][j]-255*(1-deviation))/(2*deviation));
                Color peopleColor = new Color(peopleColorValue, 255-peopleColorValue, 0, 255);
                
                peopleImg.setRGB(i, j, peopleColor.getRGB());
                pollutionImg.setRGB(i, j, atmosColor.getRGB());
                
            }
        }
        Graphics f = finalImg.getGraphics();
        f.setColor(Color.white);
        f.fillRect(0,0,width,height);
        f.drawImage(peopleImg,0,0,null);
        f.drawImage(pollutionImg,0,0,null);
        return finalImg;
    }
    
    @Override
    public void paint(Graphics g){
        BufferedImage img = getImage();
        g.drawImage(img, 0, 0, rootPane);
    }
    
    public static void main(String[] args) throws InterruptedException {
        PollutionSimulator p = new PollutionSimulator();
        p.setSize(width, height);
        p.setDefaultCloseOperation( EXIT_ON_CLOSE );
        p.setInitial();
        p.setVisible(true);  //Calls paint
        while(true){
//            Thread.sleep(1000/60);
            p.nextGeneration();
            p.repaint();
        }
    }

}
