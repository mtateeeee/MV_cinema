# MV CINEMA (Spring Boot + OPhim API)

A simple film browsing + watching website built with **Spring Boot + Thymeleaf**, using the **OPhim API** as the data source.
## Màn hình ứng dụng

### 1. Trang chủ (Home Page)
![Home Page](img/HOME%20PAGE.png)

### 2. Trang tải phim (Loading Page)
![Loading Movie](img/LOADING%20MOVIE%20PAGE.png)

### 3. Chi tiết phim (Movie Detail)
![Movie Detail](img/MOVIE%20DETAIL%20PAGE.png)

### 4. Xem phim (Watch Page)
![Movie Watch](img/MOVIE%20WATCH%20PAGE.png)
## Requirements

- Java 17+
- Maven (or use the Maven wrapper if you add it later)

## Run

From the project folder:

```bash
mvn spring-boot:run
```

Then open:

- `http://localhost:8081/`

> Note: the app is configured to run on **port 8081** (because 8080 was already in use on your machine).

## Main pages

- `/` Home (latest updates)
- `/browse/{listSlug}` Browse a list with filters
  - Example: `/browse/phim-moi-cap-nhat`
- `/search?q=batman` Search
- `/film/{slug}` Movie detail
  - Example: `/film/ban-be-deu-ghet-toi`
- `/watch/{slug}?server=0&ep=full` Watch (HLS `.m3u8` via `hls.js`)

## API endpoints used (OPhim)

- `GET https://ophim1.com/the-loai` (genres)
- `GET https://ophim1.com/quoc-gia` (countries)
- `GET https://ophim1.com/danh-sach/{slug}?page=...` (lists)
- `GET https://ophim1.com/phim/{slug}` (detail + episodes)
- `GET https://ophim1.com/v1/api/tim-kiem?keyword=...&page=...` (search)

## Config

Edit `src/main/resources/application.yml`:

- `server.port`
- `ophim.base-url`
- `ophim.image-base-url`
- `ophim.request-timeout-ms`
- cache TTLs under `cache.ttl-seconds.*`

