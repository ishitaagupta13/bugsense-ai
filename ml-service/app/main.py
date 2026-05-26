from fastapi import FastAPI
from pydantic import BaseModel
from app.model import train_model, predict_commit
from app.database import save_prediction

app = FastAPI(title="BugSense ML Service")

class CommitData(BaseModel):
    sha: str
    message: str
    author_login: str = "unknown"
    lines_added: int = 0
    lines_deleted: int = 0
    files_changed: int = 0
    committed_at: str = None

class PredictionRequest(BaseModel):
    commit: CommitData

@app.get("/health")
def health():
    return {"status": "BugSense ML Service is running"}

@app.post("/train")
def train():
    """Train the ML model on stored commits"""
    result = train_model()
    return result

@app.post("/predict")
def predict(request: PredictionRequest):
    """Predict risk score for a commit"""
    commit_dict = request.commit.dict()
    result = predict_commit(commit_dict)

    # Save prediction back to DB
    save_prediction(
        request.commit.sha,
        result["risk_score"],
        result["risk_level"]
    )

    return result

@app.get("/")
def root():
    return {"message": "BugSense ML Service", "version": "1.0.0"}