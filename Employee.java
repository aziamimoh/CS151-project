package com.example;

public interface Employee {
    
    String getName();
    int getId();
    int getSkillLevel();
    void setSkillLevel(int skillLevel);

    boolean isWorking();
    void setWorking(boolean working);

    boolean isAssignedTask();
}
