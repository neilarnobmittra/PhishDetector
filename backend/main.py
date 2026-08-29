"""
AI-Powered Phishing & Suspicious Message Detector
FastAPI Backend - Basic to Intermediate Project
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Optional
import re
import uvicorn

app = FastAPI(
    title="Phishing Detector API",
    description="Analyze text/SMS/email/URL for phishing and social-engineering risk",
    version="1.0.0"
)

# Allow Android emulator + real devices
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class AnalyzeRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=5000, description="Message or URL to analyze")


class AnalyzeResponse(BaseModel):
    risk: str
    score: int
    explanation: str
    action: str


# ---------- Detection Logic (Rule-based + easy to replace with LLM/ML) ----------

URGENCY_WORDS = [
    "urgent", "immediately", "asap", "act now", "right now", "limited time",
    "expires", "expiring", "last chance", "hurry", "quick", "now!"
]

THREAT_WORDS = [
    "account locked", "account suspended", "account closed", "security alert",
    "unauthorized", "suspicious activity", "verify your account", "confirm your identity",
    "password expired", "login attempt", "unusual sign-in", "blocked", "restricted"
]

ACTION_WORDS = [
    "click here", "click the link", "tap here", "open the link", "verify now",
    "confirm now", "update now", "login now", "sign in", "reset password",
    "download attachment", "open attachment"
]

SENSITIVE_WORDS = [
    "bank", "paypal", "credit card", "ssn", "social security", "otp", "one-time password",
    "verification code", "pin", "cvv", "account number", "routing number"
]

SUSPICIOUS_DOMAINS = [
    "bit.ly", "tinyurl", "goo.gl", "t.co", "ow.ly", "is.gd", "buff.ly",
    "login-", "secure-", "account-", "verify-", "update-", "support-"
]

# Simple brand impersonation patterns
BRAND_PATTERNS = [
    r"paypal", r"amazon", r"apple", r"microsoft", r"google", r"facebook",
    r"instagram", r"netflix", r"bank of america", r"chase", r"wellsfargo",
    r"irs", r"fedex", r"ups", r"dhl"
]


def analyze_text(text: str) -> AnalyzeResponse:
    original = text
    lower = text.lower().strip()

    if not lower:
        raise HTTPException(status_code=400, detail="Text cannot be empty")

    score = 10  # base score
    reasons = []

    # 1. Urgency language
    urgency_hits = [w for w in URGENCY_WORDS if w in lower]
    if urgency_hits:
        score += 18
        reasons.append(f"Urgency language detected ({', '.join(urgency_hits[:3])})")

    # 2. Threat / account problem language
    threat_hits = [w for w in THREAT_WORDS if w in lower]
    if threat_hits:
        score += 22
        reasons.append(f"Threat or account-lock language ({', '.join(threat_hits[:2])})")

    # 3. Call-to-action
    action_hits = [w for w in ACTION_WORDS if w in lower]
    if action_hits:
        score += 15
        reasons.append(f"Strong call-to-action ({', '.join(action_hits[:2])})")

    # 4. Sensitive data request
    sensitive_hits = [w for w in SENSITIVE_WORDS if w in lower]
    if sensitive_hits:
        score += 20
        reasons.append(f"Requests sensitive information ({', '.join(sensitive_hits[:2])})")

    # 5. Presence of links / URLs
    url_pattern = r'https?://[^\s]+|www\.[^\s]+|[a-zA-Z0-9-]+\.(com|net|org|io|co|info|xyz|top|click|link)[^\s]*'
    urls = re.findall(url_pattern, lower)
    if urls:
        score += 12
        reasons.append(f"Contains {len(urls)} link(s)")

        # Suspicious short / fake-looking domains
        for url in urls:
            for sus in SUSPICIOUS_DOMAINS:
                if sus in url:
                    score += 15
                    reasons.append(f"Suspicious domain pattern ({sus})")
                    break

    # 6. Brand impersonation + urgency = high risk
    brand_hits = []
    for pattern in BRAND_PATTERNS:
        if re.search(pattern, lower):
            brand_hits.append(pattern.replace(r"\\", ""))
    if brand_hits and (urgency_hits or threat_hits or action_hits):
        score += 18
        reasons.append(f"Possible brand impersonation ({', '.join(brand_hits[:2])})")

    # 7. All-caps or excessive punctuation (common in phishing)
    if len(re.findall(r'[!]{2,}', original)) > 0 or sum(1 for c in original if c.isupper()) > len(original) * 0.4:
        score += 8
        reasons.append("Excessive capitalization or punctuation")

    # Cap score
    score = min(score, 98)

    # Determine risk level
    if score >= 70:
        risk = "High Risk"
        action = "Do not click any links or share personal information. Delete the message and report it as phishing."
    elif score >= 40:
        risk = "Suspicious"
        action = "Be cautious. Do not click links or reply with personal data. Verify the sender through official channels."
    else:
        risk = "Safe"
        action = "Looks relatively safe, but always stay cautious with unexpected messages."

    if not reasons:
        explanation = "No strong phishing or social-engineering indicators found."
    else:
        explanation = " • ".join(reasons) + "."

    return AnalyzeResponse(
        risk=risk,
        score=score,
        explanation=explanation,
        action=action
    )


@app.get("/")
def root():
    return {
        "message": "Phishing Detector API is running",
        "endpoint": "POST /analyze",
        "docs": "/docs"
    }


@app.post("/analyze", response_model=AnalyzeResponse)
def analyze(request: AnalyzeRequest):
    """
    Analyze a message, SMS, email body or URL for phishing risk.
    """
    try:
        return analyze_text(request.text)
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")


# Optional: health check
@app.get("/health")
def health():
    return {"status": "ok"}


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
