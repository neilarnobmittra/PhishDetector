# PhishDetector

**AI-Powered Phishing & Suspicious Message Detector**

PhishDetector is my first Android application built using **Java**.  
Users can paste any suspicious SMS, email text, or URL, and the app analyzes it to detect possible phishing or social-engineering attempts.

---

## Features

- Paste suspicious SMS, email, or URL
- Real-time risk analysis (Safe / Suspicious / High Risk)
- Confidence score
- Clear explanation of the result
- Recommended action
- Local scan history
- Share result option
- Clean Material Design UI

---

## Tech Stack

| Layer          | Technology                  |
|----------------|-----------------------------|
| Frontend       | Android (Java + XML)        |
| Backend        | Python + FastAPI            |
| Networking     | Retrofit + OkHttp           |
| Local Storage  | SharedPreferences           |
| UI             | Material Design             |

---

## How to Run

### 1. Backend

```bash
cd backend
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
python main.py
Backend will run at: http://localhost:8000
2. Android App

Open the android-app folder in Android Studio
Wait for Gradle Sync
Run the app on Emulator or real device

For real device: Change BASE_URL in RetrofitClient.java to your computer’s local IP.

Project Structure
textphishing-detector-v2/
├── backend/
│   ├── main.py
│   └── requirements.txt
├── android-app/
│   └── app/src/main/
│       ├── java/
│       └── res/
└── README.md

Sample Test Messages
High Risk
textUrgent! Your bank account is locked. Click http://fake-bank-login.com to verify now.
Suspicious
textWe noticed unusual activity on your PayPal account. Please confirm your identity.
Safe
textHey, are we still meeting for lunch tomorrow?

Future Improvements

On-device AI model (TensorFlow Lite)
Real-time SMS detection
User feedback system
Dark mode


Author
Neilarnob Mittra

First Android Project – 2026
