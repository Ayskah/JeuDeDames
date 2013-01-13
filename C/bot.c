#include <stdio.h>
const int MINEVAL=-100000;
const int MAXEVAL=100000;

int main(int argc, char **argv){

}
calcIA(Jeu *jeu, int prof){
    int i,j,tmp;
    int max = MINEVAL;
    int maxi=-1,maxj=-1;

    if((prof!=0)){
        for(i=0;i<10; i++)
            for(j=0;j<10;j++){
                if(estVide(i,j)){
                    joue(i,j);
                    tmp = calcMin(jeu, prof-1);
                    if((tmp>max)||((tmp==max)&&(Rand::randi(2)))){
                        max = tmp;
                        maxi = i;
                        maxj = j;
					}
				}
			}
	}
    jeu->joue(maxi,maxj);
}
int calcMin(Jeu *jeu, int prof){
    int i,j,tmp;
    int min = MAXEVAL;

    if(prof==0)
        return evalue(jeu);

    if(jeu->getFini())
        return evalue(jeu);

    for(i=0;i<10; i++){
        for(j=0;j<10;j++){
            if(jeu->estVide(i,j)){
                jeu->joue(i,j);
                tmp = calcMax(jeu, prof-1);
                if(tmp<min)
				{
                    min = tmp;
				} 
				annuleCoup(i,j);
			}
		}
		return min;
	}
}
void calcMax(Jeu *jeu, int prof){
	int max = MINIMUM;

	if(prof== 0)
		return x;
	else
		for(chaque coup possible){
			joue;
			tmp = calcMin(j, prof-1);
			if(tmp > max)
				max = tmp;
			annuleCoup(i,j);
		}
	  return max;
}