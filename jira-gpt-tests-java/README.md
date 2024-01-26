# JIRA GPT TESTS 


### List all projects

    mvn exec:java -DJIRA_TOKEN=$JIRA_TOKEN -Dexec.args="--list"

### Get a single project information

    mvn exec:java -D JIRA_TOKEN=$JIRA_TOKEN -Dexec.args="--project 15000"

### Get a single issue information

    mvn exec:java -D JIRA_TOKEN=$JIRA_TOKEN -Dexec.args="--issue QAUTO-1234"

### Get test info include steps

    mvn exec:java -DJIRA_TOKEN=$JIRA_TOKEN -Dexec.args="--test 221525"

