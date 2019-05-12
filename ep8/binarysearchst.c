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

/***
 * MAC0323 Estruturas de Dados e Algoritmo II
 * 
 * Tabela de simbolos implementada atraves de vetores ordenados 
 * redeminsionaveis 
 *
 *     https://algs4.cs.princeton.edu/31elementary/BinarySearchST.java.html
 * 
 * As chaves e valores desta implementação são mais ou menos
 * genéricos
 */

/* interface para o uso da funcao deste módulo */
#include "binarysearchst.h"  

#include <stdlib.h>  /* free() */
#include <string.h>  /* memcpy() */
#include "util.h"    /* emalloc(), ecalloc() */

#undef DEBUG
#ifdef DEBUG
#include <stdio.h>   /* printf(): para debug */
#endif

/*
 * CONSTANTES 
 */

const int INIT_SIZE = 65536;
/*------------------------------------------------------------*/
/* 
 * Funções administrativas
 */
void resize(BinarySearchST st, int capacity);

void addToCleanup(BinarySearchST st, void *ptr);
/*----------------------------------------------------------*/
/* 
 * Estrutura Básica da Tabela de Símbolos: 
 * 
 * implementação com vetores ordenados
 */
struct binarySearchST {
  void **keys;
  void **vals;
  size_t *nKey;
  size_t *nVal;
  void **cleanUpArray;
  int cleanUpCount;
  int (*compar)(const void*,const void*);
  void* lastReturnedKey;
  size_t maxSTLength;
  int lastReturnedKeyIndex;
  int n;
};

/*-----------------------------------------------------------*/
/*
 *  initST(COMPAR)
 *
 *  RECEBE uma função COMPAR() para comparar chaves.
 *  RETORNA (referência/ponteiro para) uma tabela de símbolos vazia.
 *
 *  É esperado que COMPAR() tenha o seguinte comportamento:
 *
 *      COMPAR(key1, key2) retorna um inteiro < 0 se key1 <  key2
 *      COMPAR(key1, key2) retorna 0              se key1 == key2
 *      COMPAR(key1, key2) retorna um inteiro > 0 se key1 >  key2
 * 
 *  TODAS OS OPERAÇÕES da ST criada utilizam a COMPAR() para comparar
 *  chaves.
 * 
 */
BinarySearchST
initST(int (*compar)(const void *key1, const void *key2)) {
  BinarySearchST st = ecalloc(1, sizeof(struct binarySearchST));

  st->keys = ecalloc(INIT_SIZE, sizeof(void*));
  st->vals = ecalloc(INIT_SIZE, sizeof(void*));
  st->nKey = ecalloc(INIT_SIZE, sizeof(void*));
  st->nVal = ecalloc(INIT_SIZE, sizeof(void*));
  st->cleanUpArray = ecalloc(INIT_SIZE, sizeof(void*));
  st->cleanUpCount = 0;
  st->compar = compar;
  st->n = 0;
  st->maxSTLength = INIT_SIZE*sizeof(void*);

  return st;
}

/*-----------------------------------------------------------*/
/*
 *  freeST(ST)
 *
 *  RECEBE uma BinarySearchST  ST e devolve ao sistema toda a memoria 
 *  utilizada por ST.
 *
 */
void  
freeST(BinarySearchST st) {
  int i;
  for(i = 0; i < st->n; i++){
    free(st->keys[i]);
    free(st->vals[i]);
  }

  for(i = 0; i < st->cleanUpCount; i++){
    free(st->cleanUpArray[i]);
  }
  free(st->cleanUpArray);
  free(st->keys);
  free(st->vals);
  
  free(st->nKey);
  free(st->nVal);
  free(st);
  
}

/*------------------------------------------------------------*/
/*
 * OPERAÇÕES USUAIS: put(), get(), contains(), delete(),
 * size() e isEmpty().
 */

/*-----------------------------------------------------------*/
/*
 *  put(ST, KEY, NKEY, VAL, NVAL)
 * 
 *  RECEBE a tabela de símbolos ST e um par KEY-VAL e procura a KEY na ST.
 *
 *     - se VAL é NULL, a entrada da chave KEY é removida da ST  
 *  
 *     - se KEY nao e' encontrada: o par KEY-VAL é inserido na ST
 *
 *     - se KEY e' encontra: o valor correspondente é atualizado
 *
 *  NKEY é o número de bytes de KEY e NVAL é o número de bytes de NVAL.
 *
 *  Para criar uma copia/clone de KEY é usado o seu número de bytes NKEY.
 *  Para criar uma copia/clode de VAL é usado o seu número de bytes NVAL.
 *
 */
void  
put(BinarySearchST st, const void *key, size_t nKey, const void *val, size_t nVal) {
  int i = 0;
  int j;
  void *keyClone;
  void *valClone;
  void *keyTemp;
  void *valTemp;

  if (val == NULL) {
    delete(st, key);
    return;
  }

  if(!isEmpty(st)){
    i = rank(st, key);
  }

  /* update val if the key already exists */ 
  if (i < st->n && st->compar(st->keys[i], key) == 0) {
    valClone = emalloc(nVal);
    memcpy(valClone, val, nVal);
    free(st->vals[i]);
    st->vals[i] = valClone;
    return;
  }

  if (st->n == st->maxSTLength){
    resize(st, 2*st->maxSTLength);  
  }

  for (j = st->n; j > i; j--) {
    if(j == i + 1){
      keyTemp = emalloc(st->nKey[i]);
      memcpy(keyTemp, st->keys[i], st->nKey[i]);
      st->keys[j] = keyTemp;

      valTemp = emalloc(st->nVal[i]);
      memcpy(valTemp, st->vals[i], st->nVal[i]);
      st->vals[j] = valTemp;
    } else{
      st->keys[j] = st->keys[j-1];
      st->vals[j] = st->vals[j-1];
    }
    st->nKey[j] = st->nKey[j-1];
    st->nVal[j] = st->nVal[j-1];
  }
  
  keyClone = emalloc(nKey);
  memcpy(keyClone, key, nKey);
  free(st->keys[i]);
  st->keys[i] = keyClone;

  valClone = emalloc(nVal);
  memcpy(valClone, val, nVal);
  free(st->vals[i]);
  st->vals[i] = valClone;

  st->nKey[i] = nKey;
  st->nVal[i] = nVal;

  st->n++;
}

/*-----------------------------------------------------------*/
/*
 *  get(ST, KEY)
 *
 *  RECEBE uma tabela de símbolos ST e uma chave KEY.
 *
 *     - se KEY está em ST, RETORNA NULL;
 *
 *     - se KEY não está em ST, RETORNA uma cópia/clone do valor
 *       associado a KEY.
 * 
 */
void *
get(BinarySearchST st, const void *key) {
  int i;

  if (isEmpty(st)){
    return NULL;
  }
  
  i = rank(st, key);
  if (i < st->n && st->compar(st->keys[i], key) == 0){
    void *valClone = emalloc(st->nVal[i]);
    memcpy(valClone, st->vals[i], st->nVal[i]);

    return valClone;
  }

  return NULL;
}

/*-----------------------------------------------------------*/
/* 
 *  CONTAINS(ST, KEY)
 *
 *  RECEBE uma tabela de símbolos ST e uma chave KEY.
 * 
 *  RETORNA TRUE se KEY está na ST e FALSE em caso contrário.
 *
 */
Bool
contains(BinarySearchST st, const void *key) {
  void *searchKey = get(st, key);

  if(searchKey == NULL) {
    return FALSE;
  }
    
  return TRUE;
}

/*-----------------------------------------------------------*/
/* 
 *  DELETE(ST, KEY)
 *
 *  RECEBE uma tabela de símbolos ST e uma chave KEY.
 * 
 *  Se KEY está em ST, remove a entrada correspondente a KEY.
 *  Se KEY não está em ST, faz nada.
 *
 */
void
delete(BinarySearchST st, const void *key) {
  int i;
  int j;

  if (isEmpty(st)){
    return;
  }

  i = rank(st, key);

  /* key not in table */
  if (i == st->n || st->compar(st->keys[i], key) != 0) {
      return;
  }

  free(st->keys[i]);
  free(st->vals[i]);
  
  for (j = i; j < st->n-1; j++) {
    st->keys[j] = st->keys[j+1];
    st->vals[j] = st->vals[j+1];
    st->nKey[j] = st->nKey[j+1];
    st->nVal[j] = st->nVal[j+1];
  }

  st->keys[st->n] = NULL;
  st->vals[st->n] = NULL;

  st->n--;

  /* resize if 1/4 full */
  if (st->n > 0){
    if(st->n == st->maxSTLength/4){
      resize(st, st->maxSTLength/2);
    }
  }
}

/*-----------------------------------------------------------*/
/* 
 *  SIZE(ST)
 *
 *  RECEBE uma tabela de símbolos ST.
 * 
 *  RETORNA o número de itens (= pares chave-valor) na ST.
 *
 */
int
size(BinarySearchST st) {
  return st->n;
}

/*-----------------------------------------------------------*/
/* 
 *  ISEMPTY(ST, KEY)
 *
 *  RECEBE uma tabela de símbolos ST.
 * 
 *  RETORNA TRUE se ST está vazia e FALSE em caso contrário.
 *
 */
Bool
isEmpty(BinarySearchST st) {
  if(st->n == 0){
    return TRUE;
  }

  return FALSE;
}


/*------------------------------------------------------------*/
/*
 * OPERAÇÕES PARA TABELAS DE SÍMBOLOS ORDENADAS: 
 * min(), max(), rank(), select(), deleteMin() e deleteMax().
 */

/*-----------------------------------------------------------*/
/*
 *  MIN(ST)
 * 
 *  RECEBE uma tabela de símbolos ST e RETORNA uma cópia/clone
 *  da menor chave na tabela.
 *
 *  Se ST está vazia RETORNA NULL.
 *
 */
void *
min(BinarySearchST st) {
  void *keyClone;

  if(isEmpty(st)){
    return NULL;
  }

  keyClone = emalloc(st->nKey[0]);
  memcpy(keyClone, st->keys[0], st->nKey[0]);

  return keyClone;
}

/*-----------------------------------------------------------*/
/*
 *  MAX(ST)
 * 
 *  RECEBE uma tabela de símbolos ST e RETORNA uma cópia/clone
 *  da maior chave na tabela.
 *
 *  Se ST está vazia RETORNA NULL.
 *
 */
void *
max(BinarySearchST st) {
  void *keyClone;

  if(isEmpty(st)){
    return NULL;
  }

  keyClone = emalloc(st->nKey[st->n-1]);
  memcpy(keyClone, st->keys[st->n-1], st->nKey[st->n-1]);
  
  return keyClone;
}

/*-----------------------------------------------------------*/
/*
 *  RANK(ST, KEY)
 * 
 *  RECEBE uma tabela de símbolos ST e uma chave KEY.
 *  RETORNA o número de chaves em ST menores que KEY.
 *
 *  Se ST está vazia RETORNA NULL.
 *
 */
int
rank(BinarySearchST st, const void *key) {
  int lo,hi,mid,cmp;
  
  if(isEmpty(st)){
    return EXIT_FAILURE;
  }
  
  lo = 0;
  hi = st->n-1;
  while (lo <= hi) {
    mid = lo + (hi - lo) / 2;
    cmp = st->compar(key, st->keys[mid]);
    if      (cmp < 0) hi = mid - 1;
    else if (cmp > 0) lo = mid + 1;
    else return mid;
  }
  return lo;
} 

/*-----------------------------------------------------------*/
/*
 *  SELECT(ST, K)
 * 
 *  RECEBE uma tabela de símbolos ST e um inteiro K >= 0.
 *  RETORNA a (K+1)-ésima menor chave da tabela ST.
 *
 *  Se ST não tem K+1 elementos RETORNA NULL.
 *
 */
void *
select(BinarySearchST st, int k) {
  void *keyClone;

  if(st->n < k + 1){
    return NULL;
  }

  keyClone = emalloc(st->nKey[k]);
  memcpy(keyClone, st->keys[k], st->nKey[k]);
  
  return keyClone;
}

/*-----------------------------------------------------------*/
/*
 *  deleteMIN(ST)
 * 
 *  RECEBE uma tabela de símbolos ST e remove a entrada correspondente
 *  à menor chave.
 *
 *  Se ST está vazia, faz nada.
 *
 */
void
deleteMin(BinarySearchST st) {
  if(!isEmpty(st)){
    delete(st, st->keys[0]);
  }
}

/*-----------------------------------------------------------*/
/*
 *  deleteMAX(ST)
 * 
 *  RECEBE uma tabela de símbolos ST e remove a entrada correspondente
 *  à maior chave.
 *
 *  Se ST está vazia, faz nada.
 *
 */
void
deleteMax(BinarySearchST st) {
  if(!isEmpty(st)){
    delete(st, st->keys[st->n-1]);
  }
}

/*-----------------------------------------------------------*/
/* 
 *  KEYS(ST, INIT)
 * 
 *  RECEBE uma tabela de símbolos ST e um Bool INIT.
 *
 *  Se INIT é TRUE, KEYS() RETORNA uma cópia/clone da menor chave na ST.
 *  Se INIT é FALSE, KEYS() RETORNA a chave sucessora da última chave retornada.
 *  Se ST está vazia ou não há sucessora da última chave retornada, KEYS() RETORNA NULL.
 *
 *  Se entre duas chamadas de KEYS() a ST é alterada, o comportamento é 
 *  indefinido. 
 *  
 */
void * 
keys(BinarySearchST st, Bool init) {
  if(isEmpty(st)){
    return NULL;
  }

  if(st->lastReturnedKeyIndex == st->n-1){
    st->lastReturnedKeyIndex = 0;
    return NULL;
  }

  if(init == TRUE){
    st->lastReturnedKey = min(st);
    st->lastReturnedKeyIndex = 0;
  } else {
    st->lastReturnedKeyIndex += 1;
    st->lastReturnedKey = select(st, st->lastReturnedKeyIndex);
  }

  return st->lastReturnedKey;
}

/*-----------------------------------------------------------*/
/*
  Visit each entry on the ST.

  The VISIT function is called, in-order, with each pair key-value in the ST.
  If the VISIT function returns zero, then the iteration stops.

  visitST returns zero if the iteration was stopped by the visit function,
  nonzero otherwise.
*/
int
visitST(BinarySearchST st, int (*visit)(const void *key, const void *val)) {
  void *returnedKey;
  void *respectiveVal;
  int returnFromVisit;
  if(isEmpty(st)){
    return -1;
  }

  returnedKey = keys(st, TRUE);
  addToCleanup(st, returnedKey);

  respectiveVal = st->vals[st->lastReturnedKeyIndex];
  returnFromVisit = visit(returnedKey, respectiveVal);

  while(returnFromVisit != 0 && st->lastReturnedKeyIndex <= st->n-2){
    returnedKey = keys(st, FALSE);
    addToCleanup(st, returnedKey);

    respectiveVal = st->vals[st->lastReturnedKeyIndex];
    returnFromVisit = visit(returnedKey, respectiveVal);
  }
  
  st->lastReturnedKeyIndex = 0;
  if(returnFromVisit==0){
    return 0;
  }

  return -1;
}


/*------------------------------------------------------------*/
/* 
 * Funções administrativas
 */

/* resize the underlying arrays */
void resize(BinarySearchST st, int capacity) {
  void **tempk = ecalloc(capacity, sizeof(void*));
  void **tempv = ecalloc(capacity, sizeof(void*));
  size_t *tempNk = ecalloc(capacity, sizeof(void*));
  size_t *tempNv = ecalloc(capacity, sizeof(void*));
  int i;

  for (i = 0; i < st->n; i++) {
    tempk[i] = st->keys[i];
    tempv[i] = st->vals[i];
    tempNk[i] = st->nKey[i];
    tempNv[i] = st->nVal[i];
  }
  st->keys = tempk;
  st->vals = tempv;
  st->nKey = tempNk;
  st->nVal = tempNv;

  st->maxSTLength = capacity;
}

/* add ptr to the cleanUp array, which free all the
extra pointers at the end of the execution */
void addToCleanup(BinarySearchST st, void *ptr){
  if(st->cleanUpCount == st->maxSTLength){
    int i;
    void **temp = ecalloc(2*st->cleanUpCount, sizeof(void*));
    for (i = 0; i < st->n; i++) {
      temp[i] = st->cleanUpArray[i];
    }
    st->cleanUpArray = temp;
  }
  st->cleanUpArray[st->cleanUpCount] = ptr;
  st->cleanUpCount++;
}