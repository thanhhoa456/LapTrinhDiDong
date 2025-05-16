# Ứng dụng Ôn Thi Bằng Lái Xe A1
## Sinh viên thực hiện:

Đàng Thị Thanh Hoa_22110326

Nguyễn Thị Nỡ_22110392

## 📱 Giới thiệu
Ứng dụng hỗ trợ người dùng ôn luyện và làm bài thi thử bằng lái xe A1. Gồm các chức năng như:
- Làm bài thi trắc nghiệm theo đề ngẫu nhiên
- Ôn luyện câu hỏi theo từng chương
- Xem kết quả, theo dõi tiến độ học tập
- Lưu trạng thái học, kết quả, tài khoản người dùng

---

## 🛠️ Công nghệ sử dụng

### Backend (API):
- **Ngôn ngữ:** Java
- **Framework:** Spring Boot
- **Cơ sở dữ liệu:** MySQL
- **Cấu trúc:** RESTful API

### Frontend (Mobile):
- **Nền tảng:** Android
- **Ngôn ngữ:** Java / Kotlin
- **Database cục bộ:** SQLite (lưu câu hỏi, đề thi tạm thời)
- **Lưu trữ cài đặt và trạng thái:** SharedPreferences

---

## ⚙️ Chức năng chính

### Người dùng:
- Đăng ký / Đăng nhập tài khoản
- Làm bài thi theo đề ngẫu nhiên hoặc ôn theo chương
- Xem kết quả bài thi
- Ghi nhớ câu sai để ôn lại
- Lưu trạng thái học (bằng SharedPreferences)

### Quản trị (qua API hoặc giao diện quản lý):
- Quản lý bộ câu hỏi, đề thi
- Cập nhật dữ liệu từ file CSV hoặc nhập tay
- Theo dõi người dùng và kết quả

---

## 🧩 Cấu trúc hệ thống

### Backend:
