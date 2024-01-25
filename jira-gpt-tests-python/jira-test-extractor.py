import os

from jira import JIRA 
from dotenv import load_dotenv

load_dotenv()

JIRA_URL = os.getenv("JIRA_URL")
JIRA_USERNAME = os.getenv("JIRA_USERNAME")
JIRA_TOKEN = os.getenv("JIRA_TOKEN")

print("JIRA_URL:", JIRA_URL)
print("JIRA_USERNAME:", JIRA_USERNAME)
print("JIRA_TOKEN:", JIRA_TOKEN)