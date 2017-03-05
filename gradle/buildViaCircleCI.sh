#!/bin/bash
# This script will build the project.

SWITCHES="jacocoTestReport testReport --info --stacktrace"

GRADLE_VERSION=$(./gradlew -version | grep Gradle | cut -d ' ' -f 2)
echo "CI_PULL_REQUEST:$CI_PULL_REQUEST"
echo "CIRCLE_TAG:$CIRCLE_TAG"
echo "CIRCLE_BRANCH:$CIRCLE_BRANCH"
echo "TEST:$TEST"

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  echo -e "Build Pull Request #$TRAVIS_PULL_REQUEST => Branch [$CIRCLE_BRANCH]"
  ./gradlew build $SWITCHES
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$CIRCLE_TAG" == "" ]; then
  echo -e 'Build Branch with Snapshot => Branch ['$CIRCLE_BRANCH']'
  ./gradlew -Prelease.travisci=true snapshot $SWITCHES
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$CIRCLE_TAG" != "" ]; then
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
  echo -e 'WARN: Should not be here => Branch ['$CIRCLE_BRANCH']  Tag ['$CIRCLE_TAG']  Pull Request ['$TRAVIS_PULL_REQUEST']'
  ./gradlew build $SWITCHES
fi

EXIT=$?

exit $EXIT

