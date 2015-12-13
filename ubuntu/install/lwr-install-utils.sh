
function print() {
	printf "$*"
	echo $* >>$LOG 2>&1
}

