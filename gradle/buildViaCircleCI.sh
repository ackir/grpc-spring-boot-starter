#!/bin/bash
# This script will build the project.

SWITCHES="jacocoTestReport testReport --info --stacktrace"

GRADLE_VERSION=$(./gradlew -version | grep Gradle | cut -d ' ' -f 2)
echo "CI_PULL_REQUEST:$CI_PULL_REQUEST"
echo "CIRCLE_TAG:$CIRCLE_TAG"
echo "CIRCLE_BRANCH:$CIRCLE_BRANCH"

if [ "x$CI_PULL_REQUEST" != "x" ]; then
  echo -e "Build Pull Request #$CI_PULL_REQUEST => Branch [$CIRCLE_BRANCH]"
  ./gradlew build $SWITCHES
elif [ "x$CI_PULL_REQUEST" == "x" ] && [ "x$CIRCLE_TAG" == "x" ]; then
  echo -e 'Build Branch with Snapshot => Branch ['$CIRCLE_BRANCH']'
  ./gradlew -Prelease.travisci=true snapshot $SWITCHES
elif [ "x$CI_PULL_REQUEST" == "x" ] && [ "x$CIRCLE_TAG" != "x" ]; then
  echo -e 'Build Branch for Release => Branch ['$CIRCLE_BRANCH']  Tag ['$CIRCLE_TAG']'
  case "$CIRCLE_TAG" in
  *-rc\.*)
    ./gradlew -Prelease.travisci=true -Prelease.useLastTag=true candidate $SWITCHES
    ;;
  *)
    ./gradlew -Prelease.travisci=true -Prelease.useLastTag=true final pushImage $SWITCHES
    ;;
  esac
else
  echo -e 'WARN: Should not be here => Branch ['$CIRCLE_BRANCH']  Tag ['$CIRCLE_TAG']  Pull Request ['$CI_PULL_REQUEST']'
  ./gradlew build $SWITCHES
fi

EXIT=$?

exit $EXIT

