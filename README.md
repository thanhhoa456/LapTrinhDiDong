# Ứng dụng Ôn Thi Bằng Lái Xe A1
## Sinh viên thực hiện:

Đàng Thị Thanh Hoa_22110326

Nguyễn Thị Nỡ_22110392

## 📱 Giới thiệu
Ứng dụng hỗ trợ người dùng ôn luyện và làm bài thi thử bằng lái xe A1. Gồm các chức năng như:
- Làm bài thi trắc nghiệm
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
- **Ngôn ngữ:** Java 
- **Database cục bộ:** SQLite (lưu câu hỏi, đề thi tạm thời)
- **Lưu trữ cài đặt và trạng thái:** SharedPreferences

---

## ⚙️ Chức năng chính
1. Đăng ký và đăng nhập
Người dùng có thể đăng ký tài khoản bằng email và mật khẩu. Sau khi đăng ký, mã OTP sẽ được gửi tới email để xác thực tài khoản. Ngoài ra, người dùng có thể đặt lại mật khẩu nếu quên, cũng thông qua mã OTP gửi qua email.

2. Học lý thuyết
Cung cấp toàn bộ nội dung lý thuyết ôn thi bằng lái xe A1, được chia theo từng chương và từng chủ đề cụ thể, giúp người dùng học dễ dàng và hệ thống hơn.

3. Thi sát hạch
Mô phỏng bài thi sát hạch lý thuyết như kỳ thi thật, có giới hạn thời gian và hệ thống sẽ tự động chấm điểm sau khi nộp bài.

4. Học biển báo
Tổng hợp đầy đủ các loại biển báo giao thông (biển cấm, biển nguy hiểm, biển hiệu lệnh, biển chỉ dẫn…), kèm hình ảnh minh họa và giải thích chi tiết cho từng loại.

5. Xem mẹo thi
Cung cấp các mẹo làm bài, mẹo ghi nhớ lý thuyết và biển báo nhanh chóng, giúp tăng khả năng làm bài đúng và đạt điểm cao trong kỳ thi thật.

6. Làm lại các câu sai
Hệ thống sẽ tự động ghi nhớ các câu hỏi người dùng đã trả lời sai và cho phép người dùng ôn tập lại những câu này nhằm cải thiện kết quả.

7. Cài đặt
Cho phép người dùng thay đổi các thiết lập cá nhân như tốc độ đọc, cỡ chữ, màu nền... giúp tăng trải nghiệm học tập.


