package models;

public class DataSensitivity {
    private int id;
    private String columnName;
    private String columnType;
    private boolean qi;
    private boolean sa;

    public DataSensitivity(int id, String columnName, String columnType, boolean qi, boolean sa) {
        this.id = id;
        this.columnName = columnName;
        this.columnType = columnType;
        this.qi = qi;
        this.sa = sa;
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
