package CACIE.ui.GLSphereGUI;

public class MatrixRotation3Df {

	//How to use this class
	// matrixRotation3Df = new MatrixRotation3Df(float xAngle, float yAngle, float zAngle);
	// float rotatedPoint[4] // [0]:x, [1]:y, [2]:z
	//    = culcOut(float pointX, float pointY, float pointZ);
	
	float m[];
	float axisMatrix[][];
	
	private MatrixRotation3Df(){
		m = new float[16];
		axisMatrix = new float[3][16];
		initMatrix(m);
		for(int i=0; i<3; i++)
			initMatrix(axisMatrix[i]);		
	}
	
	public MatrixRotation3Df(float xAngle, float yAngle, float zAngle){
		this();
		setAngle(0, xAngle);setAngle(1, yAngle);setAngle(2, zAngle);
		m = multiplyMatrix(axisMatrix[0], axisMatrix[1]);
		m = multiplyMatrix(m, axisMatrix[2]);
	}
	
	public float[] calcOut(float x, float y, float z){
		float points[] = new float[4];
		points[0] = x; points[1] = y; points[2] = z; points[3] = 1.f;
		points = multiplyVectorAndMatrix(points, m);
		return points;
	}
	
	private void initMatrix(float matrix[]){
		for(int i=0; i<16; i++){
			matrix[i] = 0;
		}
		matrix[0+4*0] = 1.f;
		matrix[1+4*1] = 1.f;
		matrix[2+4*2] = 1.f;
		matrix[3+4*3] = 1.f;
	}
	
	public void setAngle(int axis, float angle){
		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);
		
		if(axis == 0){
			//generate X axis rotate and put axisMatrix[0][i] 
			axisMatrix[0][0] = 1.f; axisMatrix[0][1] = 0.f; 
			axisMatrix[0][2] = 0.f; axisMatrix[0][3] = 0.f;
			axisMatrix[0][4] = 0.f; axisMatrix[0][5] = cos;
			axisMatrix[0][6] = sin; axisMatrix[0][7] = 0.f;
			axisMatrix[0][8] = 0.f; axisMatrix[0][9] = -1.f*sin;
			axisMatrix[0][10] = cos; axisMatrix[0][11] = 0.f;
			axisMatrix[0][12] = 0.f; axisMatrix[0][13] = 0.f;
			axisMatrix[0][14] = 0.f; axisMatrix[0][15] = 1.f;
		}
		else if(axis == 1){
			//generate Y axis rotate and put axisMatrix[1][i] 
			axisMatrix[1][0] = cos; axisMatrix[1][1] = 0.f; 
			axisMatrix[1][2] = -1*sin; axisMatrix[1][3] = 0.f;
			axisMatrix[1][4] = 0.f; axisMatrix[1][5] = 1.f;
			axisMatrix[1][6] = 0.f; axisMatrix[1][7] = 0.f;
			axisMatrix[1][8] = sin; axisMatrix[1][9] = 0.f;
			axisMatrix[1][10] = cos; axisMatrix[1][11] = 0.f;
			axisMatrix[1][12] = 0.f; axisMatrix[1][13] = 0.f;
			axisMatrix[1][14] = 0.f; axisMatrix[1][15] = 1.f;
		}
		else if(axis == 2){
			//generate Z axis rotate and put axisMatrix[2][i] 
			axisMatrix[2][0] = cos; axisMatrix[2][1] = sin; 
			axisMatrix[2][2] = 0.f; axisMatrix[2][3] = 0.f;
			axisMatrix[2][4] = -1*sin; axisMatrix[2][5] = cos;
			axisMatrix[2][6] = 0.f; axisMatrix[2][7] = 0.f;
			axisMatrix[2][8] = 0.f; axisMatrix[2][9] = 0.f;
			axisMatrix[2][10] = 1.f; axisMatrix[2][11] = 0.f;
			axisMatrix[2][12] = 0.f; axisMatrix[2][13] = 0.f;
			axisMatrix[2][14] = 0.f; axisMatrix[2][15] = 1.f;
		}
		else{
			System.err.println
			("MatrixRotation3Df: setAngle(): invailed argument given. axis: " + axis);
			System.exit(1);
		}
	}
	
	private float[] multiplyMatrix(float m1[], float m2[]){
		float returnMatrix[] = new float[16];
		for(int i=0; i<16; i++){
			returnMatrix[i] = 0.f;
			for(int j=0; j<4; j++)
				returnMatrix[i] += m1[i%4*4 +j] * m2[j*4 + i>>2];
		}
		return returnMatrix;
	}
	
	private float[] multiplyVectorAndMatrix(float v[], float m[]){
		float returnVector[] = new float[4];
		for(int i=0; i<4; i++)
			returnVector[i] = 0.f;
		for(int i=0; i<4; i++){
			for(int j=0; j<4; j++)
				returnVector[j] += v[i]*m[4*i+j];
		}
		return returnVector;
	}
}
