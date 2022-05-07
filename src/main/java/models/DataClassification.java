package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataClassification {
    /**
     * Data field id.
     */
    private int id;

    /**
     * Data field column name.
     */
    private String columnName;

    /**
     * Data field column type.
     */
    private String columnType;

    /**
     * Data field quasi-sensitivity.
     */
    private boolean qi;

    /**
     * Data field sensitivity.
     */
    private boolean sa;

    public DataClassification(int id, String columnName, String columnType, boolean qi, boolean sa) {
        this.id = id;
        this.columnName = columnName;
        this.columnType = columnType;
        this.qi = qi;
        this.sa = sa;
    }

    public DataClassification(String[] data) {
        this.id = Integer.parseInt(data[0]);
        this.columnName = data[1];
        this.columnType = data[2];
        this.qi = data[3].equals("1");
        this.sa = data[4].equals("1");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public boolean isQi() {
        return qi;
    }

    public void setQi(boolean qi) {
        this.qi = qi;
    }

    public boolean isSa() {
        return sa;
    }

    public void setSa(boolean sa) {
        this.sa = sa;
    }
}
