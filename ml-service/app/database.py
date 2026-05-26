from sqlalchemy import create_engine, text
import pandas as pd
import os
from dotenv import load_dotenv

load_dotenv()

DATABASE_URL = os.getenv(
    "DATABASE_URL",
    "postgresql://postgres:admin@localhost:5432/bugsense_db"
)

engine = create_engine(DATABASE_URL)

def get_commits_for_training(repo_id: int = None):
    """Fetch commits from PostgreSQL for training"""

    query = """
        SELECT 
            c.id,
            c.sha,
            c.message,
            c.author_login,
            c.lines_added,
            c.lines_deleted,
            c.files_changed,
            c.committed_at,
            c.is_buggy,
            c.repository_id
        FROM commits c
        WHERE c.lines_added IS NOT NULL
    """

    if repo_id:
        query += f" AND c.repository_id = {repo_id}"

    df = pd.read_sql(text(query), engine.connect())
    return df

def save_prediction(commit_sha: str, risk_score: float, risk_level: str):
    """Save prediction back to PostgreSQL"""

    query = text("""
        UPDATE commits 
        SET risk_score = :risk_score, risk_level = :risk_level
        WHERE sha = :sha
    """)

    with engine.connect() as conn:
        conn.execute(query, {
            "risk_score": risk_score,
            "risk_level": risk_level,
            "sha": commit_sha
        })
        conn.commit()