#include <stdio.h>

int main(int argc, char *argv[]){
	char* doc = argv[1];
	int i=0, pwn;
	char tmp[2];
	int whites=0; 
	int blacks=0;
	for(i;i<100;i++){
		sprintf(tmp, "%c", doc[i]);
		pwn = atoi(tmp);		
		if(pwn==1||pwn==11){			
			blacks+=1;
		}
		if(pwn==2||pwn==22){
			whites+=1	;
		}
	}
	printf("whites%d-blacks%d", whites, blacks);
}
