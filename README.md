monitor-ssh-command
===================
summary: This plugin remotely executes a command through SSH and monitors the string/numeric output, exit code and response time.


tags:
- ssh
- java
version_compatibility:
- SSH Command Monitor 1.0: [7.3,7.4,7.5,7.6]

download_files: 
description: >
 This plugin remotely executes a command through SSH and monitors the string/numeric output, exit code and response time.
 
supported_monitoring_stations: [7.3,7.4,7.5,7.6]

supported_agents: 

installation_notes: n/a

dependencies: None

input_variables:

- SSH Username
- SSH Password
- SSH Port
- SSH Command (e.g. uptime | cut -f4 -d, | cut -f2 -d&#58; | sed '/^$/d;s/[[:blank:]]//g') 
- Result Datatype (String/Numeric)

output_variables:

- Returned Data (String)
- Returned Data (Numeric)
- Exit Code
- Response time

languages_used: [Java]

