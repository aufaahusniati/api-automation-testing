package models;

public class PetModel {
    private long id;
    private String name;
    private String status;

    // Constructor Kosong
    public PetModel() {}

    // Constructor Lengkap
    public PetModel(long id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    // Getter dan Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}