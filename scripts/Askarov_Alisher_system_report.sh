#!/bin/bash
# Askarov_Alisher_system_report.sh - system monitoring report of Alisher Askarov
# Usage: ./scripts/Askarov_Alisher_system_report.sh [log_file]
# Without an argument the report is appended to logs/Askarov_Alisher_system_report.log

STUDENT="Alisher Askarov"
PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_FILE="${1:-$PROJECT_DIR/logs/Askarov_Alisher_system_report.log}"

mkdir -p "$(dirname "$LOG_FILE")"

# show a line on the screen and append it to the log at the same time
log() {
    echo "$1" | tee -a "$LOG_FILE"
}

# same for the output of a command
log_cmd() {
    "$@" 2>&1 | tee -a "$LOG_FILE"
}

log "####################################################"
log "# System report: $STUDENT"
log "####################################################"
log "Full name : $STUDENT (group IT2-2304, ID 38226)"
log "Date/time : $(date '+%d.%m.%Y %H:%M:%S')"
log "Username  : $(whoami)"
log "Hostname  : $(hostname)"
log "OS        : $(. /etc/os-release && echo "$PRETTY_NAME")"
log "Kernel    : $(uname -r)"

log ""
log "[CPU]"
log "Model     : $(lscpu | grep 'Model name' | sed 's/Model name: *//')"
log "Cores     : $(nproc)"
log "Load avg  : $(cut -d ' ' -f 1-3 /proc/loadavg)"

log ""
log "[Memory]"
log_cmd free -h

log ""
log "[Disk usage]"
log_cmd df -h /
log "Project   : $(du -sh "$PROJECT_DIR" | cut -f 1) used by $PROJECT_DIR"

log ""
log "[Available disk space]"
log "$(df -h --output=avail / | tail -n 1 | tr -d ' ') free on /"
log "-------------------- end of report -----------------"
