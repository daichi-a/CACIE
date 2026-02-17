package CACIE.ui.sphereGUI;

import javax.vecmath.Color3f;


public class SphereIndividual {
	
	int m_index;
	int m_fitness;
	int position;
	
	double m_x;
	double m_y;
	double m_distance;
	double m_slope;
	
	double m_r;
	double m_theta;
    Color3f m_Col = new Color3f();
    double m_size;
    SphereIndividual m_sphere;
    
    boolean blink_flag;
    
    public void setSphere(SphereIndividual sp){
    	this.m_sphere = sp;
    	this.blink_flag = m_sphere.getBlinkFlag();
    	this.m_Col = m_sphere.getColor();
    	this.m_distance = m_sphere.getDistance();
    	this.m_fitness = m_sphere.getFitness();
    	this.m_index = m_sphere.getIndex();
    	this.m_r = m_sphere.getR();
    	this.m_size = m_sphere.getSize();
    	this.m_slope = m_sphere.getSlope();
    	this.m_theta = m_sphere.getThetaRad();
    	this.m_x = m_sphere.getX();
    	this.m_y = m_sphere.getY();
    	
    }
    
    public SphereIndividual getSphere(){
    	return this.m_sphere;
    }
    
	public void setIndex(int index){
		this.m_index = index;
	}
	
	public int getIndex(){
		return this.m_index;
	}
	
	public void setColor(Color3f col){
		this.m_Col = col;
	}
	
	public Color3f getColor(){
		return this.m_Col;
	}
	
	public void setBlinkFlag(boolean flag){
		this.blink_flag = flag;
	}
	
	public boolean getBlinkFlag(){
		return this.blink_flag;
	}
	public void setFitness(int fitness){
		this.m_fitness = fitness;
	}
	
	public int getFitness(){
		return this.m_fitness;
	}
	
	public void setPosition(int _position){
		this.position = _position;
	}
	
	public int getPosition(){
		return this.position;
	}
	
	public void setX(double x){
		this.m_x = x;
	}
	
	public double getX(){
		return this.m_x;
	}

	public void setY(double y ){
		this.m_y = y;
	}
	
	public double getY(){
		return this.m_y;
	}
	
	public void setR(double r){
		this.m_r = r;
	}
	
	public double getR(){
		return this.m_r;
	}
	
	public void setSize(double si){
		this.m_size = si;
	}
	
	public double getSize(){
		return this.m_size;
	}
	
	public void setTheta(double theta){
		this.m_theta = theta;
	}
	
	public double getThetaRad(){
		return this.m_theta;
	}
	
	public double getThetaDeg(){
		return Math.toDegrees(this.m_theta);
	}
	
	public void setDistance(double distance){
		this.m_distance = distance;
	}
	
	public double getDistance(){
		return this.m_distance;
	}
	
	public void setSlope(double slope){
		this.m_slope = slope;
	}
	
	public double getSlope(){
		return this.m_slope;
		
	}
	
	public double getTransformedX(){
		return this.m_r * Math.cos(this.m_theta);
	}
	
	public double getTransformedY(){
		return this.m_r * Math.sin(this.m_theta);
	}
	
	public double getTransformedR(){
		return Math.sqrt(this.m_x * this.m_x + this.m_y * this.m_y);
	}
	
	public double getTransformedTheta(){

		return Math.atan2(this.m_y, this.m_x);	
	}
	
	public void PolarUpdate(){
		this.m_r = Math.sqrt(this.m_x * this.m_x + this.m_y * this.m_y);
		this.m_theta = Math.atan2(this.m_y, this.m_x);
	}
	
	public void CartesianUpdate(){
		this.m_x = this.m_r * Math.cos(this.m_theta);
		this.m_y = this.m_r * Math.sin(this.m_theta);
	}
	
	public int getTransformedFitness(){
//		double f1 = (double)(this.m_r / 100.0);
//		double f2;
//		if(f1 >= 1.00){
//			f2 = 1.00 - 1.00;
//		}else if(f1 <= 0.0){
//			f2 = 1.00 - 0.00;
//		}else{
//			f2 = 1.00 - f1;
//		}
//
//		this.m_fitness = (int)(100 * f2);
//
//		return this.m_fitness;
		
		double f = this.m_r / (double) (4.5) * 100.0;
		int fitness = (int) f;
		if (fitness >= 100) {
			fitness = 100;
		}
		if (fitness <= 0) {
			fitness = 0;
		}

		fitness = 100 - fitness;
		return fitness;
	}
	
	public double getTransformedSlope(){
		return Math.tan(this.m_theta);
	}
	
}
