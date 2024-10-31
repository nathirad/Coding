#include <stdio.h>
#define ASIZE 10
int main(void) {
    int i, j, bars[ASIZE] = {19,3,16,7,11,10,13,15,4};
    for(i=20; i>0; i--){
        printf("%3d |", i);
        for(j=0; j<ASIZE; j++){
            if (bars[j] >= i){
            {
                printf(" %c ", 176);
            
            }else{
                printf(" ");
            }
            
            }


        }
            printf("\n");


    }
        printf("---------------\n");
        printf("        ");
        for(i=0; i < ASIZE; i++) {
            printf("%2d", bars[i]);

        }
    printf("/n");

}