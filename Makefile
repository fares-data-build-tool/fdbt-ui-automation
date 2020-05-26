# LOCAL

test-chrome-local:
	mvn -Dhost=local -Dbrowser=chrome test

test-firefox-local:
	mvn -Dhost=local -Dbrowser=firefox test


# REMOTE

test-windows-chrome-remote:
	mvn -Dhost=remote -Dbrowser=chrome test

test-windows-firefox-remote:
	mvn -Dhost=remote -Dbrowser=firefox test

test-windows-ie-remote:
	mvn -Dhost=remote -Dbrowser=ie test
