# Hướng dẫn deploy Student Manager lên Render

## Lưu ý quan trọng: Database

Render **không** cung cấp SQL Server. Bạn cần một trong hai:

- **Azure SQL Database** (khuyến nghị): tạo database trên [Azure Portal](https://portal.azure.com), lấy connection string.
- **SQL Server trên VPS/cloud** khác có public IP và cho phép kết nối từ internet.

Ứng dụng trên Render sẽ kết nối tới database này qua biến môi trường.

---

## Bước 1: Đẩy code lên GitHub

1. Tạo repo trên GitHub (nếu chưa có).
2. Trong thư mục dự án chạy:

```powershell
git init
git add .
git commit -m "Initial commit - Student Manager API"
git branch -M main
git remote add origin https://github.com/<username>/<repo-name>.git
git push -u origin main
```

---

## Bước 2: Tạo tài khoản Render và kết nối repo

1. Vào [https://render.com](https://render.com) → **Get Started** (đăng ký bằng GitHub).
2. Đăng nhập và cho Render quyền truy cập repository.
3. Trên **Dashboard** → **New +** → chọn **Web Service**.

---

## Bước 3: Cấu hình Web Service

1. **Connect repository**: chọn repo **studentmanager** (GitHub của bạn).
2. **Name**: `studentmanager` (hoặc tên bạn muốn).
3. **Region**: chọn gần bạn (ví dụ Singapore).
4. **Runtime**: chọn **Docker** (Render sẽ dùng Dockerfile trong repo).
5. **Branch**: `main`.

### Biến môi trường (Environment Variables)

Bấm **Add Environment Variable** và thêm:

| Key | Value (thay bằng thông tin thật) |
|-----|----------------------------------|
| `SPRING_DATASOURCE_URL` | `jdbc:sqlserver://<host>:1433;databaseName=student;encrypt=true;trustServerCertificate=true` |
| `SPRING_DATASOURCE_USERNAME` | Username đăng nhập SQL Server |
| `SPRING_DATASOURCE_PASSWORD` | Mật khẩu SQL Server |

Ví dụ Azure SQL:

```
SPRING_DATASOURCE_URL=jdbc:sqlserver://yourserver.database.windows.net:1433;database=student;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net
```

6. **Instance type**: chọn **Free** (nếu có) hoặc plan trả phí.
7. Bấm **Create Web Service**.

---

## Bước 4: Deploy và lấy URL

- Render sẽ tự build Docker image và chạy container.
- Đợi **Deploy** chuyển sang trạng thái **Live** (có thể vài phút).
- URL ứng dụng dạng: **`https://studentmanager.onrender.com`** (tên có thể khác theo tên service bạn đặt).

---

## Bước 5: Test API trên Postman

1. Import file **`StudentManager_API.postman_collection.json`** vào Postman (xem mục bên dưới).
2. Trong collection, chỉnh biến **`base_url`**:
   - Local: `http://localhost:8080`
   - Render: `https://studentmanager.onrender.com` (hoặc URL Render của bạn).
3. Gửi từng request để kiểm tra API.

---

## Lỗi thường gặp

- **Build failed**: Kiểm tra Dockerfile, đảm bảo `pom.xml` và code build được trên máy (`mvn package`).
- **Application failed to start**: Kiểm tra lại `SPRING_DATASOURCE_*`: URL, username, password và firewall (SQL Server/Azure có cho phép IP của Render không).
- **Free tier sleep**: Ở plan Free, service có thể “ngủ” sau 15 phút không có request; request đầu có thể chậm vài chục giây.

Nếu bạn dùng **Azure SQL**, nhớ mở **Firewall** trong Azure cho phép “Azure services” hoặc thêm IP (Render có danh sách IP nếu cần).
