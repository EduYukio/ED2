/****************************************************************
    Nome: Eduardo Yukio Rodrigues
    NUSP: 8988702

    Ao preencher esse cabeçalho com o meu nome e o meu número USP,
    declaro que todas as partes originais desse exercício programa (EP)
    foram desenvolvidas e implementadas por mim e que portanto não 
    constituem desonestidade acadêmica ou plágio.
    Declaro também que sou responsável por todas as cópias desse
    programa e que não distribui ou facilitei a sua distribuição.
    Estou ciente que os casos de plágio e desonestidade acadêmica
    serão tratados segundo os critérios divulgados na página da 
    disciplina.
    Entendo que EPs sem assinatura devem receber nota zero e, ainda
    assim, poderão ser punidos por desonestidade acadêmica.

    Abaixo descreva qualquer ajuda que você recebeu para fazer este
    EP.  Inclua qualquer ajuda recebida por pessoas (inclusive
    monitoras e colegas). Com exceção de material de MAC0323, caso
    você tenha utilizado alguma informação, trecho de código,...
    indique esse fato abaixo para que o seu programa não seja
    considerado plágio ou irregular.

    Exemplo:

        A monitora me explicou que eu devia utilizar a função xyz().

        O meu método xyz() foi baseada na descrição encontrada na 
        página https://www.ime.usp.br/~pf/algoritmos/aulas/enumeracao.html.

    Descrição de ajuda ou indicação de fonte:



    Se for o caso, descreva a seguir 'bugs' e limitações do seu programa:

****************************************************************/

/*
 * MAC0323 Algoritmos e Estruturas de Dados II
 * 
 * ADT Bag implementada com lista ligada de itens. 
 *  
 *    https://algs4.cs.princeton.edu/13stacks/
 *    https://www.ime.usp.br/~pf/estruturas-de-dados/aulas/bag.html
 * 
 * Os itens desta implementação são genéricos: "shallow copy" dos itens dados.
 *
 *----------------------------------------------------------------------------
 * Object copying
 * https://en.wikipedia.org/wiki/Object_copying
 * 
 * Understanding Deep and Shallow Copy 
 * https://we-are.bookmyshow.com/understanding-deep-and-shallow-copy-in-javascript-13438bad941c
 *
 * Shallow copy is a bit-wise copy of an object. A new object is created that has an 
 * exact copy of the values in the original object. If any of the fields of the object 
 * are references to other objects, just the reference addresses are copied i.e., 
 * only the memory address is copied.
 
 * A deep copy copies all fields, and makes copies of dynamically allocated memory 
 * pointed to by the fields. A deep copy occurs when an object is copied along with the 
 * objects to which it refers.
 *
 */

/* interface para o uso da funcao deste módulo */
#include "bag.h"  

#include <stdlib.h>  /* free() */
#include <string.h>  /* memcpy() */
#include "util.h"    /* emalloc() */

#undef DEBUG
#ifdef DEBUG
#include <stdio.h>   /* printf(): para debuging */
#endif

/*----------------------------------------------------------*/
/* 
 * Estrutura Básica da Bag
 * 
 * Implementação com listas ligada dos itens.
 */

struct bag {
	int n;
	struct node* first; 
	struct node* lastNodeReturned;
};

struct node {
	void* item;
	size_t nItem;
	struct node* next;
	struct node* prev;
};

/*------------------------------------------------------------*/
/* 
 * Protótipos de funções administrativas: tem modificador 'static'
 * 
 */

/*-----------------------------------------------------------*/
/*
 *  newBag()
 *
 *  RETORNA (referência/ponteiro para) uma bag vazia.
 * 
 */
Bag
newBag() {
	Bag emptyBag = ecalloc(1, sizeof(struct bag));
	emptyBag->n = 0;
  return emptyBag;
}

/*-----------------------------------------------------------*/
/*
 *  freeBag(BAG)
 *
 *  RECEBE uma Bag BAG e devolve ao sistema toda a memoria 
 *  utilizada.
 *
 */
void  
freeBag(Bag bag) {
	struct node* currentNode = bag->first;
	while(currentNode->next != NULL){
		currentNode = currentNode->next;
	}
	//invariante: currentNode é o último nó da lista

	while(currentNode != NULL){
		struct node* prevNode = currentNode->prev;
		free(currentNode->item);
		free(currentNode);
		currentNode = prevNode;
	}

	free(bag);
}    

/*------------------------------------------------------------*/
/*
 * OPERAÇÕES USUAIS: add(), size(), isEmpty() e itens().
 */

/*-----------------------------------------------------------*/
/*
 *  add(BAG, ITEM, NITEM)
 * 
 *  RECEBE uma bag BAG e um ITEM e insere o ITEM na BAG.
 *  NITEM é o número de bytes de ITEM.
 *
 *  Para criar uma copia/clone de ITEM é usado o seu número de bytes NITEM.
 *
 */
void  
add(Bag bag, const void *item, size_t nItem) {
	struct node* newNode = ecalloc(1, sizeof(struct node));

	void* itemClone = emalloc(nItem);
	memcpy(itemClone, item, nItem);
	newNode->item = itemClone;
	newNode->nItem = nItem;

	struct node* oldFirst = bag->first;
	if(!isEmpty(bag)){
		bag->first->prev = newNode;
	}

	bag->first = newNode;
	bag->first->next = oldFirst;
	bag->first->prev = NULL;

	bag->n++;
}

/*-----------------------------------------------------------*/
/* 
 *  SIZE(BAG)
 *
 *  RECEBE uma bag BAG
 * 
 *  RETORNA o número de itens em BAG.
 */
int
size(Bag bag) {
  return bag->n;
}

/*-----------------------------------------------------------*/
/* 
 *  ISEMPTY(BAG)
 *
 *  RECEBE uma bag BAG.
 * 
 *  RETORNA TRUE se BAG está vazia e FALSE em caso contrário.
 *
 */
Bool
isEmpty(Bag bag) {
  return bag->n == 0;
}

/*-----------------------------------------------------------*/
/* 
 *  ITENS(BAG, INIT)
 * 
 *  RECEBE uma bag BAG e um Bool INIT.
 *
 *  Se INIT é TRUE,  ITENS() RETORNA uma cópia/clone do primeiro item na lista de itens na BAG.
 *  Se INIT é FALSE, ITENS() RETORNA uma cópia/clone do item sucessor do último item retornado.
 *  Se BAG está vazia ou não há sucessor do último item retornada, ITENS() RETORNA NULL.
 *
 *  Se entre duas chamadas de ITENS() a BAG é alterada, o comportamento é  indefinido. 
 *  
 */
void *
itens(Bag bag, Bool init) {
  if(isEmpty(bag)){
  	return NULL;
  }

  if(init){
  	void* itemClone = emalloc(bag->first->nItem);
  	memcpy(itemClone, bag->first->item, bag->first->nItem);

  	bag->lastNodeReturned = bag->first;
  	return itemClone;
  }

  struct node* currentNode = bag->lastNodeReturned->next;
 	if(currentNode == NULL){
 		return NULL;
 	}

 	void* itemClone = emalloc(currentNode->nItem);
 	memcpy(itemClone, currentNode->item, currentNode->nItem);

 	bag->lastNodeReturned = currentNode;
 	return itemClone;
}

/*------------------------------------------------------------*/
/* 
 * Implementaçao de funções administrativas
 */

