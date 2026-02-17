package CACIE.ui.sphereGUI;
import java.awt.event.KeyEvent;

import javax.media.j3d.Behavior;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Vector3f;

import CACIE.RandomManager;

//this class implements a more complex behavior.
//the behavior modifies the coordinates within a
//GeometryArray based on simulated forces applied to
//the model. Forces are modeled as springs from the origin
//to every node. Every node has a mass, and hence an
//acceleration. Pressing a key will increase the acceleration
//at each node, upsetting the force equilibrium at each vertex.
//The model will then start to oscillate in size as the springs
//have no "damping" effect.
//
//note: this is a very computationally expensive behavior!

class StretchBehavior extends Behavior {
  // the wake up condition for the behavior
  protected WakeupCondition m_WakeupCondition = null;

  // the GeometryArray for the Shape3D that we are modifying
  protected GeometryArray m_GeometryArray = null;

  // cache some information on the model to save reallocation
  protected float[] m_CoordinateArray = null;

  protected float[] m_LengthArray = null;

  protected float[] m_MassArray = null;

  protected float[] m_AccelerationArray = null;

  protected Vector3f m_Vector = null;

  // spring stiffness: Fspring = k.Le
  protected float m_kSpringConstant = 1.3f;

  protected float m_kAccelerationLossFactor = 0.985f;

  public StretchBehavior(GeometryArray geomArray) {
    // save the GeometryArray that we are modifying
    m_GeometryArray = geomArray;

    // set the capability bits that the behavior requires
    m_GeometryArray.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    m_GeometryArray.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
    m_GeometryArray.setCapability(GeometryArray.ALLOW_COUNT_READ);

    // allocate an array for the model coordinates
    m_CoordinateArray = new float[3 * m_GeometryArray.getVertexCount()];

    // retrieve the models original coordinates - this defines
    // the relaxed length of the springs
    m_GeometryArray.getCoordinates(0, m_CoordinateArray);

    // allocate an array to store the relaxed length
    // of the springs from the origin to every vertex
    m_LengthArray = new float[m_GeometryArray.getVertexCount()];

    // allocate an array to store the mass of every vertex
    m_MassArray = new float[m_GeometryArray.getVertexCount()];

    // allocate an array to store the acceleration of every vertex
    m_AccelerationArray = new float[m_GeometryArray.getVertexCount()];

    // allocate a temporary vector
    m_Vector = new Vector3f();

    float x = 0;
    float y = 0;
    float z = 0;

    for (int n = 0; n < m_CoordinateArray.length; n += 3) {
      // calculate and store the relaxed spring length
      x = m_CoordinateArray[n];
      y = m_CoordinateArray[n + 1];
      z = m_CoordinateArray[n + 2];

      m_LengthArray[n / 3] = (x * x) + (y * y) + (z * z);

      // assign the mass for the vertex
      m_MassArray[n / 3] = (float) (50 + (5 * RandomManager.getRandom()));
    }

    // create the WakeupCriterion for the behavior
    WakeupCriterion criterionArray[] = new WakeupCriterion[2];
    criterionArray[0] = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    criterionArray[1] = new WakeupOnElapsedFrames(1);

    // save the WakeupCriterion for the behavior
    m_WakeupCondition = new WakeupOr(criterionArray);
  }

  public void initialize() {
    // apply the initial WakeupCriterion
    wakeupOn(m_WakeupCondition);
  }

  public void processStimulus(java.util.Enumeration criteria) {
    // update the positions of the vertices - regardless of criteria
    float elongation = 0;
    float force_spring = 0;
    float force_mass = 0;
    float force_sum = 0;
    float timeFactor = 0.1f;
    float accel_sum = 0;

    // loop over every vertex and calculate its new position
    // based on the sum of forces due to acceleration and the
    // spring.
    for (int n = 0; n < m_CoordinateArray.length; n += 3) {
      m_Vector.x = m_CoordinateArray[n];
      m_Vector.y = m_CoordinateArray[n + 1];
      m_Vector.z = m_CoordinateArray[n + 2];

      // use squared lengths, as sqrt is costly
      elongation = m_LengthArray[n / 3] - m_Vector.lengthSquared();

      // Fspring = k.Le
      force_spring = m_kSpringConstant * elongation;
      force_mass = m_AccelerationArray[n / 3] * m_MassArray[n / 3];

      // calculate resultant force
      force_sum = force_mass + force_spring;

      // a = F/m
      m_AccelerationArray[n / 3] = (force_sum / m_MassArray[n / 3])
          * m_kAccelerationLossFactor;
      accel_sum += m_AccelerationArray[n / 3];

      m_Vector.normalize();

      // apply a portion of the acceleration as change in coordinate.
      // based on the normalized vector from the origin to the vertex
      m_CoordinateArray[n] += m_Vector.x * timeFactor
          * m_AccelerationArray[n / 3];
      m_CoordinateArray[n + 1] += m_Vector.y * timeFactor
          * m_AccelerationArray[n / 3];
      m_CoordinateArray[n + 2] += m_Vector.z * timeFactor
          * m_AccelerationArray[n / 3];
    }

    // assign the new coordinates
    m_GeometryArray.setCoordinates(0, m_CoordinateArray);

    while (criteria.hasMoreElements()) {
      WakeupCriterion wakeUp = (WakeupCriterion) criteria.nextElement();

      // if a key was pressed increase the acceleration at the
      // vertices a little to upset the equiblibrium
      if (wakeUp instanceof WakeupOnAWTEvent) {
        for (int n = 0; n < m_AccelerationArray.length; n++)
          m_AccelerationArray[n] += 0.3f;
      } else {
        // otherwise, print the average acceleration
        System.out.print("Average acceleration:\t" + accel_sum
            / m_AccelerationArray.length + "\n");
      }
    }

    // assign the next WakeUpCondition, so we are notified again
    wakeupOn(m_WakeupCondition);
  }
}
