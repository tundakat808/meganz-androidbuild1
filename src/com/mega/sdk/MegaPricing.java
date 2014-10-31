/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.mega.sdk;

public class MegaPricing {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected MegaPricing(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(MegaPricing obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        megaJNI.delete_MegaPricing(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public int getNumProducts() {
    return megaJNI.MegaPricing_getNumProducts(swigCPtr, this);
  }

  public long getHandle(int productIndex) {
    return megaJNI.MegaPricing_getHandle(swigCPtr, this, productIndex);
  }

  public int getProLevel(int productIndex) {
    return megaJNI.MegaPricing_getProLevel(swigCPtr, this, productIndex);
  }

  public int getGBStorage(int productIndex) {
    return megaJNI.MegaPricing_getGBStorage(swigCPtr, this, productIndex);
  }

  public int getGBTransfer(int productIndex) {
    return megaJNI.MegaPricing_getGBTransfer(swigCPtr, this, productIndex);
  }

  public int getMonths(int productIndex) {
    return megaJNI.MegaPricing_getMonths(swigCPtr, this, productIndex);
  }

  public int getAmount(int productIndex) {
    return megaJNI.MegaPricing_getAmount(swigCPtr, this, productIndex);
  }

  public String getCurrency(int productIndex) {
    return megaJNI.MegaPricing_getCurrency(swigCPtr, this, productIndex);
  }

  public MegaPricing copy() {
    long cPtr = megaJNI.MegaPricing_copy(swigCPtr, this);
    return (cPtr == 0) ? null : new MegaPricing(cPtr, false);
  }

}
