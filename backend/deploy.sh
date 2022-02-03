#!/bin/bash
npm --prefix ./functions install ./functions
# firebase deploy --only functions:trigger_notify
firebase deploy --only functions

