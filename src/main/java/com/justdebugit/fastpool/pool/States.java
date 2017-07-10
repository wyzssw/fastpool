package com.justdebugit.fastpool.pool;

/**
 */
public enum States {
  STATE_NOT_IN_USE(0),STATE_IN_USE(1),STATE_RESERVED(2),STATE_REMOVED(-1);

  public final int stateVal;
  
  private States(int stateVal){
    this.stateVal = stateVal;
  }
  
  
  public static States findByValue(int value) { 
    switch (value) {
      case 0:
        return STATE_NOT_IN_USE;
      case 1:
        return STATE_IN_USE;
      case 2:
        return STATE_RESERVED;
      case -1:
        return STATE_REMOVED;
      default:
        return null;
    }
  }
}
