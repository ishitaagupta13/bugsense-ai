import xgboost as xgb
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
import os

from app.database import get_commits_for_training
from app.features import extract_features, get_risk_level

MODEL_PATH = "bugsense_model.json"

def label_commits(df: pd.DataFrame) -> pd.DataFrame:
    """Auto label commits using SZZ heuristic"""

    # Mark fix commits
    fix_pattern = "fix|bug|patch|hotfix|resolve|issue|error|crash"
    df["is_fix"] = df["message"].str.lower().str.contains(
        fix_pattern, na=False
    )

    # Label previous commits as buggy
    # Simple heuristic: commit before a fix in same repo = potentially buggy
    df = df.sort_values("committed_at")
    df["is_buggy"] = False

    for repo_id in df["repository_id"].unique():
        repo_df = df[df["repository_id"] == repo_id].copy()
        fix_indices = repo_df[repo_df["is_fix"]].index

        for fix_idx in fix_indices:
            pos = repo_df.index.get_loc(fix_idx)
            if pos > 0:
                prev_idx = repo_df.index[pos - 1]
                df.loc[prev_idx, "is_buggy"] = True

    return df

def train_model():
    """Train XGBoost model on stored commits"""

    print("Fetching commits from database...")
    df = get_commits_for_training()

    if len(df) < 10:
        return {
            "error": "Not enough commits to train. Connect more repositories first.",
            "commit_count": len(df)
        }

    print(f"Found {len(df)} commits")

    # Label commits
    print("Labeling commits...")
    df = label_commits(df)

    # Extract features
    print("Extracting features...")
    X = extract_features(df)
    y = df["is_buggy"].astype(int)

    print(f"Buggy commits: {y.sum()}, Clean commits: {(y==0).sum()}")

    # Split data
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )

    # Train XGBoost
    print("Training XGBoost model...")
    model = xgb.XGBClassifier(
        n_estimators=100,
        max_depth=6,
        learning_rate=0.1,
        scale_pos_weight=len(y[y==0]) / max(len(y[y==1]), 1),
        random_state=42,
        eval_metric="logloss"
    )
    model.fit(X_train, y_train)

    # Evaluate
    y_pred = model.predict(X_test)
    report = classification_report(y_test, y_pred, output_dict=True)

    # Save model
    model.save_model(MODEL_PATH)
    print(f"Model saved to {MODEL_PATH}")

    return {
        "status": "success",
        "commits_used": len(df),
        "accuracy": report["accuracy"],
        "message": "Model trained successfully"
    }

def predict_commit(commit_data: dict) -> dict:
    """Predict risk score for a single commit"""

    if not os.path.exists(MODEL_PATH):
        return {
            "risk_score": 0.5,
            "risk_level": "UNSCORED",
            "message": "Model not trained yet"
        }

    model = xgb.XGBClassifier()
    model.load_model(MODEL_PATH)

    df = pd.DataFrame([commit_data])
    features = extract_features(df)

    risk_score = float(model.predict_proba(features)[0][1])
    risk_level = get_risk_level(risk_score)

    return {
        "risk_score": round(risk_score, 4),
        "risk_level": risk_level
    }