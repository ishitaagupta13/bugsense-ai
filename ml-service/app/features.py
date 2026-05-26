import pandas as pd
import numpy as np

def extract_features(df: pd.DataFrame) -> pd.DataFrame:
    """Extract ML features from raw commit data"""

    features = pd.DataFrame()

    # Code change features
    features["lines_added"] = df["lines_added"].fillna(0)
    features["lines_deleted"] = df["lines_deleted"].fillna(0)
    features["files_changed"] = df["files_changed"].fillna(0)
    features["total_changes"] = features["lines_added"] + features["lines_deleted"]
    features["churn_ratio"] = features["lines_deleted"] / (features["lines_added"] + 1)

    # Commit message features
    features["message_length"] = df["message"].str.len().fillna(0)
    features["is_fix_commit"] = df["message"].str.lower().str.contains(
        "fix|bug|patch|hotfix|resolve|issue", na=False
    ).astype(int)
    features["is_feature_commit"] = df["message"].str.lower().str.contains(
        "feat|feature|add|new|implement", na=False
    ).astype(int)

    # Time features
    if "committed_at" in df.columns:
        df["committed_at"] = pd.to_datetime(df["committed_at"])
        features["hour_of_day"] = df["committed_at"].dt.hour
        features["day_of_week"] = df["committed_at"].dt.dayofweek
        features["is_weekend"] = (features["day_of_week"] >= 5).astype(int)
        features["is_late_night"] = (
            (features["hour_of_day"] >= 22) | (features["hour_of_day"] <= 5)
        ).astype(int)
    else:
        features["hour_of_day"] = 12
        features["day_of_week"] = 0
        features["is_weekend"] = 0
        features["is_late_night"] = 0

    # Developer experience features
    if "author_login" in df.columns:
        commit_counts = df.groupby("author_login").cumcount()
        features["developer_commit_count"] = commit_counts
    else:
        features["developer_commit_count"] = 0

    return features

def get_risk_level(score: float) -> str:
    if score >= 0.7:
        return "HIGH"
    elif score >= 0.4:
        return "MEDIUM"
    else:
        return "LOW"