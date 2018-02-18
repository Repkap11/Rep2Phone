#!/bin/bash
set -e
if [ "$#" -ne 1 ]; then
	echo "usage: Please spesify the version of the app to upload."
	exit
fi
APP_VERSION=$1
LOCAL_FILE=./build/outputs/apk/live/debug/app-live-debug.apk

cp $LOCAL_FILE ./rep2phone-live-debug-$APP_VERSION.apk
REMOTE_USERNAME=paul
REMOTE_SERVER=repkap11.com
REMOTE_FILE=/home/paul/website/rep2phone/Rep2Phone.apk
REMOTE_TARGET=$REMOTE_USERNAME@$REMOTE_SERVER:$REMOTE_FILE
echo "Copying $LOCAL_FILE to $REMOTE_TARGET"
rsync --update $LOCAL_FILE $REMOTE_TARGET
firebase --project rep2phone database:set -y -d $APP_VERSION /user_group/appVersion
echo "Upload Complete"
