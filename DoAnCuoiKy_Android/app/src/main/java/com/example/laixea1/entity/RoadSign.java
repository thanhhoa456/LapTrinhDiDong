package com.example.laixea1.entity;

public class RoadSign {
    private int id;
    private int rsGroupId;
    private String name;
    private String description;
    private String image; // Sửa từ byte[] thành String để lưu chuỗi Base64

    public RoadSign(int id, int rsGroupId, String name, String description, String image) {
        this.id = id;
        this.rsGroupId = rsGroupId;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public int getId() { return id; }
    public int getRsGroupId() { return rsGroupId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImage() { return image; }

    // (Tùy chọn) Thêm phương thức để giải mã Base64 nếu cần byte[]
    public byte[] getImageBytes() {
        if (image != null && !image.isEmpty()) {
            return android.util.Base64.decode(image, android.util.Base64.DEFAULT);
        }
        return null;
    }
}