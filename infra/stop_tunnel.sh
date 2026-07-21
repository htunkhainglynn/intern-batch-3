#!/usr/bin/env bash
pkill -f "bore local" 2>/dev/null && echo "All bore tunnels stopped." || echo "No bore tunnels were running."
