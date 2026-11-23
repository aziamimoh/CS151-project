package com.example;

  //This interface allows different item implementations
  //to be added in the future if needed

public interface ItemType {
    String getName();

    String getType();

    void setType(String type);

    boolean isAvailable();

    void setAvailable(boolean available);
}