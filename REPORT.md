# BÁO CÁO DỰ ÁN: MV CINEMA

## 1) Thông tin chung

- **Tên dự án**: MV CINEMA  
- **Mục tiêu**: Xây dựng website xem thông tin phim và phát phim trực tuyến (HLS), dữ liệu lấy từ **OPhim API**.
- **Mô hình**: Web **server-side rendering** (SSR) bằng Spring Boot + Thymeleaf.

## 2) Công nghệ sử dụng

### 2.1 Backend (Java)

- **Ngôn ngữ**: Java **17**
- **Framework**: **Spring Boot 3.4.3**
  - `spring-boot-starter-web`: xây dựng web app, routing (MVC), embedded Tomcat
  - `spring-boot-starter-thymeleaf`: render giao diện HTML phía server
  - `spring-boot-starter-cache`: hạ tầng cache
  - `spring-boot-starter-actuator`: health/info phục vụ giám sát & deploy
- **Cache**: **Caffeine** (`com.github.ben-manes.caffeine:caffeine`)
  - Dùng annotation `@Cacheable` để cache taxonomy (thể loại/quốc gia), danh sách, tìm kiếm…
  - TTL cấu hình trong `application.yml` (`cache.ttl-seconds.*`)
- **HTTP client**: `RestClient` (Spring) để gọi OPhim API
- **Build tool**: **Maven** (`pom.xml`)

### 2.2 Frontend (UI)

- **Template engine**: **Thymeleaf**
  - Templates: `src/main/resources/templates/*.html` (`home.html`, `browse.html`, `film.html`, `watch.html`…)
  - Static assets: `src/main/resources/static/`
- **Phát video**: **HLS (.m3u8) + hls.js**
  - Trang xem: `/watch/{slug}?server=0&ep=full`

### 2.3 Dữ liệu bên thứ ba (API)

- **Nguồn dữ liệu**: **OPhim API** (base URL cấu hình trong `application.yml`)
- Một số endpoint sử dụng:
  - `GET /the-loai`, `GET /quoc-gia`
  - `GET /v1/api/danh-sach/{slug}?page=...`
  - `GET /phim/{slug}`
  - `GET /v1/api/tim-kiem?keyword=...&page=...`

### 2.4 DevOps / Triển khai

- **Docker**:
  - `Dockerfile` multi-stage: build bằng Maven → chạy bằng JRE (Alpine)
  - App chạy trong container và lắng nghe cổng **8081**
- **Deploy**:
  - Đã cấu hình triển khai trên **Railway** (khuyến nghị always-on so với Render free sleep)
  - Lưu ý port: Spring Boot đọc `PORT` env (fallback `8081`) để tương thích các PaaS

## 3) Kiến trúc & luồng hoạt động

### 3.1 Kiến trúc tổng quan

- Trình duyệt gọi các route (MVC Controller)
- Controller gọi service `OPhimClient` để lấy dữ liệu từ OPhim API
- Dữ liệu được đưa vào `Model` và render bằng Thymeleaf template
- Một số dữ liệu được cache (Caffeine) để giảm độ trễ và hạn chế gọi API lặp

### 3.2 Các route chính

- **Home**: `/` (phim mới cập nhật + gợi ý)
- **Browse**: `/browse/{listSlug}` + query filter (genre/country/year/type/sort)
- **Search**: `/search?q=...`
- **Detail**: `/film/{slug}`
- **Watch**: `/watch/{slug}?server=...&ep=...`

## 4) Cấu trúc thư mục chính

- `src/main/java/.../web`: các **Controller** (Home/Browse/Film/Search…)
- `src/main/java/.../ophim`: client + DTO gọi OPhim API
- `src/main/resources/templates`: Thymeleaf HTML templates
- `src/main/resources/static`: tài nguyên tĩnh (CSS/JS/ảnh…)
- `src/main/resources/application.yml`: cấu hình runtime, cache TTL, actuator…

## 5) Hướng dẫn chạy dự án

### 5.1 Chạy local

```bash
mvn spring-boot:run
```

Mở: `http://localhost:8081/`

### 5.2 Chạy bằng Docker

```bash
docker build -t mv-cinema .
docker run --rm -p 8081:8081 mv-cinema
```

Mở: `http://localhost:8081/`

## 6) Ghi chú

- Dự án phụ thuộc API bên thứ ba (OPhim), vì vậy tốc độ/độ ổn định phụ thuộc vào upstream.
- Actuator endpoint dùng cho healthcheck: `GET /actuator/health`.

