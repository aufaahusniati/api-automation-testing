package models;

public class OrderModel {
    private int id;
    private long petId;
    private int quantity;
    private String shipDate;
    private String status;
    private boolean complete;

    // Constructor Kosong
    public OrderModel() {}

    // Constructor Lengkap
    public OrderModel(int id, long petId, int quantity, String shipDate, String status, boolean complete) {
        this.id = id;
        this.petId = petId;
        this.quantity = quantity;
        this.shipDate = shipDate;
        this.status = status;
        this.complete = complete;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getPetId() { return petId; }
    public void setPetId(long petId) { this.petId = petId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getShipDate() { return shipDate; }
    public void setShipDate(String shipDate) { this.shipDate = shipDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isComplete() { return complete; }
    public void setComplete(boolean complete) { this.complete = complete; }
}