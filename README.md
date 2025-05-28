
# ⚽ UniBET - Your Football Stats

A dynamic web application built with **Java + Spring Boot** that provides performance statistics and rankings of football teams based on user predictions. This project features real-time graphical dashboards, session-based user tracking, and personalized visual feedback.

## Project Windows

### Login:

![image](https://github.com/user-attachments/assets/ee460345-24ff-4d38-be9b-5a7e96f8aa37)

### Home:

![image](https://github.com/user-attachments/assets/15d9ea82-f595-4d48-a2be-0f8e814db5dc)


## 📊 Features

- ✅ Calculates and displays percentage of correct and incorrect predictions (Greens and Reds)
- 📈 Generates graphical dashboards dynamically using Python (via backend integration)
- 🏆 Highlights the best and worst performing teams
- 👤 User session tracking for individualized stats
- 🌐 REST API endpoints for performance data and image generation
- 🔒 Secure session-based `userId` to personalize content and file naming

## 🚀 Technologies Used

- **Java 17**
- **Spring Boot 3**
- **Thymeleaf / HTML / JS / Bootstrap**
- **Python + Matplotlib** (for chart generation)
- **MySQL** (or other SQL-compatible database)

## 📂 Folder Structure

```Mathematical
src/  
└── main/  
├── java/  
│ └── com.futweb/  
│ ├── config/ # Spring configuration classes  
│ ├── controllers/ # REST controllers  
│ ├── models/ # Entity classes and models  
│ ├── records/ # Record-based DTOs  
│ ├── repositories/ # Spring Data JPA interfaces  
│ └── services/ # Business logic and Python integration  
│ ├── dashboard.py # Python script for chart generation  
│ ├── performance.py # Python logic for performance calculations  
│ ├── sendcode.py # Python utility for sending codes (e.g., email/SMS)  
│ └── ...Service.java # Java service classes (GameService, UserService, etc.)  
│  
├── resources/  
│ ├── application.properties # Spring Boot configuration file  
│ ├── static/  
│ │ ├── css/ # Custom styles  
│ │ ├── img/ # Generated dashboards and static images  
│ │ └── javascript/ # JavaScript files  
│ └── templates/ # Thymeleaf HTML templates  
│  
└── uploads/  
├── profile/ # Uploaded user profile images  
└── teams/ # Uploaded team-related content
```


## 🔁 Workflow

1. User logs in and submits predictions.
2. Backend fetches performance data from the database.
3. A dashboard image is generated using Python:
   - Filename: `dashboard_<userId>.png`
4. The image is displayed dynamically in the frontend using JavaScript.

## 📌 API Endpoints

- `GET /api/performance`  
  Returns team stats (Greens and Reds)

- `GET /api/performancestats`  
  Returns total correct/incorrect predictions

- `POST /api/generate-dashboard`  
  Triggers Python chart generation, expects:

## 🧠 Example JavaScript Logic

```javascript
const userId = document.getElementById('userId')?.value;
fetch(`/api/generate-dashboard?acertos=${totalAcertos}&erros=${totalErros}&userId=${userId}`, {
method: 'POST'
})
.then(() => {
const imgSrc = `/img/dashboard_${userId}.png?v=${Date.now()}`;
document.getElementById('rightanswers').innerHTML = `<img src="${imgSrc}" />`;
});
```

## 🧪 How to Run

1. Clone the project
```bash
git clone https://github.com/will-csc/UniBET-SoccerStats
```

2. Install Python requirements
```bash
pip install matplotlib
```

3. Start the Spring Boot app
```bash
./mvnw spring-boot:run
```

4. Log in and interact with the dashboard!
```bash
http://localhost:8080/
```


---

Let me know if you want a version in Portuguese or if you'd like to include a license or badges (build status, version, etc).
